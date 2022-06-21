package dev.wateralt.mc.ics4ufinal.common.yaku;

import dev.wateralt.mc.ics4ufinal.common.MahjongTile;

import java.util.Arrays;

public class Tanyao extends Yaku {
  @Override
  public String name() {
    return "Tanyao";
  }

  public static boolean isSimple(MahjongTile tile) {
    return tile.getSuit() != 'z' && tile.getNumber() >= 2 && tile.getNumber() <= 8;
  }

  @Override
  protected int match(ParsedHand hand) {
    return (Arrays.stream(hand.objs()).map(Yaku::unwrapObj).allMatch(v -> Arrays.stream(v).allMatch(Tanyao::isSimple)) && isSimple(hand.pair())) ? 1 : 0;
  }
}
