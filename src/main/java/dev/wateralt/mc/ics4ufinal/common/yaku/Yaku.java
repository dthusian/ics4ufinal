package dev.wateralt.mc.ics4ufinal.common.yaku;

import dev.wateralt.mc.ics4ufinal.common.MahjongHand;
import dev.wateralt.mc.ics4ufinal.common.MahjongTile;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class Yaku {
  public record Run(MahjongTile firstTile, boolean shown) { }
  public record Triple(MahjongTile tile, boolean shown) { }
  public record ParsedHand(Object[] objs, MahjongTile pair) {
    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      for(Object o : objs) {
        if(o instanceof Run) {
          Run oDerived = (Run)o;
          sb.append("[ %s %s %s ] ".formatted(
              oDerived.firstTile(),
              oDerived.firstTile().add(1),
              oDerived.firstTile().add(2)
          ));
        } else if(o instanceof Triple) {
          Triple oDerived = (Triple) o;
          sb.append("[ %s %s %s ] ".formatted(
              oDerived.tile(),
              oDerived.tile(),
              oDerived.tile()
          ));
        }
      }
      sb.append("[ %s %s ]".formatted(pair, pair));
      return sb.toString();
    }

    public boolean isOpen() {
      return Arrays.stream(objs).allMatch(v -> {
        if(v instanceof Run) {
          return ((Run)v).shown();
        }
        if(v instanceof Triple) {
          return ((Triple)v).shown();
        }
        throw new RuntimeException();
      });
    }
  }
  public record YakuEntry(Yaku yaku, int han) {
    @Override
    public String toString() {
      return "%s (%d han)".formatted(yaku.name(), han);
    }
  }

  public abstract String name();
  protected abstract int match(ParsedHand hand);

  public static MahjongTile[] unwrapObj(Object o) {
    if(o instanceof Run) {
      Run oDerived = (Run) o;
      return new MahjongTile[] { oDerived.firstTile(), oDerived.firstTile().add(1), oDerived.firstTile().add(2) };
    }
    if(o instanceof Triple) {
      Triple oDerived = (Triple) o;
      return new MahjongTile[] { oDerived.tile(), oDerived.tile(),oDerived.tile() };
    }
    throw new IllegalArgumentException();
  }

  // TODO private
  private static ArrayList<Integer[]> findObjects(List<Integer> numbers) {
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
      boolean valid = true;
      for(int j = 0; j < possibleChis.size(); j++) {
        if((i & (1 << j)) != 0) {
          Integer[] chiTiles = new Integer[] { possibleChis.get(j), possibleChis.get(j) + 1, possibleChis.get(j) + 2 };
          for(int k = 0; k < 3; k++) {
            if(!tmpTiles.contains(chiTiles[k])) {
              valid = false;
              break;
            }
            tmpTiles.remove(chiTiles[k]);
          }
          if(!valid) break;
          sets.add(chiTiles);
        }
      }
      if(!valid) continue;

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
      valid = true;
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

  private static ParsedHand handToObjects(MahjongHand hand) {
    // Convert matched objects of each suit to a harmonized list of matched objects
    ArrayList<Object> objects = new ArrayList<>();
    ArrayList<Integer[]> matchM = findObjects(hand.getHidden().stream().filter(v -> v.getSuit() == 'm').map(v -> v.getNumber()).collect(Collectors.toList()));
    ArrayList<Integer[]> matchP = findObjects(hand.getHidden().stream().filter(v -> v.getSuit() == 'p').map(v -> v.getNumber()).collect(Collectors.toList()));
    ArrayList<Integer[]> matchS = findObjects(hand.getHidden().stream().filter(v -> v.getSuit() == 's').map(v -> v.getNumber()).collect(Collectors.toList()));
    if(matchM == null || matchP == null || matchS == null) return null;
    int nPairs = 0;
    MahjongTile pair = MahjongTile.NULL;
    for(Integer[] t : matchM) {
      if(!Objects.equals(t[0], t[1])) {
        objects.add(new Run(new MahjongTile(t[0], 'm'), false));
      } else if(t.length == 2) {
        nPairs++;
        pair = new MahjongTile(t[0], 'm');
      } else {
        objects.add(new Triple(new MahjongTile(t[0], 'm'), false));
      }
    }
    for(Integer[] t : matchP) {
      if(!Objects.equals(t[0], t[1])) {
        objects.add(new Run(new MahjongTile(t[0], 'p'), false));
      } else if(t.length == 2) {
        nPairs++;
        pair = new MahjongTile(t[0], 'p');
      } else {
        objects.add(new Triple(new MahjongTile(t[0], 'p'), false));
      }
    }
    for(Integer[] t : matchS) {
      if(!Objects.equals(t[0], t[1])) {
        objects.add(new Run(new MahjongTile(t[0], 's'), false));
      } else if(t.length == 2) {
        nPairs++;
        pair = new MahjongTile(t[0], 's');
      } else {
        objects.add(new Triple(new MahjongTile(t[0], 's'), false));
      }
    }
    for(int i = 0; i < hand.getShown().size(); i += 3) {
      if(hand.getShown().get(i).equals(hand.getShown().get(i + 1))) {
        objects.add(new Triple(hand.getShown().get(i), true));
      } else {
        objects.add(new Run(hand.getShown().get(i), true));
      }
    }
    int[] honorsFreq = new int[8];
    for(MahjongTile tile : hand.getHidden().stream().filter(v -> v.getSuit() == 'z').toList()) {
      honorsFreq[tile.getNumber()]++;
    }
    for(int i = 1; i <= 7; i++) {
      if(honorsFreq[i] == 0) {
        continue;
      } else if(honorsFreq[i] == 2) {
        nPairs++;
        pair = new MahjongTile(i, 'z');
      } else if(honorsFreq[i] == 3) {
        objects.add(new Triple(new MahjongTile(i, 'z'), false));
      }
    }
    if(objects.size() != 4) throw new RuntimeException(); // How did this happen
    if(nPairs != 1) return null;
    return new ParsedHand(objects.toArray(), pair);
  }

  // Least efficient Yaku checker ever
  public static ArrayList<YakuEntry> matchHand(MahjongHand hand) {
    Yaku[] yakus = new Yaku[] {
        new Pinfu(),
        new Tanyao(),
        new Iipeikou(),
        new Yakuhai(),
        new Chantaiyao(),
        new Honitsu(),
        new Toitoi()
    };
    ParsedHand hand2 = handToObjects(hand);
    if(hand2 == null) return null;
    ArrayList<YakuEntry> entries = new ArrayList<>();
    for(Yaku yaku : yakus) {
      int matched = yaku.match(hand2);
      if(matched > 0) {
        entries.add(new YakuEntry(yaku, matched));
      }
    }
    return entries;
  }
}
