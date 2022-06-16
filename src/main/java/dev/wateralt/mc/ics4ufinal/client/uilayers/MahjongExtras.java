package dev.wateralt.mc.ics4ufinal.client.uilayers;

import dev.wateralt.mc.ics4ufinal.client.Client;
import dev.wateralt.mc.ics4ufinal.client.MahjongClientState;
import dev.wateralt.mc.ics4ufinal.client.Window;
import dev.wateralt.mc.ics4ufinal.common.network.DiscardTilePacket;

import java.io.IOException;

/**
 * I wanted MahjongRenderer to be hyper-focused on OpenGL code, so this UI layer implements a lot
 * of stuff that is needed to play the game yet isn't too OpenGL focused.
 */
public class MahjongExtras implements UILayer {
  MahjongClientState state;
  Client client;
  MahjongRenderer renderer;

  @Override
  public String getId() {
    return "MahjongExtras";
  }

  @Override
  public void initialize(Window wnd) {

  }

  public void setRenderer(MahjongRenderer renderer) {
    this.renderer = renderer;
  }

  public void setClientState(MahjongClientState state) {
    this.state = state;
  }

  public void setClient(Client client) {
    this.client = client;
  }

  @Override
  public void render(Window wnd) {

  }

  @Override
  public boolean onMousePress(Window wnd, int button) {
    synchronized (state) {
      if(state.getPlayerAction() == MahjongClientState.PlayerAction.DISCARD_TILE) {
        int hoveredTile = renderer.getHoveredTileIndex();
        if(hoveredTile < state.getMyHand().getHidden().size() && hoveredTile >= 0) {
          try {
            state.setPlayerAction(MahjongClientState.PlayerAction.NOTHING);
            client.sendPacket(new DiscardTilePacket(state.getMyHand().getHidden().get(hoveredTile), state.getMyPlayerId()));
          } catch(IOException ignored) {

          }
        }
      }
    }
    return false;
  }
}
