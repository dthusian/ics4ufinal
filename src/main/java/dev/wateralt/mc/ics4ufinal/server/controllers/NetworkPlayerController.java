package dev.wateralt.mc.ics4ufinal.server.controllers;

import dev.wateralt.mc.ics4ufinal.common.Logger;
import dev.wateralt.mc.ics4ufinal.common.Util;
import dev.wateralt.mc.ics4ufinal.common.network.Packet;
import dev.wateralt.mc.ics4ufinal.common.network.SelfNamePacket;
import dev.wateralt.mc.ics4ufinal.server.MahjongGame;

import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Represents a player connected over the network. Maintains a thread for sending and receiving data.
 */
public class NetworkPlayerController implements Controller {
  final Socket sock;
  final Thread transferThread;
  MahjongGame.PlayerState playerState;
  final Queue<Packet> turnsPacketQueue;
  Logger logs;

  /**
   * Constructs new controller with a socket.
   * @param tcpStream The socket.
   */
  public NetworkPlayerController(Socket tcpStream) {
    sock = tcpStream;
    logs = new Logger(this);
    turnsPacketQueue = new ArrayDeque<>();
    transferThread = new Thread(this::threadMain);
    transferThread.start();
  }

  private void threadMain() {
    while(true) {
      try {
        ByteBuffer buf = Util.readSinglePacket(sock.getInputStream());
        if(Util.DEBUG) logs.info("Packet Recieved: %s", Util.debugPrintPacket(buf));
        Packet p = Packet.deserialize(buf);
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

  /**
   *
   * @param state The game state.
   * @param playerId The index of the player that this controller controls.
   */
  @Override
  public void initialize(MahjongGame state, int playerId) {
    playerState = state.getPlayer(playerId);
  }

  /**
   *
   * @param packet The packet to send.
   */
  @Override
  public void send(Packet packet) {
    try {
      sock.getOutputStream().write(Packet.serialize(packet).array());
    } catch(IOException ignored) {
      // Assume sends are infallible (reads will fail anyway if the connection is down)
    }
  }

  /**
   *
   * @param timeout The time to wait for incoming moves, in milliseconds.
   * @return
   */
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
