package dev.wateralt.mc.ics4ufinal.common.yaku;

import java.util.Arrays;

public class Honitsu extends Yaku {
  @Override
  public String name() {
    return "Honitsu";
  }

  public boolean matchSuit(ParsedHand hand, char c) {
    return Arrays.stream(hand.objs())
        .map(Yaku::unwrapObj)
        .allMatch(v -> Arrays.stream(v).allMatch(v2 -> v2.getSuit() == 'z' || v2.getSuit() == c)) &&
        (hand.pair().getSuit() == 'z' || hand.pair().getSuit() == c);
  }

  @Override
  protected int match(ParsedHand hand) {
    return (matchSuit(hand, 'm') || matchSuit(hand, 'p') || matchSuit(hand, 's')) ?
        (hand.isOpen() ? 1 : 2)
        : 0;
  }
}
