package dev.wateralt.mc.ics4ufinal.common.network;

public class WinPacketC2S extends NoActionPacket {
  public static int ID = 206;

  @Override
  public int getId() {
    return ID;
  }
}
