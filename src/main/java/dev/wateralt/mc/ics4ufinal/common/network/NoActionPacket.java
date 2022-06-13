package dev.wateralt.mc.ics4ufinal.common.network;

import java.nio.ByteBuffer;

/**
 * Packet that is sent by a client to indicate it will not take any action
 * following another client discarding a tile.
 *
 * This packet has no fields.
 */
public class NoActionPacket extends Packet {
  public static int ID = 202;

  /**
   * Constructs an empty packet
   */
  public NoActionPacket() { }

  /**
   *
   * @return The ID of the packet type.
   */
  @Override
  public int getId() {
    return ID;
  }

  /**
   *
   * @return an empty buffer (this packet has no content)
   */
  @Override
  public ByteBuffer serializeInternal() {
    return ByteBuffer.allocate(0);
  }

  /**
   *
   * @param buf The buffer to deserialize. The contents will be assigned to this object.
   */
  @Override
  public void deserializeInternal(ByteBuffer buf) { }
}
