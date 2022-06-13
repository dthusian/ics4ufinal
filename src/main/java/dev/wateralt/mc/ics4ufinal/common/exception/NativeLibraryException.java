package dev.wateralt.mc.ics4ufinal.common.exception;

/**
 * An umbrella exception type for when something unexpected happens while
 * interacting with a native library
 */
public class NativeLibraryException extends RuntimeException {
  /**
   * Constructs an exception object with the specified message
   * @param s The message
   */
  public NativeLibraryException(String s) {
    super(s);
  }
}
