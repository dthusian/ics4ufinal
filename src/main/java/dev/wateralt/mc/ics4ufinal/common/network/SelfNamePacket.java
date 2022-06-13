package dev.wateralt.mc.ics4ufinal.common.network;

import dev.wateralt.mc.ics4ufinal.common.Util;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * Packet that is sent by clients to indicate they wish to change their name.
 * This packet may be emitted at any point.
 */
public class SelfNamePacket extends Packet {
  public static final int ID = 100;
  String name;

  /**
   * Constructs a default SelfNamePacket
   */
  public SelfNamePacket() {

  }

  /**
   * Constructs a SelfNamePacket with the specified name
   * @param name The name.
   */
  public SelfNamePacket(String name) {
    this.name = name;
  }

  /**
   *
   * @return The ID of the packet type
   */
  @Override
  public int getId() {
    return ID;
  }

  /**
   *
   * @return Serialized content
   */
  @Override
  public ByteBuffer serializeInternal() {
    byte[] bytes = name.getBytes(StandardCharsets.UTF_8);
    ByteBuffer outBytes = ByteBuffer.allocate(4 + bytes.length);
    outBytes.putInt(bytes.length);
    outBytes.put(bytes);
    return outBytes;
  }

  /**
   *
   * @param buf The buffer to deserialize. The contents will be assigned to this object.
   */
  @Override
  public void deserializeInternal(ByteBuffer buf) {
    name = Util.byteBufReadString(buf, 0);
  }

  /**
   *
   * @return Gets the name in this packet
   */
  public String getName() {
    return name;
  }

  /**
   *
   * @param name Sets the name in this packet
   */
  public void setName(String name) {
    this.name = name;
  }
}
