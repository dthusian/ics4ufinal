package dev.wateralt.mc.ics4ufinal.client.uilayers;

import dev.wateralt.mc.ics4ufinal.client.FontManager;
import dev.wateralt.mc.ics4ufinal.client.UIManager;
import dev.wateralt.mc.ics4ufinal.client.Window;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NanoVG;

import java.io.IOException;

public class MenuUI implements UILayer {
  private static final int BTN_WIDTH = 250;
  private static final int BTN_HEIGHT = 75;
  private static final int TEXT_WIDTH = 250;
  private static final int TEXT_HEIGHT = 50;

  String contents;
  UIManager mgr;
  String connectionError;
  int connectionErrorTimeout;
  int mx, my;

  public MenuUI(UIManager mgr) {
    contents = "";
    this.mgr = mgr;
    connectionErrorTimeout = -1;
    mx = 0;
    my = 0;
  }

  @Override
  public String getId() {
    return "MenuUI";
  }

  @Override
  public void initialize(Window wnd) {

  }

  @Override
  public void render(Window wnd) {
    NVGColor color = NVGColor.malloc().r(0.0f).g(0.0f).b(0.3f).a(1.0f);
    NVGColor white = NVGColor.malloc().r(1.0f).g(1.0f).b(1.0f).a(1.0f);
    NVGColor red = NVGColor.malloc().r(1.0f).g(0.0f).b(0.0f).a(1.0f);

    long nvg = wnd.getNanoVG();
    NanoVG.nvgBeginFrame(nvg, (float) wnd.getWidth(), (float) wnd.getHeight(), (float) wnd.getDpi());
    NanoVG.nvgFontFaceId(nvg, FontManager.fontSourceSansPro);
    NanoVG.nvgFontSize(nvg, 30);

    NanoVG.nvgFillColor(nvg, color);
    NanoVG.nvgBeginPath(nvg);
    NanoVG.nvgRoundedRect(nvg, 50, 500, BTN_WIDTH, BTN_HEIGHT, 15);
    NanoVG.nvgRoundedRect(nvg, 50, 600, TEXT_WIDTH, TEXT_HEIGHT, 15);
    NanoVG.nvgRoundedRect(nvg, 50, 675, BTN_WIDTH, BTN_HEIGHT, 15);
    NanoVG.nvgFill(nvg);
    NanoVG.nvgClosePath(nvg);

    NanoVG.nvgFillColor(nvg, white);
    NanoVG.nvgBeginPath(nvg);
    NanoVG.nvgRect(nvg, 60, 610, TEXT_WIDTH - 20, TEXT_HEIGHT - 20);
    NanoVG.nvgFill(nvg);
    NanoVG.nvgClosePath(nvg);

    NanoVG.nvgFillColor(nvg, color);
    NanoVG.nvgText(nvg, 60, 637, contents);

    if(connectionErrorTimeout != -1) {
      NanoVG.nvgFillColor(nvg, red);
      NanoVG.nvgStrokeColor(nvg, red);
      NanoVG.nvgText(nvg, wnd.getWidth() / 2, wnd.getHeight() - 500, connectionError);
      connectionErrorTimeout--;
    }

    NanoVG.nvgEndFrame(wnd.getNanoVG());
    color.free();
    white.free();
    red.free();
  }

  @Override
  public boolean onKeyPress(Window wnd, int key, int modifiers) {
    if(key >= GLFW.GLFW_KEY_A && key <= GLFW.GLFW_KEY_Z) {
      if((modifiers & GLFW.GLFW_MOD_SHIFT) != 0) {
        contents += Character.toChars((int)'A' + (key - GLFW.GLFW_KEY_A))[0];
      } else {
        contents += Character.toChars((int)'a' + (key - GLFW.GLFW_KEY_A))[0];
      }
    }
    if(key >= GLFW.GLFW_KEY_0 && key <= GLFW.GLFW_KEY_9) {
      contents += Character.toChars((int)'0' + (key - GLFW.GLFW_KEY_0))[0];
    }
    if(key == GLFW.GLFW_KEY_BACKSPACE && contents.length() > 0) {
      contents = contents.substring(0, contents.length() - 1);
    }
    if(key == GLFW.GLFW_KEY_PERIOD) contents += '.';
    if(key == GLFW.GLFW_KEY_SEMICOLON) contents += ':';
    return true;
  }

  @Override
  public boolean onMouseMove(Window wnd, double newX, double newY) {
    mx = (int) newX;
    my = (int) newY;
    return true;
  }

  @Override
  public boolean onMousePress(Window wnd, int button) {
    if(mx > 50 && mx < 300 && my > 500 && my < 575) {
      try {
        mgr.hostGame();
      } catch(IOException err) {
        connectionError = err.getMessage();
        connectionErrorTimeout = 1200;
      }
      return false;
    }
    if(mx > 50 && mx < 300 && my > 650 && my < 725) {
      try {
        mgr.connectToGame(contents);
      } catch(Exception err) {
        connectionError = err.getMessage();
        connectionErrorTimeout = 1200;
      }
    }
    return true;
  }
}
