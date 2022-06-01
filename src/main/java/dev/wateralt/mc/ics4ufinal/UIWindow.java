// UIWindow.java - NanoVG+GLFW UI renderer that can also render OpenGL scene

package dev.wateralt.mc.ics4ufinal;

import dev.wateralt.mc.ics4ufinal.exception.NativeLibraryException;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.nanovg.NanoVGGL3;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL32;

public class UIWindow {
  GLFWVidMode videoMode;
  long window;
  long nanovg;
  MahjongRenderer renderer;
  int width, height;
  float dpi;
  int debugFont;

  public UIWindow(String title) {
    // GLFW
    GLFWErrorCallback.createPrint().set();
    if(!GLFW.glfwInit()) throw new NativeLibraryException("GLFW.glfwInit failed");
    videoMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
    if(videoMode == null) throw new NativeLibraryException("Failed to query monitor size");
    GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_FALSE);
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
    debugFont = NanoVG.nvgCreateFont(nanovg, "Jetbrains Mono", Util.ASSET_ROOT + "/JetBrainsMono-Regular.ttf");
  }

  public void setRenderer(MahjongRenderer renderer) {
    this.renderer = renderer;
  }

  public void run() {
    while(!GLFW.glfwWindowShouldClose(window)) {
      // OpenGL rendering
      long start = System.nanoTime();
      GL32.glEnable(GL32.GL_DEPTH_TEST);
      GL32.glClearColor(0.0f, 1.0f, 1.0f, 0.0f);
      GL32.glClear(GL32.GL_COLOR_BUFFER_BIT | GL32.GL_DEPTH_BUFFER_BIT | GL32.GL_STENCIL_BUFFER_BIT);
      this.renderer.drawGL();
      long end = System.nanoTime();
      double fps = 1000000000.0d / (double)(end - start);

      // NanoVG rendering
      GL.createCapabilities();
      GL32.glEnable(GL32.GL_STENCIL_TEST);
      GL32.glClear(GL32.GL_STENCIL_BUFFER_BIT);
      NanoVG.nvgBeginFrame(nanovg, width, height, dpi);
      this.renderer.drawVG(nanovg);
      if(Util.DEBUG) {
        NVGColor red = NVGColor.malloc().r(1.0f).g(0.0f).b(0.0f).a(1.0f);
        NanoVG.nvgFillColor(nanovg, red);
        NanoVG.nvgStrokeColor(nanovg, red);
        NanoVG.nvgFontFaceId(nanovg, debugFont);
        NanoVG.nvgFontSize(nanovg, 25);

        NanoVG.nvgBeginPath(nanovg);
        NanoVG.nvgRect(nanovg, 100, 100, 100, 100);
        NanoVG.nvgFill(nanovg);
        NanoVG.nvgClosePath(nanovg);

        NanoVG.nvgBeginPath(nanovg);
        NanoVG.nvgText(nanovg, 300, 400, "FPS: %.2f".formatted(fps));
        NanoVG.nvgStroke(nanovg);
        NanoVG.nvgClosePath(nanovg);
        red.free();
      }
      NanoVG.nnvgEndFrame(nanovg);

      // GLFW
      GLFW.glfwSwapBuffers(window);
    }
  }
}
