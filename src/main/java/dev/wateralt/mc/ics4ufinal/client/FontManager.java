package dev.wateralt.mc.ics4ufinal.client;

import dev.wateralt.mc.ics4ufinal.common.Util;
import org.lwjgl.nanovg.NanoVG;

public class FontManager {
  public static int fontJetBrainsMono = -1;
  public static int fontSourceSansPro = -1;

  public static void initialize(long nanovg) {
    fontJetBrainsMono = NanoVG.nvgCreateFont(nanovg, "JetBrainsMono", Util.ASSET_ROOT + "/JetBrainsMono-Regular.ttf");
    fontSourceSansPro = NanoVG.nvgCreateFont(nanovg, "SourceSansPro", Util.ASSET_ROOT + "/SourceSansPro-Regular.ttf");
  }
}
