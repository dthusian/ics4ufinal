package dev.wateralt.mc.ics4ufinal.client.uilayers;

import dev.wateralt.mc.ics4ufinal.client.UIManager;
import dev.wateralt.mc.ics4ufinal.client.Window;

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

  }
}
