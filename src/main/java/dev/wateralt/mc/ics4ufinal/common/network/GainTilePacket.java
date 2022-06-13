package dev.wateralt.mc.ics4ufinal.common.network;

import dev.wateralt.mc.ics4ufinal.common.MahjongTile;

import java.nio.ByteBuffer;

/**
 * Packet that is broadcast to all clients to indicate a specific client has recieved a tile.
 */
public class GainTilePacket extends BasicTilePacket {
  public static final int ID = 200;

  /**
   * Alias of BasicTilePacket constructor
   */
  public GainTilePacket() { super(); }

  /**
   * Alias of BasicTilePacket constructor
   * @param tile
   * @param player
   */
  public GainTilePacket(MahjongTile tile, int player) { super(tile, player); }

  /**
   *
   * @return The ID of the packet type.
   */
  @Override
  public int getId() {
    return ID;
  }
}
