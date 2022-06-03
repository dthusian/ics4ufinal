package dev.wateralt.mc.ics4ufinal.common.network;

import dev.wateralt.mc.ics4ufinal.common.MahjongTile;

import java.nio.ByteBuffer;

public class GainTilePacket implements Packet {
  MahjongTile tile;
  int player;

  public GainTilePacket() {
    tile = MahjongTile.NULL;
    player = 0;
  }

  public GainTilePacket(MahjongTile tile, int player) {
    this.tile = tile;
    this.player = player;
  }

  @Override
  public int getId() {
    return 200;
  }

  @Override
  public ByteBuffer serializeInternal() {
    ByteBuffer buf = ByteBuffer.allocate(8);
    buf.putInt(tile.getInternal());
    buf.putInt(player);
    return buf;
  }

  @Override
  public void deserializeInternal(ByteBuffer buf) {
    tile = new MahjongTile(buf.getInt());
    player = buf.getInt();
  }

  public MahjongTile getTile() {
    return tile;
  }

  public void setTile(MahjongTile tile) {
    this.tile = tile;
  }
}
