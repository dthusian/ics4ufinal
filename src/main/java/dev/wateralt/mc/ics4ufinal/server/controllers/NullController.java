package dev.wateralt.mc.ics4ufinal.server.controllers;

import dev.wateralt.mc.ics4ufinal.common.MahjongTile;
import dev.wateralt.mc.ics4ufinal.common.network.DiscardTilePacket;
import dev.wateralt.mc.ics4ufinal.common.network.NoActionPacket;
import dev.wateralt.mc.ics4ufinal.common.network.Packet;
import dev.wateralt.mc.ics4ufinal.common.network.UpdateHandPacket;
import dev.wateralt.mc.ics4ufinal.server.MahjongGame;

/**
 * Dummy controller that only knows the most basic rules of mahjong.
 * Controllers are replaced by the dummy controller if they disconnect.
 */
public class NullController implements Controller {
  int playerId;
  MahjongTile lastTile;
  Packet nextPacket = new NoActionPacket();

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
    if(packet.getId() == UpdateHandPacket.ID) {
      UpdateHandPacket pDerived = (UpdateHandPacket) packet;
      if(pDerived.getPlayerId() == playerId) {
        if(pDerived.getHand().getLength() == 14) {
          nextPacket = new DiscardTilePacket(pDerived.getHand().getHidden().get(0), playerId);
        }
      }
    } else if(packet.getId() == DiscardTilePacket.ID) {
      DiscardTilePacket pDerived = (DiscardTilePacket) packet;
      nextPacket = new NoActionPacket();
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
    try {
      if(ret.getId() == NoActionPacket.ID) Thread.sleep(250);
      else Thread.sleep(1000);
    } catch(InterruptedException ignored) {
    }
    return ret;
  }
}
