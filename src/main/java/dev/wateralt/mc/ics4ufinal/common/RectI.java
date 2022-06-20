package dev.wateralt.mc.ics4ufinal.common;

public record RectI(int x, int y, int w, int h) {
  public boolean inside(int px, int py) {
    return px >= x && px <= x + w && py >= y && py <= y + h;
  }
}
