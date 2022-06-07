package dev.wateralt.mc.ics4ufinal.server.controllers;

import dev.wateralt.mc.ics4ufinal.common.MahjongTile;
import dev.wateralt.mc.ics4ufinal.common.network.DiscardTilePacket;
import dev.wateralt.mc.ics4ufinal.common.network.GainTilePacket;
import dev.wateralt.mc.ics4ufinal.common.network.NoActionPacket;
import dev.wateralt.mc.ics4ufinal.common.network.Packet;
import dev.wateralt.mc.ics4ufinal.server.MahjongGame;

public class NullController implements Controller {
  int handSize;
  int playerId;
  MahjongTile lastTile;
  Packet nextPacket;

  @Override
  public void initialize(MahjongGame state, int playerId) {
    this.playerId = playerId;
  }

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

  @Override
  public Packet receive(int timeout) {
    Packet ret = nextPacket;
    nextPacket = new NoActionPacket();
    return ret;
  }
}
