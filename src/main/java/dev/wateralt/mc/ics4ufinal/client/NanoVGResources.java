package dev.wateralt.mc.ics4ufinal.client;

import dev.wateralt.mc.ics4ufinal.common.Util;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NanoVG;

public class NanoVGResources {
  public static int fontJetBrainsMono = -1;
  public static int fontSourceSansPro = -1;

  public static NVGColor colorDarkBlue = null;
  public static NVGColor colorRed = null;
  public static NVGColor colorWhite = null;

  public static void initialize(long nanovg) {
    fontJetBrainsMono = NanoVG.nvgCreateFont(nanovg, "JetBrainsMono", Util.ASSET_ROOT + "/JetBrainsMono-Regular.ttf");
    fontSourceSansPro = NanoVG.nvgCreateFont(nanovg, "SourceSansPro", Util.ASSET_ROOT + "/SourceSansPro-Regular.ttf");
    colorDarkBlue = NVGColor.calloc().r(0.0f).g(0.0f).b(0.3f).a(1.0f);
    colorRed = NVGColor.calloc().r(1.0f).g(0.0f).b(0.0f).a(1.0f);
    colorWhite = NVGColor.calloc().r(1.0f).g(1.0f).b(1.0f).a(1.0f);
  }
}
