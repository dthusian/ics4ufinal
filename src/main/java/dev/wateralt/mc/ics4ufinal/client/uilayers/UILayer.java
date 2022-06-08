package dev.wateralt.mc.ics4ufinal.client.uilayers;

import dev.wateralt.mc.ics4ufinal.client.Window;

/*
UI Layers API

I don't want to use Java Swing, but I don't want to rewrite a full UI toolkit, so UI Layers are the easy middle ground.
They're rather easy to implement and easy to extend. Rendering is controlled by a set of layers, each of which is allowed
to render one after the other. Layers can render themselves using either NanoVG or OpenGL. Each layer has a string ID,
and that ID can be used to identify themselves and add new components relative to one another.

Events are propagated down the stack, with each event handler returning an boolean indicating whether the event should
continue propagating or not (true = continue; false = stop)
*/

public interface UILayer {
  String getId();
  void initialize(Window wnd);
  void render(Window wnd);
  default boolean onMouseMove(Window wnd, double newX, double newY) {
    return true;
  }
  default boolean onMousePress(Window wnd, int button) {
    return true;
  }
  default boolean onMouseRelease(Window wnd, int button) {
    return true;
  }
  default void close() { }
}
