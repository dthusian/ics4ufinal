package dev.wateralt.mc.ics4ufinal.client.uilayers;

import dev.wateralt.mc.ics4ufinal.client.Client;
import dev.wateralt.mc.ics4ufinal.client.NanoVGResources;
import dev.wateralt.mc.ics4ufinal.client.MahjongClientState;
import dev.wateralt.mc.ics4ufinal.client.Window;
import dev.wateralt.mc.ics4ufinal.common.MahjongTile;
import dev.wateralt.mc.ics4ufinal.common.network.*;
import dev.wateralt.mc.ics4ufinal.common.yaku.Yaku;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NanoVG;

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
  public void setClient(Client client) {
    this.client = client;
    this.state = client.getClientState();
  }

  @Override
  public void render(Window wnd) {
    long nvg = wnd.getNanoVG();
    NanoVG.nvgBeginFrame(nvg, (float) wnd.getWidth(), (float) wnd.getHeight(), (float) wnd.getDpi());
    NanoVG.nvgFillColor(nvg, NanoVGResources.colorDarkBlue);
    NanoVG.nvgStrokeColor(nvg, NanoVGResources.colorDarkBlue);
    NanoVG.nvgFontFaceId(nvg, NanoVGResources.fontSourceSansPro);
    NanoVG.nvgFontSize(nvg, 40);

    // Draw crosshair
    NanoVG.nvgBeginPath(nvg);
    NanoVG.nvgMoveTo(nvg, wnd.getWidth() / 2, wnd.getHeight() / 2 - 20);
    NanoVG.nvgLineTo(nvg, wnd.getWidth() / 2, wnd.getHeight() / 2 + 20);
    NanoVG.nvgMoveTo(nvg, wnd.getWidth() / 2 - 20, wnd.getHeight() / 2);
    NanoVG.nvgLineTo(nvg, wnd.getWidth() / 2 + 20, wnd.getHeight() / 2);
    NanoVG.nvgStroke(nvg);
    NanoVG.nvgClosePath(nvg);

    synchronized (state) {
      if (state.getPlayerAction() == MahjongClientState.PlayerAction.CALL_TILE) {
        if (state.getCallOptions().canPon()) {
          NanoVG.nvgText(nvg, 50, wnd.getHeight() - 200, "[Q] Pon");
        }
        if (state.getCallOptions().getChiList().size() > 0) {
          NanoVG.nvgText(nvg, 50, wnd.getHeight() - 150, "[W] Chi");
        }
        if(state.getCallOptions().canRon()) {
          NanoVG.nvgText(nvg, 50, wnd.getHeight() - 100, "[E] Ron");
        }
        NanoVG.nvgText(nvg, 50, wnd.getHeight() - 250, "[Z] Skip");
      } else if(state.getPlayerAction() == MahjongClientState.PlayerAction.SELECT_CHI) {
        NanoVG.nvgTextAlign(nvg, NanoVG.NVG_ALIGN_CENTER);
        NanoVG.nvgText(nvg, wnd.getWidth() / 2, wnd.getHeight() - 500, "Select a tile to chi");
        NanoVG.nvgTextAlign(nvg, NanoVG.NVG_ALIGN_LEFT);
      } else if(state.getPlayerAction() == MahjongClientState.PlayerAction.DISCARD_TILE) {
        NanoVG.nvgTextAlign(nvg, NanoVG.NVG_ALIGN_CENTER);
        NanoVG.nvgText(nvg, wnd.getWidth() / 2, wnd.getHeight() - 500, "Discard a tile");
        NanoVG.nvgTextAlign(nvg, NanoVG.NVG_ALIGN_LEFT);
        if(state.getCanTsumo()) {
          NanoVG.nvgText(nvg, 50, wnd.getHeight() - 200, "[E] Tsumo");
        }
      } else if(state.getPlayerAction() == MahjongClientState.PlayerAction.ENDED_GAME) {
        int i = 100;
        NanoVG.nvgTextAlign(nvg, NanoVG.NVG_ALIGN_LEFT);
        NanoVG.nvgText(nvg, 300, i, "Player %d wins!".formatted(state.getWinningId() + 1));
        for(Yaku.YakuEntry yaku : state.getWinningYakus()) {
          i += 50;
          NanoVG.nvgText(nvg, 300, i, yaku.toString());
        }
      }
    }
    NanoVG.nvgEndFrame(nvg);
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
      if(state.getPlayerAction() == MahjongClientState.PlayerAction.CALL_TILE) {
        if(key == GLFW.GLFW_KEY_Q && state.getCallOptions().canPon()) {
          state.setPlayerAction(MahjongClientState.PlayerAction.NOTHING);
          state.setCallOptions(null);
          client.sendPacket(new PonPacket());
        } else if(key == GLFW.GLFW_KEY_W && state.getCallOptions().getChiList().size() > 0) {
          state.setPlayerAction(MahjongClientState.PlayerAction.SELECT_CHI);
        } else if(key == GLFW.GLFW_KEY_E && state.getCallOptions().canRon()) {
          state.setPlayerAction(MahjongClientState.PlayerAction.NOTHING);
          client.sendPacket(new WinPacketC2S());
        } else if(key == GLFW.GLFW_KEY_Z) {
          state.setPlayerAction(MahjongClientState.PlayerAction.NOTHING);
          client.sendPacket(new NoActionPacket());
        }
      } else if(state.getPlayerAction() == MahjongClientState.PlayerAction.DISCARD_TILE) {
        if(key == GLFW.GLFW_KEY_E && state.getCanTsumo()) {
          state.setPlayerAction(MahjongClientState.PlayerAction.NOTHING);
          client.sendPacket(new WinPacketC2S());
        }
      }
    }
    return false;
  }
}
