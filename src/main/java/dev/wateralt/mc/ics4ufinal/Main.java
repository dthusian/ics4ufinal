package dev.wateralt.mc.ics4ufinal;

import dev.wateralt.mc.ics4ufinal.client.MahjongClientState;
import dev.wateralt.mc.ics4ufinal.client.uilayers.DebugLayer;
import dev.wateralt.mc.ics4ufinal.client.uilayers.MahjongRenderer;
import dev.wateralt.mc.ics4ufinal.client.Window;
import dev.wateralt.mc.ics4ufinal.common.MahjongHand;
import dev.wateralt.mc.ics4ufinal.common.MahjongTile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
  public static void main(String[] args) {
    Window wnd = new Window("LWJGL Tech Test");
    MahjongRenderer render = new MahjongRenderer();
    MahjongClientState state = new MahjongClientState();
    for(int i = 0; i < 4; i++)
      state.getPlayerHands()[i].getHidden().addAll(Stream.of(
          "1m", "9m", "1p", "9p", "1s", "9s",
          "1z", "2z", "3z", "4z", "5z", "6z", "7z"
      ).map(MahjongTile::new).toList());
    render.setClientState(state);
    wnd.addLayer(render);
    wnd.addLayer(new DebugLayer());
    wnd.run();
  }
}