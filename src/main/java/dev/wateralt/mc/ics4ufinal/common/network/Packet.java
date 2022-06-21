package dev.wateralt.mc.ics4ufinal.common.network;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * <h2>Networking</h2>
 * The core of networking here is the Packet class. Each packet type inherits
 * Packet, and they need to be able to serialize and deserialize themselves.
 * Unfortunately, without reflection, it's difficult to generically match
 * packet types to packet classes, so the deserialize method is rather
 * long.
 *
 * <pre>
 * Packet format:
 * - Always big-endian
 *
 * i32 -> Packet length (not including this header)
 * i32 -> Packet type
 * bytes -> Packet data
 *
 * Packet types:
 * 1xx: Connection negotiation and game setup
 * 2xx: Normal gameplay
 * 3xx: Errors and disconnections
 * </pre>
 */
public abstract class Packet {
  /**
   * Returns the integer type of this packet
   * @return The integer type of this packet
   */
  public abstract int getId();

  /**
   * Serializes the packet into a bytebuffer.  Don't call directly, use Packet.serialize which wraps the raw bytes with a length and type.
   */
  protected abstract ByteBuffer serializeInternal();

  /**
   * @param buf The buffer to deserialize. The contents will be assigned to this object.
   */
  protected abstract void deserializeInternal(ByteBuffer buf);

  /**
   * Serializes a packet into a ByteBuffer
   * @param p The packet to serialize
   * @return The serialized bytes
   */
  public static ByteBuffer serialize(Packet p) {
    ByteBuffer noHeader = p.serializeInternal();
    noHeader.rewind();
    ByteBuffer newBuf = ByteBuffer.allocate(noHeader.capacity() + 8);
    newBuf.putInt(noHeader.capacity());
    newBuf.putInt(p.getId());
    newBuf.put(noHeader);
    return newBuf;
  }

  /**
   * Deserializes a packet from bytes.
   * @param bytes The bytes representing the packet
   * @return The packet represented by the bytes.
   */
  public static Packet deserialize(ByteBuffer bytes) {
    bytes.order(ByteOrder.BIG_ENDIAN);
    int length = bytes.getInt(0);
    int type = bytes.getInt(4);
    if(bytes.capacity() != length + 8) {
      throw new RuntimeException("Length mismatch; possibly truncated buffer");
    }
    Packet p;
    // No better way to do this without reflection
    // And I don't want to use reflection
    if(type == SelfNamePacket.ID) {
      p = new SelfNamePacket();
      p.deserializeInternal(bytes.slice(8, length));
    } else if(type == DiscardTilePacket.ID) {
      p = new DiscardTilePacket();
      p.deserializeInternal(bytes.slice(8, length));
    } else if(type == UndiscardTilePacket.ID) {
      p = new UndiscardTilePacket();
      p.deserializeInternal(bytes.slice(8, length));
    } else if(type == UpdateHandPacket.ID) {
      p = new UpdateHandPacket();
      p.deserializeInternal(bytes.slice(8, length));
    } else if(type == NoActionPacket.ID) {
      p = new NoActionPacket();
      p.deserializeInternal(bytes.slice(8, length)); // This LOC is the most useless one in the codebase
    } else if(type == PonPacket.ID) {
      p = new PonPacket();
      p.deserializeInternal(bytes.slice(8, length));
    } else if(type == ChiPacket.ID) {
      p = new ChiPacket();
      p.deserializeInternal(bytes.slice(8, length));
    } else if(type == WinPacketC2S.ID) {
      p = new WinPacketC2S();
      p.deserializeInternal(bytes.slice(8, length));
    } else if(type == WinPacketS2C.ID) {
      p = new WinPacketS2C();
      p.deserializeInternal(bytes.slice(8, length));
    } else {
      return null;
    }
    return p;
  }
}

/*
200 -> DiscardTilePacket
201 -> UndiscardTilePacket
202 -> UpdateHandPacket
203 -> NoActionPacket
204 -> PonPacket
205 -> ChiPacket
206 -> WinPacketC2S
207 -> WinPacketS2C
*/
