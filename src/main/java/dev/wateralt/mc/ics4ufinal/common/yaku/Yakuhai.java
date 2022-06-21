package dev.wateralt.mc.ics4ufinal.common.yaku;

import dev.wateralt.mc.ics4ufinal.common.MahjongTile;

import java.util.Arrays;

public class Yakuhai extends Yaku {
  @Override
  public String name() {
    return "Yakuhai";
  }

  public static boolean isDragon(MahjongTile tile) {
    return tile.getSuit() == 'z' && (tile.getNumber() == 1 || tile.getNumber() == 5 || tile.getNumber() == 6 || tile.getNumber() == 7);
  }

  @Override
  protected int match(ParsedHand hand) {
    return (int) Arrays.stream(hand.objs()).filter(v -> v instanceof Triple).map(v -> (Triple)v).filter(v -> Yakuhai.isDragon(v.tile())).count();
  }
}
