package dev.wateralt.mc.ics4ufinal.server;

import dev.wateralt.mc.ics4ufinal.common.Logger;
import dev.wateralt.mc.ics4ufinal.server.controllers.NetworkPlayerController;
import dev.wateralt.mc.ics4ufinal.server.controllers.NullController;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

public class Server {
  ServerSocket socket;
  Thread thread;
  MahjongGame state;
  AtomicInteger signal;
  Logger logs;

  public Server(int port) throws IOException {
    socket = new ServerSocket(port);
    state = new MahjongGame();
    logs = new Logger();
    signal = new AtomicInteger();
    signal.set(0);
    thread = new Thread(this::run);
    thread.start();
  }

  public static Server startDebug() throws IOException {
    return new Server(42727);
  }

  public void stop() throws IOException {
    signal.set(1);
    socket.close();
  }

  public void run() {
    try {
      //TODO debug only
      Socket client = socket.accept();
      logs.info("Player connected: %s", client.getInetAddress().toString());
      state.addPlayer(new NetworkPlayerController(client));
      state.addPlayer(new NullController());
      state.addPlayer(new NullController());
      state.addPlayer(new NullController());
      state.runGame();
    } catch(IOException ignored) {
    }
  }
}
