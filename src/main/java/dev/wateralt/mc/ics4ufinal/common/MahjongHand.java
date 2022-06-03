package dev.wateralt.mc.ics4ufinal.common;

import java.util.ArrayList;
import java.util.Objects;

public class MahjongHand {
  // Yes. I know LinkedList works better here.
  // I dislike LinkedList for personal reasons.
  // Traversing a LinkedList might be slower because it plays worse with CPU caching (because non-locality)

  private final ArrayList<MahjongTile> hidden;
  private final ArrayList<MahjongTile> hiddenKan;
  private final ArrayList<MahjongTile> shown;
  private MahjongTile finalTile;

  public MahjongHand(ArrayList<MahjongTile> initial) {
    hidden = initial;
    hiddenKan = new ArrayList<>();
    shown = new ArrayList<>();
    finalTile = MahjongTile.NULL;
  }

  public void clear() {
    hidden.clear();
    hiddenKan.clear();
    finalTile = MahjongTile.NULL;
  }

  public ArrayList<MahjongTile> getHidden() {
    return hidden;
  }

  public ArrayList<MahjongTile> getHiddenKan() {
    return hiddenKan;
  }

  public ArrayList<MahjongTile> getShown() {
    return shown;
  }

  public MahjongTile getFinalTile() {
    return finalTile;
  }

  public void setFinalTile(MahjongTile finalTile) {
    this.finalTile = finalTile;
  }
  public int getLength() {
    return hidden.size() + hiddenKan.size() + shown.size() + (finalTile == null ? 0 : 1);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    MahjongHand that = (MahjongHand) o;
    return Objects.equals(hidden, that.hidden) && Objects.equals(hiddenKan, that.hiddenKan) && Objects.equals(shown, that.shown) && Objects.equals(finalTile, that.finalTile);
  }

  @Override
  public int hashCode() {
    return Objects.hash(hidden, hiddenKan, shown, finalTile);
  }
}
