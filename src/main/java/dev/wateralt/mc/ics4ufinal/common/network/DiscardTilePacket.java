package dev.wateralt.mc.ics4ufinal.common.network;

import dev.wateralt.mc.ics4ufinal.common.MahjongTile;

import java.nio.ByteBuffer;

/**
 * Packet that is emitted by a client to indicate that they would like to discard a tile,
 * or by the server to indicate that a client has discarded a tile.
 */
public class DiscardTilePacket extends BasicTilePacket {
  public static final int ID = 201;

  /**
   * Alias of BasicTilePacket constructor
   */
  public DiscardTilePacket() { super(); }

  /**
   * Alias of BasicTilePacket constructor
   * @param tile
   * @param player
   */
  public DiscardTilePacket(MahjongTile tile, int player) { super(tile, player); }

  /**
   *
   * @return The ID of the packet type.
   */
  @Override
  public int getId() {
    return ID;
  }
}
