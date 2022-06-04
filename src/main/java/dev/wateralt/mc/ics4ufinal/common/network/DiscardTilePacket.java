package dev.wateralt.mc.ics4ufinal.common.network;

import dev.wateralt.mc.ics4ufinal.common.MahjongTile;

import java.nio.ByteBuffer;

public class DiscardTilePacket extends BasicTilePacket {
  public static final int ID = 201;

  public DiscardTilePacket() { super(); }
  public DiscardTilePacket(MahjongTile tile, int player) { super(tile, player); }
  @Override
  public int getId() {
    return ID;
  }
}
