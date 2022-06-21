package dev.wateralt.mc.ics4ufinal.common.yaku;

import java.util.Arrays;

public class Iipeikou extends Yaku {

  @Override
  public String name() {
    return "Iipeikou";
  }

  @Override
  protected int match(ParsedHand hand) {
    return Arrays.stream(new int[] {
        1,
        2,
        3,
        12,
        13,
        23,
    }).anyMatch(v -> hand.objs()[v % 10].equals(hand.objs()[v / 10])) && !hand.isOpen() ? 1 : 0;
  }
}
