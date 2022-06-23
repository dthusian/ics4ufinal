package dev.wateralt.mc.ics4ufinal.client.uilayers;

import dev.wateralt.mc.ics4ufinal.client.MahjongClientState;
import dev.wateralt.mc.ics4ufinal.client.NanoVGResources;
import dev.wateralt.mc.ics4ufinal.client.UIManager;
import dev.wateralt.mc.ics4ufinal.client.Window;
import org.lwjgl.nanovg.NanoVG;

public class LobbyUI implements UILayer {

  UIManager mgr;
  MahjongClientState state;

  public LobbyUI(UIManager mgr) {
    this.mgr = mgr;
  }

  @Override
  public String getId() {
    return "LobbyUI";
  }

  public void setClientState(MahjongClientState clientState) {
    state = clientState;
  }

  @Override
  public void initialize(Window wnd) {

  }

  @Override
  public void render(Window wnd) {
    long nvg = wnd.getNanoVG();
    if(state.getnPlayers() != 4) {
      NanoVG.nvgBeginFrame(nvg, (float) wnd.getWidth(), (float) wnd.getHeight(), (float) wnd.getDpi());
      NanoVG.nvgFontFaceId(nvg, NanoVGResources.fontSourceSansPro);
      NanoVG.nvgFontSize(nvg, 30);

      NanoVG.nvgFillColor(nvg, NanoVGResources.colorDarkBlue);
      NanoVG.nvgBeginPath(nvg);
      NanoVG.nvgRoundedRect(nvg, wnd.getWidth() / 2 - 300 / 2, wnd.getHeight() / 2 - 100 / 2, 300, 100, 15);
      NanoVG.nvgFill(nvg);
      NanoVG.nvgFillColor(nvg, NanoVGResources.colorWhite);
      NanoVG.nvgTextAlign(nvg, NanoVG.NVG_ALIGN_CENTER);
      NanoVG.nvgText(nvg, wnd.getWidth() / 2, wnd.getHeight() / 2 - 15, "Waiting for Players");
      NanoVG.nvgText(nvg, wnd.getWidth() / 2, wnd.getHeight() / 2 + 35, "%d/4".formatted(state.getnPlayers()));
      NanoVG.nvgClosePath(nvg);

      NanoVG.nvgEndFrame(nvg);
    }
  }
}
