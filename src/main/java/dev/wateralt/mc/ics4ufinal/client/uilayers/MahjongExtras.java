package dev.wateralt.mc.ics4ufinal.client.uilayers;

import dev.wateralt.mc.ics4ufinal.client.Client;
import dev.wateralt.mc.ics4ufinal.client.FontManager;
import dev.wateralt.mc.ics4ufinal.client.MahjongClientState;
import dev.wateralt.mc.ics4ufinal.client.Window;
import dev.wateralt.mc.ics4ufinal.common.MahjongTile;
import dev.wateralt.mc.ics4ufinal.common.Util;
import dev.wateralt.mc.ics4ufinal.common.network.ChiPacket;
import dev.wateralt.mc.ics4ufinal.common.network.DiscardTilePacket;
import dev.wateralt.mc.ics4ufinal.common.network.PonPacket;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NanoVG;

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
    NVGColor col = NVGColor.malloc().r(0.0f).g(0.0f).b(1.0f).a(1.0f);
    NanoVG.nvgBeginFrame(wnd.getNanoVG(), (float) wnd.getWidth(), (float) wnd.getHeight(), (float) wnd.getDpi());
    NanoVG.nvgFillColor(wnd.getNanoVG(), col);
    NanoVG.nvgStrokeColor(wnd.getNanoVG(), col);
    NanoVG.nvgFontFaceId(wnd.getNanoVG(), FontManager.fontSourceSansPro);
    NanoVG.nvgFontSize(wnd.getNanoVG(), 40);
    synchronized (state) {
      if (state.getPlayerAction() == MahjongClientState.PlayerAction.CALL_TILE) {
        if (state.getCallOptions().canPon()) {
          NanoVG.nvgText(wnd.getNanoVG(), 50, wnd.getHeight() - 200, "[Q] Pon");
        }
        if (state.getCallOptions().getChiList().size() > 0) {
          NanoVG.nvgText(wnd.getNanoVG(), 50, wnd.getHeight() - 150, "[W] Chi");
        }
      } else if(state.getPlayerAction() == MahjongClientState.PlayerAction.SELECT_CHI) {
        NanoVG.nvgTextAlign(wnd.getNanoVG(), NanoVG.NVG_ALIGN_CENTER);
        NanoVG.nvgText(wnd.getNanoVG(), wnd.getWidth() / 2, wnd.getHeight() - 500, "Select a tile to chi");
        NanoVG.nvgTextAlign(wnd.getNanoVG(), NanoVG.NVG_ALIGN_LEFT);
      }
    }
    NanoVG.nvgEndFrame(wnd.getNanoVG());
    col.free();
  }

  @Override
  public boolean onMousePress(Window wnd, int button) {
    synchronized (state) {
      if(state.getPlayerAction() == MahjongClientState.PlayerAction.DISCARD_TILE) {
        int hoveredTile = renderer.getHoveredTileIndex();
        if(hoveredTile < state.getMyHand().getHidden().size() && hoveredTile >= 0) {
          state.setPlayerAction(MahjongClientState.PlayerAction.NOTHING);
          client.sendPacket(new DiscardTilePacket(state.getMyHand().getHidden().get(hoveredTile), state.getMyPlayerId()));
        }
      } else if(state.getPlayerAction() == MahjongClientState.PlayerAction.SELECT_CHI) {
        int hoveredTile = renderer.getHoveredTileIndex();
        if(hoveredTile != -1) {
          MahjongTile[] wantChi = state.getCallOptions().getChiList().get(state.getMyHand().getHidden().get(hoveredTile));
          if(wantChi != null) {
            state.setCallOptions(null);
            state.setPlayerAction(MahjongClientState.PlayerAction.NOTHING);
            client.sendPacket(new ChiPacket(wantChi, state.getMyPlayerId()));
          }
        }
      }
    }
    return false;
  }

  @Override
  public boolean onKeyPress(Window wnd, int key, int modifiers) {
    synchronized (state) {
      if(key == GLFW.GLFW_KEY_Q) {
        state.setPlayerAction(MahjongClientState.PlayerAction.NOTHING);
        state.setCallOptions(null);
        client.sendPacket(new PonPacket());
      } else if(key == GLFW.GLFW_KEY_W) {
        state.setPlayerAction(MahjongClientState.PlayerAction.SELECT_CHI);
      }
    }
    return false;
  }
}
