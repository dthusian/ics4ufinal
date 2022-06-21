package dev.wateralt.mc.ics4ufinal.common.yaku;

import java.util.Arrays;

public class Toitoi extends Yaku {
  @Override
  public String name() {
    return "Toitoi";
  }

  @Override
  protected int match(ParsedHand hand) {
    return Arrays.stream(hand.objs()).allMatch(v -> v instanceof Triple) ? 2 : 0;
  }
}
