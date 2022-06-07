package dev.wateralt.mc.ics4ufinal.common;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.util.Date;

public class Logger {
  private static final OutputStream LOG_OUT = System.out;
  private final String name;

  public Logger() {
    this.name = "UnknownSource";
  }

  public Logger(String name) {
    this.name = name;
  }

  public Logger(Object clazz) {
    this.name = clazz.getClass().getName();
  }

  private void outRaw(String level, String msg) {
    ZonedDateTime now = ZonedDateTime.now();
    String out = String.format("[%02d:%02d:%02d] [%s/%s] %s%n",
        now.get(ChronoField.HOUR_OF_DAY),
        now.get(ChronoField.MINUTE_OF_HOUR),
        now.get(ChronoField.SECOND_OF_MINUTE),
        name, level, msg);
    try { LOG_OUT.write(out.getBytes(StandardCharsets.UTF_8)); } catch(IOException ingored) { }
  }

  public void info(String msg, Object... format) {
    outRaw("info", String.format(msg, format));
  }

  public void warn(String msg, Object... format) {
    outRaw("warn", String.format(msg, format));
  }

  public void err(String msg, Object... format) {
    outRaw("err", String.format(msg, format));
  }
}
