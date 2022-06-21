package dev.wateralt.mc.ics4ufinal.common.network;

import dev.wateralt.mc.ics4ufinal.common.MahjongHand;

public class WinPacketS2C extends UpdateHandPacket {
  public static int ID = 207;

  public WinPacketS2C() {
    super();
  }

  public WinPacketS2C(MahjongHand hand, int player) {
    super(hand, player, false);
  }

  @Override
  public int getId() {
    return ID;
  }
}
