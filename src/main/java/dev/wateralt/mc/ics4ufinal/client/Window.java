// UIWindow.java - NanoVG+GLFW UI renderer that can also render OpenGL scene

package dev.wateralt.mc.ics4ufinal.client;

import dev.wateralt.mc.ics4ufinal.client.uilayers.UILayer;
import dev.wateralt.mc.ics4ufinal.common.Util;
import dev.wateralt.mc.ics4ufinal.common.exception.NativeLibraryException;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.nanovg.NanoVGGL3;
import org.lwjgl.opengl.GL;

import java.util.ArrayList;

public class Window {
  // Window State
  long window;
  int width, height;
  float dpi;
  long nanovg;
  ArrayList<UILayer> layers;
  double fps;

  public Window(String title) {
    layers = new ArrayList<>();
    // GLFW
    GLFWErrorCallback.createPrint().set();
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
    GLFW.glfwSwapInterval(0);

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

    // GLFW callbacks
    GLFW.glfwSetKeyCallback(window, (wnd, key, scancode, action, mods) -> {
      if(key == GLFW.GLFW_KEY_ESCAPE) {
        GLFW.glfwSetWindowShouldClose(wnd, true);
      }
    });
    GLFW.glfwSetMouseButtonCallback(window, (wnd, button, action, mods) -> {
      for(int i = layers.size() - 1; i >= 0; i--) {
        if(action == GLFW.GLFW_PRESS) {
          layers.get(i).onMousePress(this, button + 1);
        } else if(action == GLFW.GLFW_RELEASE) {
          layers.get(i).onMouseRelease(this, button + 1);
        }
      }
    });
    GLFW.glfwSetCursorPosCallback(window, (wnd, newMouseX, newMouseY) -> {
      for(int i = layers.size() - 1; i >= 0; i--) {
        layers.get(i).onMouseMove(this, newMouseX, newMouseY);
      }
    });
  }

  public void addLayer(UILayer layer) {
    layer.initialize(this);
    layers.add(layer);
  }

  public void replaceLayer(String id, UILayer layer) {
    for(int i = 0; i < layers.size(); i++) {
      if(layers.get(i).getId().equals(id)) {
        layers.set(i, layer);
        break;
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
}
