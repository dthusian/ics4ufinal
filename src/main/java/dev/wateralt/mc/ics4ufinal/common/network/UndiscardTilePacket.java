package dev.wateralt.mc.ics4ufinal.common.network;

import java.nio.ByteBuffer;

public class UndiscardTilePacket extends Packet {
  public static final int ID = 201;

  int playerId;

  public UndiscardTilePacket() {
    playerId = 0;
  }

  public UndiscardTilePacket(int playerId) {
    this.playerId = playerId;
  }

  public int getPlayerId() {
    return playerId;
  }

  @Override
  public int getId() {
    return ID;
  }

  @Override
  protected ByteBuffer serializeInternal() {
    ByteBuffer buf = ByteBuffer.allocate(4);
    buf.putInt(playerId);
    return buf;
  }

  @Override
  protected void deserializeInternal(ByteBuffer buf) {
    playerId = buf.getInt(0);
  }
}
