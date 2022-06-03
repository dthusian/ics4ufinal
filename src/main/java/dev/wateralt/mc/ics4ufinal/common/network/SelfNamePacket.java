package dev.wateralt.mc.ics4ufinal.common.network;

import dev.wateralt.mc.ics4ufinal.common.Util;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class SelfNamePacket implements Packet {
  String name;

  public SelfNamePacket() {

  }

  @Override
  public int getId() {
    return 100;
  }

  @Override
  public ByteBuffer serializeInternal() {
    byte[] bytes = name.getBytes(StandardCharsets.UTF_8);
    ByteBuffer outBytes = ByteBuffer.allocate(4 + bytes.length);
    outBytes.putInt(bytes.length);
    outBytes.put(bytes);
    return outBytes;
  }

  @Override
  public void deserializeInternal(ByteBuffer buf) {
    name = Util.byteBufReadString(buf, 0);
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
