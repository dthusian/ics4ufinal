package dev.wateralt.mc.ics4ufinal.common.network;

import dev.wateralt.mc.ics4ufinal.common.MahjongTile;

import java.nio.ByteBuffer;

/**
 * Not a real packet type, but a common base class for any packet that includes a tile and a player,
 * which is surprisingly common.
 */
public abstract class BasicTilePacket extends Packet {
  MahjongTile tile;
  int player;

  /**
   * Constructs a default packet.
   */
  public BasicTilePacket() {
    tile = MahjongTile.NULL;
    player = 0;
  }

  /**
   * Constructs a packet with the specified tile and player index
   * @param tile The tile
   * @param player The player index
   */
  public BasicTilePacket(MahjongTile tile, int player) {
    this.tile = tile;
    this.player = player;
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

  public int getPlayer() {
    return player;
  }

  public void setPlayer(int player) {
    this.player = player;
  }
}
