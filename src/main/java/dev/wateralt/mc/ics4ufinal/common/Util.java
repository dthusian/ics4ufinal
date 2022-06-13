package dev.wateralt.mc.ics4ufinal.common;

import dev.wateralt.mc.ics4ufinal.common.network.Packet;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Random utility functions.
 */
public class Util {
  /**
   * True if debugging, false otherwise.
   */
  public static final boolean DEBUG = true;

  /**
   * Returns the directory containing all asset files.
   */
  public static String ASSET_ROOT = "assets/";

  /**
   * Reads all the contents of a file into a single string
   * @param path The path of the file to read
   * @return The contents of the file
   */
  public static String slurp(String path) {
    try {
      FileInputStream inputStream = new FileInputStream(path);
      String s = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
      inputStream.close();
      return s;
    } catch(IOException err) {
      throw new RuntimeException(err);
    }
  }

  /**
   * Reads a pascal-string from a byte buffer
   * @param buf The buffer to read from.
   * @param idx The starting index of the string
   * @return The read string.
   */
  public static String byteBufReadString(ByteBuffer buf, int idx) {
    int length = buf.getInt(idx);
    byte[] strBuf = new byte[length];
    buf.get(strBuf, idx + 4, length);
    return new String(strBuf);
  }

  /**
   * Reads a single packet from the input stream based on the length read.
   * @param stream The stream to read from
   * @return The bytebuffer containing the new packet
   * @throws IOException
   */
  public static ByteBuffer readSinglePacket(InputStream stream) throws IOException {
    ByteBuffer lengthBuf = ByteBuffer.allocate(4);
    int read = stream.read(lengthBuf.array(), 0, 4);
    if(read != 4) throw new IOException();
    int length = lengthBuf.getInt(0);
    ByteBuffer buf = ByteBuffer.allocate(length + 8);
    buf.putInt(length);
    read = stream.read(buf.array(), 4, length + 4);
    if(read != length + 4) throw new IOException();
    return buf;
  }

  /**
   * Stringifies the contents of a packet.
   * @param buf The packet
   * @return
   */
  public static String debugPrintPacket(ByteBuffer buf) {
    Packet p = Packet.deserialize(buf);
    return String.format("Type: %s Length: %d Bytes: %s",
        p.getClass().getSimpleName(),
        buf.capacity(),
        byteBufToString(buf));
  }

  /**
   * Converts a byte[] to a Byte[], so that it can be
   * used in generic methods. I hate Java.
   * @param arr The byte[]
   * @return The Byte[]
   */
  public static Byte[] byteArrayToByteArray(byte[] arr) {
    Byte[] arr2 = new Byte[arr.length];
    for(int i = 0; i < arr.length; i++) {
      arr2[i] = arr[i];
    }
    return arr2;
  }

  /**
   * Converts a byte buffer to hex representation.
   * @param buf The buffer to convert.
   * @return The converted buffer.
   */
  public static String byteBufToString(ByteBuffer buf) {
    return Arrays.stream(Util.byteArrayToByteArray(buf.array()))
        .map("%02x "::formatted)
        .collect(Collectors.joining());
  }
}
