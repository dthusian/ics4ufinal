package dev.wateralt.mc.ics4ufinal.common.network;

import dev.wateralt.mc.ics4ufinal.common.Util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/*
Packet format:
- Always big-endian

i32 -> Packet length (not including this header)
i32 -> Packet type
bytes -> Packet data

Packet types:
1xx: Connection negotiation and game setup
2xx: Normal gameplay, S->C
3xx: Normal gameplay, C->S and S->C
4xx: Errors and disconnections
*/

public interface Packet {
  int getId();
  ByteBuffer serializeInternal();
  void deserializeInternal(ByteBuffer buf);

  static ByteBuffer serialize(Packet p) {
    ByteBuffer noHeader = p.serializeInternal();
    ByteBuffer newBuf = ByteBuffer.allocate(noHeader.capacity() + 8);
    newBuf.putInt(noHeader.capacity());
    newBuf.putInt(p.getId());
    newBuf.put(noHeader);
    return newBuf;
  }

  static Packet deserialize(ByteBuffer bytes) {
    bytes.order(ByteOrder.BIG_ENDIAN);
    int length = bytes.getInt(0);
    int type = bytes.getInt(4);
    if(bytes.capacity() != length + 8) {
      throw new RuntimeException("Length mismatch; possibly truncated buffer");
    }
    Packet p;
    if(type == new SelfNamePacket().getId()) {
      p = new SelfNamePacket();
      p.deserializeInternal(bytes.slice(8, length));
    } else {
      return null;
    }
    return p;
  }
}
