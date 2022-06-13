package dev.wateralt.mc.ics4ufinal.client.uilayers;

import dev.wateralt.mc.ics4ufinal.client.Window;

/**
 * <h2>UI Layers API</h2>
 *
 * I don't want to use Java Swing, but I don't want to rewrite a full UI toolkit, so UI Layers are the easy middle ground.
 * They're rather easy to implement and easy to extend. Rendering is controlled by a set of layers, each of which is allowed
 * to render one after the other. Layers can render themselves using either NanoVG or OpenGL. Each layer has a string ID,
 * and that ID can be used to identify themselves and add new components relative to one another.
 * <br/><br/>
 * Events are propagated down the stack, with each event handler returning a boolean indicating whether the event should
 * continue propagating or not (true = continue; false = stop)
 */
public interface UILayer {

  /**
   * Returns a unique ID for this UI layer. Used for adding or removing layers.
   * @return THe unique layer ID.
   */
  String getId();

  /**
   * Initializes the UI Layer. At this point, the Window and OpenGL context are
   * guaranteed to exist, so OpenGL layers should construct their resources here.
   * @param wnd The window that the layer will be added to.
   */
  void initialize(Window wnd);

  /**
   * Render to the window.
   * @param wnd The window to render to.
   */
  void render(Window wnd);

  /**
   * Event handler that is fired when the mouse cursor moves.
   * @param wnd The window the event is fired for.
   * @param newX The x-position (in pixels from the left) that the mouse is now at.
   * @param newY The y-position (in pixels from the top) that the mouse is now
   * @return true to continue propagating the event down the stack, false otherwise
   */
  default boolean onMouseMove(Window wnd, double newX, double newY) {
    return true;
  }

  /**
   * Event handler that is fired when the mouse is pressed down.
   * @param wnd The window the event is fired for.
   * @param button The number of the mouse button. Uses the normal 1 = left, 2 = right, 3 = middle convention.
   * @return true to continue propagating the event down the stack, false otherwise
   */
  default boolean onMousePress(Window wnd, int button) {
    return true;
  }

  /**
   * Event handler that is fired when the mouse is unpressed.
   * @param wnd The window the event is fired for.
   * @param button The number of the mouse button. Uses the normal 1 = left, 2 = right, 3 = middle convention.
   * @return true to continue propagating the event down the stack, false otherwise
   */
  default boolean onMouseRelease(Window wnd, int button) {
    return true;
  }

  /**
   * Called when the layer is removed from the stack, to release its resources.
   */
  default void close() { }
}
