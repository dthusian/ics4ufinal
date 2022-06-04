package dev.wateralt.mc.ics4ufinal.server.controllers;

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
        ByteBuffer lengthBuf = ByteBuffer.allocate(4);
        int read = sock.getInputStream().read(lengthBuf.array(), 0, 4);
        if(read != 4) throw new IOException();
        int length = lengthBuf.getInt(0);
        ByteBuffer buf = ByteBuffer.allocate(length + 8);
        buf.putInt(read);
        read = sock.getInputStream().read(buf.array(), 4, length + 4);
        if(read != length + 4) throw new IOException();
        Packet p = Packet.deserialize(buf);
        if(p == null) {
        } else if(p.getId() == SelfNamePacket.ID) {
          synchronized (playerState) {
            playerState.setName(((SelfNamePacket)p).getName());
          }
        } else {
          turnsPacketQueue.add(p);
        }
      } catch(IOException ignored) {
        break;
      }
    }
  }

  @Override
  public void initialize(MahjongGame.PlayerState state) {
    playerState = state;
  }

  @Override
  public void send(Packet packet) {
    try {
      sock.getOutputStream().write(Packet.serialize(packet).array());
    } catch(IOException ignored) {
      // Assume sends are infalliable (reads will fail anyway if the connection is down)
    }
  }

  @Override
  public Packet receive(int timeout) {
    synchronized (turnsPacketQueue) {
      return turnsPacketQueue.remove();
    }
  }
}
