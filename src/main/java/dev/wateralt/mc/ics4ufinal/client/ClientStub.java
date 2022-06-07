package dev.wateralt.mc.ics4ufinal.client;

import dev.wateralt.mc.ics4ufinal.common.MahjongTile;
import dev.wateralt.mc.ics4ufinal.common.Util;
import dev.wateralt.mc.ics4ufinal.common.network.DiscardTilePacket;
import dev.wateralt.mc.ics4ufinal.common.network.GainTilePacket;
import dev.wateralt.mc.ics4ufinal.common.network.Packet;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ClientStub {
  Socket socket;
  MahjongClientState clientState;

  public ClientStub(String url) throws IOException {
    socket = new Socket();
    String[] urlParts = url.split(":");
    clientState = new MahjongClientState();
    socket.connect(new InetSocketAddress(urlParts[0], Integer.parseInt(urlParts[1])));
  }

  public static ClientStub startDebug() throws IOException {
    return new ClientStub("localhost:42727");
  }

  public MahjongClientState getClientState() {
    return clientState;
  }

  public void run() {
    try {
      while(true) {
        Packet p = Packet.deserialize(Util.readSinglePacket(socket.getInputStream()));
        if(p == null) throw new IOException();
        if(p.getId() == GainTilePacket.ID) {
          GainTilePacket pDerived = (GainTilePacket)p;
          clientState.getPlayerHands()[pDerived.getPlayer()].getHidden().add(pDerived.getTile());
          if(!pDerived.getTile().equals(MahjongTile.NULL)) {

          }
          break;
        } else if(p.getId() == DiscardTilePacket.ID) {
          DiscardTilePacket pDerived = (DiscardTilePacket)p;

        }
      }
    } catch(IOException err) {

    }
  }
}
