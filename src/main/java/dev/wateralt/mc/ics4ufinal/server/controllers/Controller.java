package dev.wateralt.mc.ics4ufinal.server.controllers;

import dev.wateralt.mc.ics4ufinal.common.network.Packet;

public interface Controller {
  void sendMove(Packet packet);
  Packet recvMove(int timeout);
}
