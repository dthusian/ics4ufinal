package dev.wateralt.mc.ics4ufinal.server.controllers;

import dev.wateralt.mc.ics4ufinal.common.Util;
import dev.wateralt.mc.ics4ufinal.common.network.Packet;
import dev.wateralt.mc.ics4ufinal.common.network.SelfNamePacket;
import dev.wateralt.mc.ics4ufinal.server.MahjongGame;

import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.Queue;

public class NetworkPlayerController implements Controller {
  final Socket sock;
  final Thread transferThread;
  MahjongGame.PlayerState playerState;
  final Queue<Packet> turnsPacketQueue;

  public NetworkPlayerController(Socket tcpStream) {
    sock = tcpStream;
    turnsPacketQueue = new ArrayDeque<>();
    transferThread = new Thread(this::threadMain);
    transferThread.start();
  }

  private void threadMain() {
    while(true) {
      try {
        Packet p = Packet.deserialize(Util.readSinglePacket(sock.getInputStream()));
        handlePacket(p);
      } catch(IOException ignored) {
        break;
      }
    }
  }

  private void handlePacket(Packet p) throws IOException {
    if(p == null) {
      throw new IOException();
    } else if(p.getId() == SelfNamePacket.ID) {
      synchronized (playerState) {
        playerState.setName(((SelfNamePacket)p).getName());
      }
    } else {
      turnsPacketQueue.add(p);
    }
  }

  @Override
  public void initialize(MahjongGame state, int playerId) {
    playerState = state.getPlayer(playerId);
  }

  @Override
  public void send(Packet packet) {
    try {
      sock.getOutputStream().write(Packet.serialize(packet).array());
    } catch(IOException ignored) {
      // Assume sends are infallible (reads will fail anyway if the connection is down)
    }
  }

  @Override
  public Packet receive(int timeout) {
    try {
      for(int i = 0; i < 10; i++) {
        Packet p;
        synchronized (turnsPacketQueue) {
          p = turnsPacketQueue.poll();
        }
        if(p != null) return p;
        Thread.sleep(timeout / 10);
      }
    } catch (InterruptedException ignored) {
    }
    // TODO Return default
    throw new UnsupportedOperationException();
  }
}
