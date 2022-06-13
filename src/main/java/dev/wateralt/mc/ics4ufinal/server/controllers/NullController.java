package dev.wateralt.mc.ics4ufinal.server.controllers;

import dev.wateralt.mc.ics4ufinal.common.MahjongTile;
import dev.wateralt.mc.ics4ufinal.common.network.DiscardTilePacket;
import dev.wateralt.mc.ics4ufinal.common.network.GainTilePacket;
import dev.wateralt.mc.ics4ufinal.common.network.NoActionPacket;
import dev.wateralt.mc.ics4ufinal.common.network.Packet;
import dev.wateralt.mc.ics4ufinal.server.MahjongGame;

/**
 * Dummy controller that only knows the most basic rules of mahjong.
 * Controllers are replaced by the dummy controller if they disconnect.
 */
public class NullController implements Controller {
  int handSize;
  int playerId;
  MahjongTile lastTile;
  Packet nextPacket;

  /**
   *
   * @param state The game state.
   * @param playerId The index of the player that this controller controls.
   */
  @Override
  public void initialize(MahjongGame state, int playerId) {
    this.playerId = playerId;
  }

  /**
   *
   * @param packet The packet to send.
   */
  @Override
  public void send(Packet packet) {
    if(packet.getId() == GainTilePacket.ID) {
      handSize++;
      lastTile = ((GainTilePacket)packet).getTile();
      if(handSize == 14) {
        nextPacket = new DiscardTilePacket(lastTile, playerId);
      }
    }
  }

  /**
   *
   * @param timeout The time to wait for incoming moves, in milliseconds.
   * @return
   */
  @Override
  public Packet receive(int timeout) {
    Packet ret = nextPacket;
    nextPacket = new NoActionPacket();
    return ret;
  }
}
