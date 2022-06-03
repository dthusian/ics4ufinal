package dev.wateralt.mc.ics4ufinal.client.uilayers;

import dev.wateralt.mc.ics4ufinal.client.Window;
import dev.wateralt.mc.ics4ufinal.common.Util;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.opengl.GL32;

import java.time.Instant;
import java.time.temporal.TemporalField;
import java.util.Date;

public class DebugLayer implements UILayer {
  private static class DebugPrinter {
    int posY = 25;
    long nanovg;
    public DebugPrinter(long nanovg) {
      this.nanovg = nanovg;
    }
    public void print(String fmt, Object... obj) {
      NanoVG.nvgText(this.nanovg , 0, posY, fmt.formatted(obj));
      posY += 25;
    }
  }

  int debugFont;

  // Debug info
  int lastUpdatedSecond;
  String osName;
  String osArch;
  String glVendorName;
  String glRendererName;
  String glVersion;
  String javaVersion;
  String javaVendor;
  long memUsed;
  long memAllocated;
  long memMax;

  public DebugLayer() {
    debugFont = 0;
    lastUpdatedSecond = -1;
    osName = System.getProperty("os.name");
    osArch = System.getProperty("os.arch");
    javaVersion = System.getProperty("java.version");
    javaVendor = System.getProperty("java.vendor");
  }

  @Override
  public void initialize(Window wnd) {
    debugFont = NanoVG.nvgCreateFont(wnd.getNanoVG(), "Jetbrains Mono", Util.ASSET_ROOT + "/JetBrainsMono-Regular.ttf");
    glVendorName = GL32.glGetString(GL32.GL_VENDOR);
    glRendererName = GL32.glGetString(GL32.GL_RENDERER);
    glVersion = GL32.glGetString(GL32.GL_VERSION);
  }

  @Override
  public String getId() {
    return "DebugLayer";
  }

  public void updateInfo() {
    int currentSecond = Date.from(Instant.now()).getSeconds();
    if(lastUpdatedSecond != currentSecond) {
      lastUpdatedSecond = currentSecond;
      memUsed = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
      memAllocated = Runtime.getRuntime().totalMemory();
      memMax = Runtime.getRuntime().maxMemory();
    }
  }

  @Override
  public void render(Window wnd) {
    updateInfo();

    GL32.glEnable(GL32.GL_STENCIL_TEST);
    GL32.glClear(GL32.GL_STENCIL_BUFFER_BIT);
    long nanovg = wnd.getNanoVG();
    NanoVG.nvgBeginFrame(nanovg, (float) wnd.getWidth(), (float) wnd.getHeight(), (float) wnd.getDpi());

    NVGColor red = NVGColor.malloc().r(1.0f).g(0.0f).b(0.0f).a(1.0f);
    NanoVG.nvgFillColor(nanovg, red);
    NanoVG.nvgStrokeColor(nanovg, red);
    NanoVG.nvgFontFaceId(nanovg, debugFont);
    NanoVG.nvgFontSize(nanovg, 25);

    DebugPrinter pr = new DebugPrinter(nanovg);
    pr.print("CS Mahjong Debug");
    pr.print("OS: %s %s", osName, osArch);
    pr.print("Java: %s %s", javaVendor, javaVersion);
    pr.print("OpenGL Version: %s", glVersion);
    pr.print("OpenGL Vendor: %s", glVendorName);
    pr.print("OpenGL Renderer: %s", glRendererName);
    pr.print("FPS: %.2f", wnd.getFps());
    pr.print("Memory: %dM used / %dM alloc / %dM max", memUsed / 1000000, memAllocated / 1000000, memMax / 1000000);

    red.free();

    NanoVG.nnvgEndFrame(nanovg);
  }
}
