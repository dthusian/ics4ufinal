package dev.wateralt.mc.ics4ufinal.server.controllers;

import dev.wateralt.mc.ics4ufinal.common.network.Packet;

import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;

public class NetworkPlayerController implements Controller {
  Socket sock;

  public NetworkPlayerController(Socket tcpStream) {
    sock = tcpStream;
  }

  @Override
  public void sendMove(Packet packet) {
    try {
      sock.getOutputStream().write(packet.serializeInternal().array());
    } catch(IOException err) {
      throw new RuntimeException(err);
    }
  }

  @Override
  public Packet recvMove(int timeout) {
    return null;
  }
}
