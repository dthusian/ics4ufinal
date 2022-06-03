package dev.wateralt.mc.ics4ufinal.common;

import java.util.Objects;

public class MahjongTile implements Comparable<MahjongTile> {
  private final int number;
  public static final MahjongTile NULL = new MahjongTile(8, 'z');

  public MahjongTile(int n, char s) {
    if(n > 9 || n < 1) {
      throw new IllegalArgumentException("Invalid tile");
    }
    if(s == 'm') {
      number = n;
    } else if(s == 'p') {
      number = 10 + n;
    } else if(s == 's') {
      number = 20 + n;
    } else if(s == 'z') {
      number = 30 + n;
    } else {
      throw new RuntimeException();
    }
  }

  public MahjongTile(int id) {
    if(id <= 0 || id == 10 || id == 20 || id == 30 || id == 40 || id >= 49) {
      throw new IllegalArgumentException("Invalid tile");
    }
    number = id;
  }

  public int getNumber() {
    return number % 10;
  }

  public char getSuit() {
    return "mpsz".charAt(number / 10);
  }

  public int getInternal() {
    return number;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    MahjongTile that = (MahjongTile) o;
    return number == that.number;
  }

  @Override
  public int hashCode() {
    return Objects.hash(number);
  }

  @Override
  public int compareTo(MahjongTile o) {
    return Integer.compare(this.getInternal(), o.getInternal());
  }
}
