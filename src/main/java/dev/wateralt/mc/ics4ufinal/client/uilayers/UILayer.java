package dev.wateralt.mc.ics4ufinal.client.uilayers;

import dev.wateralt.mc.ics4ufinal.client.Window;

public interface UILayer {
  String getId();
  void initialize(Window wnd);
  void render(Window wnd);
  default void onMouseMove(Window wnd, double newX, double newY) { }
  default boolean onMousePress(Window wnd, int button) {
    return true;
  }
  default boolean onMouseRelease(Window wnd, int button) {
    return true;
  }
}
