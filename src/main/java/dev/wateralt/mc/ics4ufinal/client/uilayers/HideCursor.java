package dev.wateralt.mc.ics4ufinal.client.uilayers;

import dev.wateralt.mc.ics4ufinal.client.Window;
import org.lwjgl.glfw.GLFW;

public class HideCursor implements UILayer {
  @Override
  public String getId() {
    return null;
  }

  @Override
  public void initialize(Window wnd) {
    GLFW.glfwSetInputMode(wnd.getGlfw(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
  }

  @Override
  public void render(Window wnd) {

  }
}
