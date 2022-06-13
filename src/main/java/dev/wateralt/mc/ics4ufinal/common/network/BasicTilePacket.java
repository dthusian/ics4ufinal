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

  /**
   *
   * @return
   */
  @Override
  public ByteBuffer serializeInternal() {
    ByteBuffer buf = ByteBuffer.allocate(8);
    buf.putInt(tile.getInternal());
    buf.putInt(player);
    return buf;
  }

  /**
   *
   * @param buf The buffer to deserialize. The contents will be assigned to this object.
   */
  @Override
  public void deserializeInternal(ByteBuffer buf) {
    tile = new MahjongTile(buf.getInt());
    player = buf.getInt();
  }

  /**
   *
   * @return The tile inside of this packet.
   */
  public MahjongTile getTile() {
    return tile;
  }

  /**
   *
   * @param tile The tile to set to.
   */
  public void setTile(MahjongTile tile) {
    this.tile = tile;
  }

  /**
   *
   * @return The player index inside of this packet
   */
  public int getPlayer() {
    return player;
  }

  /**
   *
   * @param player The player to set to.
   */
  public void setPlayer(int player) {
    this.player = player;
  }
}
