package dev.wateralt.mc.ics4ufinal.common.network;

import dev.wateralt.mc.ics4ufinal.common.MahjongTile;

import java.nio.ByteBuffer;

public class GainTilePacket extends BasicTilePacket {
  public static final int ID = 200;
  public GainTilePacket() { super(); }
  public GainTilePacket(MahjongTile tile, int player) { super(tile, player); }
  @Override
  public int getId() {
    return ID;
  }
}
