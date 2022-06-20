package dev.wateralt.mc.ics4ufinal.client.uilayers;

import dev.wateralt.mc.ics4ufinal.client.NanoVGResources;
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
  int nBots;

  public MenuUI(UIManager mgr) {
    contents = "";
    this.mgr = mgr;
    connectionErrorTimeout = -1;
    mx = 0;
    my = 0;
    nBots = 0;
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

    long nvg = wnd.getNanoVG();
    NanoVG.nvgBeginFrame(nvg, (float) wnd.getWidth(), (float) wnd.getHeight(), (float) wnd.getDpi());
    NanoVG.nvgFontFaceId(nvg, NanoVGResources.fontSourceSansPro);
    NanoVG.nvgFontSize(nvg, 30);

    NanoVG.nvgFillColor(nvg, NanoVGResources.colorDarkBlue);
    NanoVG.nvgBeginPath(nvg);
    NanoVG.nvgRoundedRect(nvg, topLeft(wnd, 1), 400, BTN_WIDTH, BTN_HEIGHT, 15);
    NanoVG.nvgRoundedRect(nvg, topLeft(wnd, 1),  500, BTN_WIDTH, BTN_HEIGHT, 15);
    NanoVG.nvgRoundedRect(nvg, topLeft(wnd, 2), 500, TEXT_WIDTH, TEXT_HEIGHT, 15);
    NanoVG.nvgRoundedRect(nvg, topLeft(wnd, 2), 400, BTN_WIDTH, BTN_HEIGHT, 15);
    NanoVG.nvgFill(nvg);
    NanoVG.nvgClosePath(nvg);

    NanoVG.nvgFillColor(nvg, NanoVGResources.colorWhite);
    NanoVG.nvgBeginPath(nvg);
    NanoVG.nvgRect(nvg, topLeft(wnd, 2) + 10, 510, TEXT_WIDTH - 20, TEXT_HEIGHT - 20);
    NanoVG.nvgFill(nvg);
    NanoVG.nvgClosePath(nvg);

    NanoVG.nvgFillColor(nvg, NanoVGResources.colorWhite);
    NanoVG.nvgTextAlign(nvg, NanoVG.NVG_ALIGN_CENTER);
    NanoVG.nvgText(nvg, topLeft(wnd, 1) + 125, 450, "Host Game");
    NanoVG.nvgText(nvg, topLeft(wnd, 2) + 125, 450, "Connect to Game");
    NanoVG.nvgText(nvg, topLeft(wnd, 1) + 125, 550, "Bots: " + nBots);

    NanoVG.nvgFillColor(nvg, NanoVGResources.colorDarkBlue);
    NanoVG.nvgTextAlign(nvg, NanoVG.NVG_ALIGN_LEFT);
    NanoVG.nvgText(nvg, topLeft(wnd, 2) + 10, 535, contents);

    if(connectionErrorTimeout != -1) {
      NanoVG.nvgFillColor(nvg, NanoVGResources.colorRed);
      NanoVG.nvgStrokeColor(nvg, NanoVGResources.colorRed);
      NanoVG.nvgText(nvg, wnd.getWidth() / 2, wnd.getHeight() - 500, connectionError);
      connectionErrorTimeout--;
    }

    NanoVG.nvgEndFrame(wnd.getNanoVG());
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

  private int topLeft(Window wnd, int i) {
    return i * wnd.getWidth() / 3 - 125;
  }

  @Override
  public boolean onMousePress(Window wnd, int button) {
    if(mx > topLeft(wnd, 1) && mx < topLeft(wnd, 1) + 250 && my > 400 && my < 475) {
      try {
        mgr.hostGame(nBots);
      } catch(IOException err) {
        connectionError = err.getMessage();
        connectionErrorTimeout = 1200;
      }
      return false;
    }
    if(mx > topLeft(wnd, 2) && mx < topLeft(wnd, 2) + 250 && my > 400 && my < 475) {
      try {
        mgr.connectToGame(contents);
      } catch(Exception err) {
        connectionError = err.getMessage();
        connectionErrorTimeout = 1200;
      }
      return false;
    }
    if(mx > topLeft(wnd, 1) && mx < topLeft(wnd, 1) + 250 && my > 500 && my < 575) {
      nBots++;
      nBots %= 4;
    }
    return true;
  }
}
