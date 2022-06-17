package dev.wateralt.mc.ics4ufinal.common.network;

import dev.wateralt.mc.ics4ufinal.common.MahjongTile;

import java.nio.ByteBuffer;

public class ChiPacket extends Packet {
  public static final int ID = 205;

  MahjongTile[] tiles;
  int player;

  public ChiPacket() {
    tiles = new MahjongTile[0];
  }

  public ChiPacket(MahjongTile[] tiles, int player) {
    this.tiles = tiles;
    this.player = player;
  }

  @Override
  public int getId() {
    return 0;
  }

  @Override
  protected ByteBuffer serializeInternal() {
    ByteBuffer buf = ByteBuffer.allocate(16);
    buf.putInt(tiles[0].getInternal());
    buf.putInt(tiles[1].getInternal());
    buf.putInt(tiles[2].getInternal());
    buf.putInt(player);
    return buf;
  }

  @Override
  protected void deserializeInternal(ByteBuffer buf) {
    tiles = new MahjongTile[] {
        new MahjongTile(buf.getInt(0)),
        new MahjongTile(buf.getInt(4)),
        new MahjongTile(buf.getInt(8)),
    };
    player = buf.getInt(12);
  }

  public int getPlayer() {
    return player;
  }

  public MahjongTile[] getTiles() {
    return tiles;
  }
}
