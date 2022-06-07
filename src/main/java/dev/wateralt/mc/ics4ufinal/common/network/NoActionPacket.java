package dev.wateralt.mc.ics4ufinal.common.network;

import java.nio.ByteBuffer;

public class NoActionPacket extends Packet {
  public static int ID = 202;

  public NoActionPacket() { }

  @Override
  public int getId() {
    return ID;
  }

  @Override
  public ByteBuffer serializeInternal() {
    return ByteBuffer.allocate(0);
  }

  @Override
  public void deserializeInternal(ByteBuffer buf) { }
}
