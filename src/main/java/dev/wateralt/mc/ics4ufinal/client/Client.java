package dev.wateralt.mc.ics4ufinal.client;

import dev.wateralt.mc.ics4ufinal.common.Logger;
import dev.wateralt.mc.ics4ufinal.common.MahjongTile;
import dev.wateralt.mc.ics4ufinal.common.Util;
import dev.wateralt.mc.ics4ufinal.common.network.DiscardTilePacket;
import dev.wateralt.mc.ics4ufinal.common.network.GainTilePacket;
import dev.wateralt.mc.ics4ufinal.common.network.Packet;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Client {
  Socket socket;
  Thread thread;
  MahjongClientState clientState;
  AtomicInteger signal; // Host->Thread
  Logger logs;

  public Client(String url) throws IOException {
    socket = new Socket();
    String[] urlParts = url.split(":");
    clientState = new MahjongClientState();
    socket.connect(new InetSocketAddress(urlParts[0], Integer.parseInt(urlParts[1])));
    signal = new AtomicInteger();
    signal.set(0);
    logs = new Logger(this);
    thread = new Thread(this::threadMain);
    thread.start();
  }

  public static Client startDebug() throws IOException {
    return new Client("localhost:42727");
  }

  public MahjongClientState getClientState() {
    return clientState;
  }

  private void threadMain() {
    try {
      while(true) {
        ByteBuffer buf = Util.readSinglePacket(socket.getInputStream());
        if(Util.DEBUG) logs.info("Packet Received: %s", Util.debugPrintPacket(buf));
        Packet p = Packet.deserialize(buf);
        if(p == null) throw new IOException();
        if(p.getId() == GainTilePacket.ID) {
          GainTilePacket pDerived = (GainTilePacket)p;
          synchronized (clientState) {
            clientState.getPlayerHands()[pDerived.getPlayer()].getHidden().add(pDerived.getTile());
            // GainTile can only be shown to you
            if(!pDerived.getTile().equals(MahjongTile.NULL)) {
              clientState.setMyPlayerId(pDerived.getPlayer());
            }
          }
          break;
        } else if(p.getId() == DiscardTilePacket.ID) {
          DiscardTilePacket pDerived = (DiscardTilePacket)p;
          synchronized (clientState) {
            clientState.getPlayerHands()[pDerived.getPlayer()].getHidden().add(pDerived.getTile());
          }
        }
        if(signal.get() == 1) {
          break;
        }
      }
    } catch(IOException ignored) {
    }
  }

  public void close() {
    signal.set(1);
  }
}
