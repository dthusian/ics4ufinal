package dev.wateralt.mc.ics4ufinal.client.uilayers;

import dev.wateralt.mc.ics4ufinal.client.NanoVGResources;
import dev.wateralt.mc.ics4ufinal.client.UIManager;
import dev.wateralt.mc.ics4ufinal.client.Window;
import org.lwjgl.nanovg.NanoVG;

public class LobbyUI implements UILayer {

  UIManager mgr;

  public LobbyUI(UIManager mgr) {
    this.mgr = mgr;
  }

  @Override
  public String getId() {
    return "LobbyUI";
  }

  @Override
  public void initialize(Window wnd) {

  }

  @Override
  public void render(Window wnd) {
    long nvg = wnd.getNanoVG();
    NanoVG.nvgBeginFrame(nvg, (float) wnd.getWidth(), (float) wnd.getHeight(), (float) wnd.getDpi());
    NanoVG.nvgFontFaceId(nvg, NanoVGResources.fontSourceSansPro);
    NanoVG.nvgFontSize(nvg, 30);


  }
}
