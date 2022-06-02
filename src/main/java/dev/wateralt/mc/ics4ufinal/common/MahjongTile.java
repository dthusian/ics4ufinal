package dev.wateralt.mc.ics4ufinal.common;

import java.util.Objects;

public class MahjongTile implements Comparable<MahjongTile> {
  private final int number;
  private final char suit;
  public static final MahjongTile NULL = new MahjongTile(8, 'z');

  public MahjongTile(int n, char s) {
    if(s != 'm' && s != 'p' && s != 's' && s != 'z') {
      throw new IllegalArgumentException("Suit invalid");
    }
    if(n > 9 || n < 1) {
      throw new IllegalArgumentException("Number out of range");
    }
    number = n;
    suit = s;
  }

  public int getNumber() {
    return number;
  }

  public char getSuit() {
    return suit;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    MahjongTile that = (MahjongTile) o;
    return number == that.number && suit == that.suit;
  }

  @Override
  public int hashCode() {
    return Objects.hash(number, suit);
  }

  @Override
  public int compareTo(MahjongTile o) {
    int cmp = Character.compare(this.suit, o.suit);
    if(cmp == 0) {
      return Integer.compare(this.number, o.number);
    }
    return cmp;
  }
}
