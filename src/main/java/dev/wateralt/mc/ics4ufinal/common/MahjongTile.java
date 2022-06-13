package dev.wateralt.mc.ics4ufinal.common;

import java.util.Objects;

/**
 * Represents a single mahjong tile. Each tile is represented with an internal ID,
 * for which the tens digit is the suit and the ones digit is the number.
 */
public class MahjongTile implements Comparable<MahjongTile> {
  private final int number;
  public static final MahjongTile NULL = new MahjongTile(8, 'z');

  /**
   * Constructs a tile with the specified number and suit
   * @param n The number 1-9 of the tile
   * @param s The suit, one of "mpsz"
   */
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

  /**
   * Constructs a tile from an internal ID
   * @param id The internal ID
   */
  public MahjongTile(int id) {
    if(id <= 0 || id == 10 || id == 20 || id == 30 || id == 40 || id >= 49) {
      throw new IllegalArgumentException("Invalid tile");
    }
    number = id;
  }

  /**
   * Constructs a tile using the [1-9][mpsz] notation.
   * @param s The string specifying a tile.
   */
  public MahjongTile(String s) {
    this(Integer.parseInt(Character.toString(s.charAt(0))), s.charAt(1));
  }

  /**
   *
   * @return the numerical portion of the tile.
   */
  public int getNumber() {
    return number % 10;
  }

  /**
   *
   * @return the suit of the tile. One of "mpsz"
   */
  public char getSuit() {
    return "mpsz".charAt(number / 10);
  }

  /**
   *
   * @return the internal ID of this tile. 1-38
   */
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
