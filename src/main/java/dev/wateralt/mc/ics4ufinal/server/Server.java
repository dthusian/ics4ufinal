package dev.wateralt.mc.ics4ufinal.server;

import dev.wateralt.mc.ics4ufinal.server.controllers.NetworkPlayerController;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

public class Server {
  ServerSocket socket;
  Thread thread;
  MahjongGame state;
  AtomicInteger signal;

  public Server(short port) throws IOException {
    socket = new ServerSocket(port);
    state = new MahjongGame();
    thread = new Thread(this::run);
    thread.start();
  }

  private void run() {
    try {
      while(true) {
          Socket client = socket.accept();
          state.addPlayer(new NetworkPlayerController(client));
        if(signal.get() == 1) {
          break;
        }
      }
    } catch(IOException ignored) {

    }
  }
}
