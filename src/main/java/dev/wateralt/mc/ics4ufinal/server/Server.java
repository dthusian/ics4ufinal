package dev.wateralt.mc.ics4ufinal.server;

import dev.wateralt.mc.ics4ufinal.common.Logger;
import dev.wateralt.mc.ics4ufinal.common.network.PlayerChangePacket;
import dev.wateralt.mc.ics4ufinal.server.controllers.NetworkPlayerController;
import dev.wateralt.mc.ics4ufinal.server.controllers.NullController;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Batteries-included server part of the stack. Includes game state as well.
 */
public class Server {
  ServerSocket socket;
  Thread thread;
  MahjongGame state;
  AtomicInteger signal;
  Logger logs;
  int needPlayers;

  /**
   * Constructs a server to listen on a specified port.
   * @param port The port to listen on.
   * @throws IOException
   */
  public Server(int port, int nBots) throws IOException {
    if(nBots > 4 || nBots < 0) throw new IllegalArgumentException();
    socket = new ServerSocket(port);
    state = new MahjongGame();
    logs = new Logger(this);
    signal = new AtomicInteger();
    signal.set(0);
    needPlayers = 4 - nBots;
    for(int i = 0; i < nBots; i++) state.addPlayer(new NullController());
    thread = new Thread(this::threadMain);
    thread.start();
  }

  /**
   * Starts the server in a debug environment.
   * @return The debug server.
   * @throws IOException
   */
  public static Server startDebug() throws IOException {
    return new Server(42727, 3);
  }

  /**
   * Stops a server.
   * @throws IOException
   */
  public void stop() throws IOException {
    signal.set(1);
    socket.close();
  }

  private void threadMain() {
    try {
      //TODO debug only
      for(int i = 0; i < needPlayers; i++) {
        Socket client = socket.accept();
        logs.info("Player connected: %s", client.getInetAddress().toString());
        state.addPlayer(new NetworkPlayerController(client));
        for(int j = 0; j < state.getNumPlayers(); j++) {
          state.getPlayer(j).getController().send(new PlayerChangePacket(state.getNumPlayers()));
        }
      }
      state.runGame();
    } catch(IOException ignored) {
    }
  }
}
