package dev.wateralt.mc.ics4ufinal.server.controllers;

import dev.wateralt.mc.ics4ufinal.common.network.Packet;
import dev.wateralt.mc.ics4ufinal.server.MahjongGame;

public interface Controller {
  void initialize(MahjongGame.PlayerState state);
  void send(Packet packet);
  Packet receive(int timeout);
}
