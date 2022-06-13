package dev.wateralt.mc.ics4ufinal.common;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.util.Date;

/**
 * <h2>Logger</h2>
 *
 * Utility class for outputting structured logs
 * You should use the Logger(Object) constructor as it
 * initializes the logger's name to the calling class.
 */
public class Logger {
  private static final OutputStream LOG_OUT = System.out;
  private final String name;

  /**
   * Constructs a logger with a generic name.
   */
  public Logger() {
    this.name = "UnknownSource";
  }

  /**
   * Constructs a logger with the specified name.
   * @param name The specified name.
   */
  public Logger(String name) {
    this.name = name;
  }

  /**
   * Constructs a logger with the name of the class of the object.
   * Usually used like new Logger(this) to construct a logger with the same name as the calling class.
   * @param clazz The object to read the class name from.
   */
  public Logger(Object clazz) {
    this.name = clazz.getClass().getSimpleName();
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

  /**
   * Logs an info message to the console.
   * @param msg The message to log
   * @param format Formatting arguments
   */
  public void info(String msg, Object... format) {
    outRaw("info", String.format(msg, format));
  }

  /**
   * Logs a warning to the console.
   * @param msg The message to log
   * @param format Formatting arguments
   */
  public void warn(String msg, Object... format) {
    outRaw("warn", String.format(msg, format));
  }

  /**
   * Logs an error to the console.
   * @param msg The message to log
   * @param format Formatting arguments
   */
  public void err(String msg, Object... format) {
    outRaw("err", String.format(msg, format));
  }
}
