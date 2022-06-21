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
  public static final String ASSET_ROOT = "assets/";

  public static final int SIGNAL_START = 0;
  public static final int SIGNAL_STOP = 1;
  public static final int SIGNAL_SUSPEND = 2;

  public record Tuple2<T1, T2>(T1 x, T2 y) { }

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
    buf.get(idx + 4, strBuf, 0, length);
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
   * Stringifies the contents of a packet. In the past this method revealed more information
   * about the packet, but that relied on buggy network code that was often
   * the thing being debugged. Now, it's just an alias of byteBufToString
   * @param buf The packet
   * @return
   */
  public static String debugPrintPacket(ByteBuffer buf) {
    return Util.byteBufToString(buf);
  }

  /**
   * Converts a byte buffer to hex representation.
   * @param buf The buffer to convert.
   * @return The converted buffer.
   */
  public static String byteBufToString(ByteBuffer buf) {
    StringBuilder sb = new StringBuilder();
    for(int i = 0; i < buf.limit(); i++) {
       sb.append(String.format("%02x ", buf.get(i)));
    }
    return sb.toString();
  }

  /**
   * How the fuck does Java manage to design yet another bad API look at how many Util methods
   * deal with ByteBuffers and tell me that it shouldn't be burned to the ground and redesigned
   *
   * Converts a byte buffer to a byte array according to its offset and limit, not according to
   * its underlying array (which is what .array() does)
   * @param buf The buffer to convert
   * @return The byte array
   */
  public static byte[] byteBufToByteArray(ByteBuffer buf) {
    byte[] arr = new byte[buf.limit()];
    for(int i = 0; i < arr.length; i++) {
      arr[i] = buf.get(i);
    }
    return arr;
  }
}
