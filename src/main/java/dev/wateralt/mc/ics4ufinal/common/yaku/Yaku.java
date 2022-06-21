package dev.wateralt.mc.ics4ufinal.common.yaku;

import dev.wateralt.mc.ics4ufinal.common.MahjongHand;
import dev.wateralt.mc.ics4ufinal.common.MahjongTile;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class Yaku {
  public record Run(MahjongTile[] tiles, boolean shown) {}
  public record Triple(MahjongTile tile, boolean shown) {}
  public record YakuEntry(Yaku yaku, int han) {}

  public abstract String name();
  protected abstract int match(Object[] entries, MahjongTile pair);

  // TODO private
  public static ArrayList<Integer[]> findObjects(List<Integer> numbers) {
    ArrayList<Integer> tiles = new ArrayList<>(numbers);
    Set<Integer> tileSet = new HashSet<>(numbers);

    ArrayList<Integer> possibleChis = new ArrayList<>();
    for(int i = 1; i <= 7; i++) {
      if(tileSet.contains(i) && tileSet.contains(i + 1) && tileSet.contains(i + 2)) possibleChis.add(i);
    }

    // For each possible combination of chis, create a new array and check its validity
    int twoToTheN = 1 << possibleChis.size();
    for(int i = 0; i < twoToTheN; i++) {
      ArrayList<Integer> tmpTiles = new ArrayList<>(numbers);

      ArrayList<Integer[]> sets = new ArrayList<>();
      for(int j = 0; j < possibleChis.size(); j++) {
        if((i & (1 << j)) != 0) {
          Integer[] chiTiles = new Integer[] { possibleChis.get(j), possibleChis.get(j) + 1, possibleChis.get(j) + 2 };
          for(int k = 0; k < 3; k++) tmpTiles.remove(chiTiles[k]);
          sets.add(chiTiles);
        }
      }

      // Frequency List
      HashMap<Integer, Integer> freq = new HashMap<>();
      for(int j : tmpTiles) {
        if(!freq.containsKey(j)) {
          freq.put(j, 1);
        } else {
          freq.put(j, freq.get(j) + 1);
        }
      }
      // Check triples for validity
      boolean pairUsed = false;
      boolean valid = true;
      for(int key : freq.keySet()) {
        if(freq.get(key) % 3 == 0) {
          for(int k = 0; k < freq.get(key); k += 3) sets.add(new Integer[] { key, key, key });
        } else if(freq.get(key) % 3 == 2 && !pairUsed) {
          pairUsed = true;
          for(int k = 0; k < freq.get(key) - 2; k += 3) sets.add(new Integer[] { key, key, key });
          sets.add(new Integer[] { key, key });
        } else {
          // Not possible to construct
          valid = false;
          break;
        }
      }
      if(valid) return sets;
    }
    return null;
  }

  // Least efficient Yaku checker ever
  public static ArrayList<YakuEntry> matchHand(MahjongHand hand) {
    // Convert matched objects of each suit to a harmonized list of matched objects
    ArrayList<Object> objects = new ArrayList<>();
    ArrayList<Integer[]> matchM = findObjects(hand.getHidden().stream().filter(v -> v.getSuit() == 'm').map(v -> v.getNumber()).collect(Collectors.toList()));
    ArrayList<Integer[]> matchP = findObjects(hand.getHidden().stream().filter(v -> v.getSuit() == 'p').map(v -> v.getNumber()).collect(Collectors.toList()));
    ArrayList<Integer[]> matchS = findObjects(hand.getHidden().stream().filter(v -> v.getSuit() == 'p').map(v -> v.getNumber()).collect(Collectors.toList()));
    if(matchM == null || matchP == null || matchS == null) return null;
    int nPairs = 0;
    for(Integer[] t : matchM) {
      if(!Objects.equals(t[0], t[1])) {
        List<MahjongTile> a0 = Arrays.stream(t).map(v -> new MahjongTile(v, 'm')).toList();
        MahjongTile[] a1 = new MahjongTile[a0.size()];
        a0.toArray(a1);
        objects.add(new Run(a1, false));
      } else if(t.length == 2) {
        nPairs++;
      } else {
        objects.add(new Triple(new MahjongTile(t[0], 'm'), false));
      }
    }
    for(Integer[] t : matchP) {
      if(!Objects.equals(t[0], t[1])) {
        List<MahjongTile> a0 = Arrays.stream(t).map(v -> new MahjongTile(v, 'p')).toList();
        MahjongTile[] a1 = new MahjongTile[a0.size()];
        a0.toArray(a1);
        objects.add(new Run(a1, false));
      } else if(t.length == 2) {
        nPairs++;
      } else {
        objects.add(new Triple(new MahjongTile(t[0], 'p'), false));
      }
    }
    for(Integer[] t : matchS) {
      if(!Objects.equals(t[0], t[1])) {
        List<MahjongTile> a0 = Arrays.stream(t).map(v -> new MahjongTile(v, 's')).toList();
        MahjongTile[] a1 = new MahjongTile[a0.size()];
        a0.toArray(a1);
        objects.add(new Run(a1, false));
      } else if(t.length == 2) {
        nPairs++;
      } else {
        objects.add(new Triple(new MahjongTile(t[0], 's'), false));
      }
    }
    if(nPairs > 1) return null;
    throw new UnsupportedOperationException();
  }
}
