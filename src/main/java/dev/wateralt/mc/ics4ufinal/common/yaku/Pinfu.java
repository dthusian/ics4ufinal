package dev.wateralt.mc.ics4ufinal.common.yaku;

import java.util.Arrays;

public class Pinfu extends Yaku {

  @Override
  public String name() {
    return "Pinfu";
  }

  @Override
  protected int match(ParsedHand hand) {
    if(hand.isOpen()) return 0;
    if(Arrays.stream(hand.objs()).allMatch(v -> v instanceof Run)) return 1;
    return 0;
  }
}
