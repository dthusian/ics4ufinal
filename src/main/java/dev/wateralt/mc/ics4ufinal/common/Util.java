package dev.wateralt.mc.ics4ufinal.common;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class Util {
  public static final boolean DEBUG = true;

  public static String ASSET_ROOT = "assets/";

  public static final Random RANDOM = new Random();

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

  public static String byteBufReadString(ByteBuffer buf, int idx) {
    int length = buf.getInt(idx);
    byte[] strBuf = new byte[length];
    buf.get(strBuf, idx + 4, length);
    return new String(strBuf);
  }

  public static ByteBuffer readSinglePacket(InputStream stream) throws IOException {
    ByteBuffer lengthBuf = ByteBuffer.allocate(4);
    int read = stream.read(lengthBuf.array(), 0, 4);
    if(read != 4) throw new IOException();
    int length = lengthBuf.getInt(0);
    ByteBuffer buf = ByteBuffer.allocate(length + 8);
    buf.putInt(read);
    read = stream.read(buf.array(), 4, length + 4);
    if(read != length + 4) throw new IOException();
    return buf;
  }
}
