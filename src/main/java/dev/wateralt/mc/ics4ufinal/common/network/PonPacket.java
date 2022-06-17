package dev.wateralt.mc.ics4ufinal.common.network;

// They both have no fields, so no problem extending
public class PonPacket extends BasicTilePacket {
  public static final int ID = 204;

  @Override
  public int getId() {
    return ID;
  }
}
