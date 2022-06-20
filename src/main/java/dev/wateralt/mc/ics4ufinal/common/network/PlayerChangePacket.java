package dev.wateralt.mc.ics4ufinal.common.network;

import java.nio.ByteBuffer;

public class PlayerChangePacket extends Packet {
  public static int ID = 101;

  int nPlayers;

  public PlayerChangePacket() {
    nPlayers = 0;
  }

  public PlayerChangePacket(int np) {
    nPlayers = np;
  }

  @Override
  public int getId() {
    return ID;
  }

  @Override
  protected ByteBuffer serializeInternal() {
    ByteBuffer buf = ByteBuffer.allocate(4);
    buf.putInt(nPlayers);
    return null;
  }

  @Override
  protected void deserializeInternal(ByteBuffer buf) {
    nPlayers = buf.getInt(0);
  }
}
