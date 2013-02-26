package net.sf.geographiclib;

/**
 * <code>LibraryLoader</code> loads the JNI library which implements the functions
 * in C++.
 *
 * @author <a href="mailto:nmaludy@gmail.com">Nick Maludy</a>
 * @version 1.29
 */
public class LibraryLoader {
  static {
    System.loadLibrary("GeographicJni");
  }

  /**
   * The forces the JNI library to be loaded using static initialization.
   * Clients should not need to call this since the classes in this library
   * invoke this function on their own.
   */
  public static void load()
  {}
}
