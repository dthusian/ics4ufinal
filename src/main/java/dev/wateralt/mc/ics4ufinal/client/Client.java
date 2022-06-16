package dev.wateralt.mc.ics4ufinal.client;

import dev.wateralt.mc.ics4ufinal.common.Logger;
import dev.wateralt.mc.ics4ufinal.common.MahjongTile;
import dev.wateralt.mc.ics4ufinal.common.Util;
import dev.wateralt.mc.ics4ufinal.common.network.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Comparator;
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
    signal.set(Util.SIGNAL_START);
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
      socket.getOutputStream().write(Util.byteBufToByteArray(Packet.serialize(new SelfNamePacket("Among Us"))));
      while(true) {
        if(signal.get() == Util.SIGNAL_STOP) {
          break;
        } else if(signal.get() == Util.SIGNAL_SUSPEND) {
          continue;
        }
        ByteBuffer buf = Util.readSinglePacket(socket.getInputStream());
        if(Util.DEBUG) logs.info("Packet Received: %s", Util.debugPrintPacket(buf));
        Packet p = Packet.deserialize(buf);
        if(p == null) throw new IOException();
        synchronized (clientState) {
          handlePacket(p);
        }
      }
    } catch(IOException ignored) {
    }
  }

  public void handlePacket(Packet p) throws IOException {
    if(p.getId() == GainTilePacket.ID) {
      GainTilePacket pDerived = (GainTilePacket)p;
      clientState.getPlayerHands()[pDerived.getPlayer()].getHidden().add(pDerived.getTile());
      // GainTile can only be shown to you
      if(!pDerived.getTile().equals(MahjongTile.NULL)) {
        clientState.setMyPlayerId(pDerived.getPlayer());
      }
      if(clientState.getMyHand().getLength() == 14) {
        clientState.setPlayerAction(MahjongClientState.PlayerAction.DISCARD_TILE);
      }
    } else if(p.getId() == DiscardTilePacket.ID) {
      DiscardTilePacket pDerived = (DiscardTilePacket)p;
      clientState.getPlayerDiscardPiles().get(pDerived.getPlayer()).add(pDerived.getTile());
      if(pDerived.getPlayer() == clientState.getMyPlayerId()) {
        clientState.getMyHand().getHidden().remove(pDerived.getTile());
      } else {
        ArrayList<MahjongTile> hidden = clientState.getPlayerHands()[pDerived.getPlayer()].getHidden();
        hidden.remove(hidden.size() - 1);
      }
      if(pDerived.getPlayer() == clientState.getMyPlayerId()) {
        sendPacket(new NoActionPacket());
      } else {
        //TODO pon
        //clientState.setPlayerAction(MahjongClientState.PlayerAction.CALL_TILE);
        sendPacket(new NoActionPacket());
      }
    }
    clientState.getMyHand().getHidden().sort(Comparator.naturalOrder());
  }

  public void sendPacket(Packet p) throws IOException {
    socket.getOutputStream().write(Util.byteBufToByteArray(Packet.serialize(p)));
  }

  /**
   * Stops the listening thread and releases resources used
   */
  public void close() {
    signal.set(1);
  }
}
