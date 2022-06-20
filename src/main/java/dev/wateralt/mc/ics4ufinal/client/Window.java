// UIWindow.java - NanoVG+GLFW UI renderer that can also render OpenGL scene

package dev.wateralt.mc.ics4ufinal.client;

import dev.wateralt.mc.ics4ufinal.client.uilayers.UILayer;
import dev.wateralt.mc.ics4ufinal.common.Logger;
import dev.wateralt.mc.ics4ufinal.common.Util;
import dev.wateralt.mc.ics4ufinal.common.exception.NativeLibraryException;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.nanovg.NanoVGGL3;
import org.lwjgl.opengl.GL;

import java.util.ArrayList;

/**
 * <h2>Window</h2>
 *
 * The window class performs rendering of UI layers and manages them. It uses GLFW from LWJGL to accomplish this.
 */
public class Window {
  // Window State

  // GLFW handle to Window
  long window;

  // Dimensions of Window
  int width, height;

  // DPI of screen
  float dpi;

  // NanoVG handle
  long nanovg;

  // ArrayList of layers
  // Yes, I know linkedlists are better but I personally dislike them
  ArrayList<UILayer> layers;

  // Last measured frames per second of renderer
  double fps;

  // Logging
  Logger logs;

  /**
   * Constructs a Window with the specified title
   * @param title
   */
  public Window(String title) {
    layers = new ArrayList<>();
    logs = new Logger(this);
    // GLFW
    GLFW.glfwSetErrorCallback((err, descriptionId) -> {
      String errStr = GLFWErrorCallback.getDescription(descriptionId);
      logs.err("GLFW Error %d: %s", err, errStr);
    });
    if(!GLFW.glfwInit()) throw new NativeLibraryException("GLFW.glfwInit failed");
    GLFWVidMode videoMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
    if(videoMode == null) throw new NativeLibraryException("Failed to query monitor size");
    GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_FALSE);
    GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
    GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
    GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 2);
    window = GLFW.glfwCreateWindow(videoMode.width(), videoMode.height(), title, GLFW.glfwGetPrimaryMonitor(), 0);
    if(window == 0) throw new NativeLibraryException("Failed to create window");
    GLFW.glfwMakeContextCurrent(window);
    GLFW.glfwSwapInterval(1);

    // NanoVG
    GL.createCapabilities();
    int[] fw = new int[] { 0 };
    int[] fh = new int[] { 0 };
    GLFW.glfwGetFramebufferSize(window, fw, fh);
    float[] dpix = new float[] { 0.0f };
    float[] dpiy = new float[] { 0.0f };
    GLFW.glfwGetWindowContentScale(window, dpix, dpiy);
    if(Math.abs(dpix[0] - dpiy[0]) > 0.01) throw new RuntimeException("Wierd DPI, not supported");
    width = fw[0];
    height = fh[0];
    dpi = dpix[0];
    nanovg = NanoVGGL3.nvgCreate(NanoVGGL3.NVG_ANTIALIAS | NanoVGGL3.NVG_STENCIL_STROKES | (Util.DEBUG ? NanoVGGL3.NVG_DEBUG : 0));
    NanoVGResources.initialize(nanovg);

    // GLFW callbacks
    GLFW.glfwSetKeyCallback(window, (wnd, key, scancode, action, mods) -> {
      if(key == GLFW.GLFW_KEY_ESCAPE) {
        GLFW.glfwSetWindowShouldClose(wnd, true);
      } else if(action == GLFW.GLFW_PRESS) {
        for(int i = layers.size() - 1; i >= 0; i--) {
          if(!layers.get(i).onKeyPress(this, key, mods)) break;
        }
      }
    });
    GLFW.glfwSetMouseButtonCallback(window, (wnd, button, action, mods) -> {
      for(int i = layers.size() - 1; i >= 0; i--) {
        if(action == GLFW.GLFW_PRESS) {
          if(!layers.get(i).onMousePress(this, button + 1)) break;
        } else if(action == GLFW.GLFW_RELEASE) {
          if(!layers.get(i).onMouseRelease(this, button + 1)) break;
        }
      }
    });
    GLFW.glfwSetCursorPosCallback(window, (wnd, newMouseX, newMouseY) -> {
      for(int i = layers.size() - 1; i >= 0; i--) {
        if(!layers.get(i).onMouseMove(this, newMouseX, newMouseY)) break;
      }
    });
  }

  /**
   * Adds a layer to the top of the UI layer stack
   * @param layer The layer to add
   */
  public void addLayer(UILayer layer) {
    layer.initialize(this);
    layers.add(layer);
  }

  /**
   * Adds a UI layer into the UI layer stack before the specified layer
   * @param id The ID of the specified layer
   * @param layer The layer to add
   */
  public void addLayerBefore(String id, UILayer layer) {
    layer.initialize(this);
    layers.add(layers.stream().map(UILayer::getId).toList().indexOf(id), layer);
  }

  /**
   * Adds a UI layer into the UI layer stack before the specified layer
   * @param id The ID of the specified layer
   * @param layer The layer to add
   */
  public void addLayerAfter(String id, UILayer layer) {
    layer.initialize(this);
    layers.add(layers.stream().map(UILayer::getId).toList().indexOf(id) + 1, layer);
  }

  public void replaceLayer(String id, UILayer layer) {
    for(int i = 0; i < layers.size(); i++) {
      if(layers.get(i).getId().equals(id)) {
        layers.get(i).close();
        layers.set(i, layer);
        break;
      }
    }
  }

  public void removeLayer(String id) {
    for(int i = 0; i < layers.size(); i++) {
      UILayer layer = layers.get(i);
      if(layer.getId().equals(id)) {
        layer.close();
        layers.remove(layer);
        i--;
      }
    }
  }

  public void run() {
    while(!GLFW.glfwWindowShouldClose(window)) {
      GLFW.glfwPollEvents();
      GL.createCapabilities();
      long start = System.nanoTime();
      for (UILayer layer : layers) {
        layer.render(this);
      }
      long end = System.nanoTime();
      fps = 1000000000.0d / (double)(end - start);
      GLFW.glfwSwapBuffers(window);
    }
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  public long getNanoVG() {
    return nanovg;
  }

  public double getDpi() {
    return dpi;
  }

  public double getFps() {
    return fps;
  }

  public long getGlfw() { return window; }
}
