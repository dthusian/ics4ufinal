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

/**
 * <h2>Client</h2>
 *
 * The client class contains a batteries-included client portion of the client-server networking model.
 * It starts a new thread to receive packets and updates the MahjongClientState with that info.
 */
public class Client {
  Socket socket;
  Thread thread;
  MahjongClientState clientState;
  AtomicInteger signal; // Host->Thread
  Logger logs;

  /**
   * Connects a client to a remote Mahjong server
   * @param url URL of that server, written in <pre>&lt;ip&gt;:&lt;port&gt;</pre> notation
   * @throws IOException
   */
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

  /**
   * Connects a client to the local testing server
   * @return The client connected
   * @throws IOException
   */
  public static Client startDebug() throws IOException {
    return new Client("localhost:42727");
  }

  /**
   * Gets the client state associated with this Client
   * @return The client state
   */
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

  /**
   * Stops the listening thread and releases resources used
   */
  public void close() {
    signal.set(1);
  }
}
