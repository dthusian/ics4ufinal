package dev.wateralt.mc.ics4ufinal.common.yaku;

import java.util.Arrays;

public class Chantaiyao extends Yaku {

  @Override
  public String name() {
    return "Chantaiyao";
  }

  @Override
  protected int match(ParsedHand hand) {
    return (Arrays.stream(hand.objs()).map(Yaku::unwrapObj).allMatch(v -> Arrays.stream(v).anyMatch(v2 -> !Tanyao.isSimple(v2))) && !Tanyao.isSimple(hand.pair())) ?
        (hand.isOpen() ? 1 : 2)
        : 0;
  }
}
