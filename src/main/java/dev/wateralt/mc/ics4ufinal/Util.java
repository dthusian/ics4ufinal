package dev.wateralt.mc.ics4ufinal;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class Util {
  public static final boolean DEBUG = true;

  public static String ASSET_ROOT = "assets/";

  public static final Random RANDOM = new Random();

  public static String slurp(String path) throws IOException {
    FileInputStream inputStream = new FileInputStream(path);
    String s = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    inputStream.close();
    return s;
  }
}
