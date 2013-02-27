package net.sf.geographiclib;

import java.io.Closeable;

/**
 * <code>Geodesic</code> wrapper for the C++ <a href="http://geographiclib.sourceforge.net/html/classGeographicLib_1_1Geodesic.html">GeographicLib::Geodesic</a> class.
 *
 * This object managers resources internally, however since Java lacks a
 * destructor the {@link #finalize() finalize()} method is relied in.
 * Finalize is only guaranteed to be called when the garbage collector destroys
 * this object, however when that will happen is unspecified.
 * If one wishes to dispose of the resources more promptly one can call
 * the {@link #close() close()} method which will clean up all of
 * this classes resources. In Java 7 one can also use this class in a
 * <a href="http://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html">"try-with-resources" statement</a> since <code>Geodesic</code> implements {@link java.io.Closeable}
 *
 * @author <a href="mailto:nmaludy@gmail.com">Nick Maludy</a>
 * @version 1.29
 */
public class Geodesic implements Closeable {

  static {
    // force the GeographicLib JNI library to be loaded before anything below
    // is executed
    LibraryLoader.load();
    staticInit();
  }

  private static final long CAP_NONE = 0;
  private static final long CAP_C1   = 1 << 0;
  private static final long CAP_C1p  = 1 << 1;
  private static final long CAP_C2   = 1 << 2;
  private static final long CAP_C3   = 1 << 3;
  private static final long CAP_C4   = 1 << 4;
  private static final long CAP_ALL  = 0x1F;
  private static final long OUT_ALL  = 0x7F80;

  /**
   * No capabilities, no output.
   **********************************************************************/
  public static final long MASK_NONE          = 0;
  /**
   * Calculate latitude \e lat2.  (It's not necessary to include this as a
   * capability to GeodesicLine because this is included by default.)
   **********************************************************************/
  public static final long MASK_LATITUDE      = 1 << 7  | CAP_NONE;
  /**
   * Calculate longitude \e lon2.
   **********************************************************************/
  public static final long MASK_LONGITUDE     = 1 << 8  | CAP_C3;
  /**
   * Calculate azimuths \e azi1 and \e azi2.  (It's not necessary to
   * include this as a capability to GeodesicLine because this is included
   * by default.)
   **********************************************************************/
  public static final long MASK_AZIMUTH       = 1 << 9  | CAP_NONE;
  /**
   * Calculate distance \e s12.
   **********************************************************************/
  public static final long MASK_DISTANCE      = 1 << 10 | CAP_C1;
  /**
   * Allow distance \e s12 to be used as input in the direct geodesic
   * problem.
   **********************************************************************/
  public static final long MASK_DISTANCE_IN   = 1 << 11 | CAP_C1 | CAP_C1p;
  /**
   * Calculate reduced length \e m12.
   **********************************************************************/
  public static final long MASK_REDUCEDLENGTH = 1 << 12 | CAP_C1 | CAP_C2;
  /**
   * Calculate geodesic scales \e M12 and \e M21.
   **********************************************************************/
  public static final long MASK_GEODESICSCALE = 1 << 13 | CAP_C1 | CAP_C2;
  /**
   * Calculate area \e S12.
   **********************************************************************/
  public static final long MASK_AREA          = 1 << 14 | CAP_C4;
  /**
   * All capabilities.  Calculate everything.
   **********************************************************************/
  public static final long MASK_ALL           = OUT_ALL | CAP_ALL;
  /**
   * A global instantiation of Geodesic with the parameters for the
   * WGS84 ellipsoid.
   **********************************************************************/
  public static final Geodesic WGS84 = newGeodesicWgs84Object();

  /**
   * <code>geodesicCppPtr</code> is a pointer to the wrapped C++
   * GeographicLib::Geodesic object created by newGeodesicCppObject().
   */
  private long geodesicCppPtr;

  /**
   * Default constructor, marked private so clients can't create
   * an instance without an area or flattening
   */
  private Geodesic() {
    // null by default
    geodesicCppPtr = 0;
  }

  /**
   * Creates a new <code>Geodesic</code> instance.
   * See the <a href="http://geographiclib.sourceforge.net/html/classGeographicLib_1_1Geodesic.html">C++ documenation for Geodesic</a> for more information.
   *
   * @param a equatorial radius (meters).
   * @param f flattening of ellipsoid. Setting f = 0 gives a sphere.
   *      Negative f gives a prolate ellipsoid. If f > 1, set flattening to 1/f.
   */
  public Geodesic(final double a, final double f) {
    // create the underlying Geodesic C++ object
    geodesicCppPtr = newGeodesicCppObject(a, f);
  }

  //////////////////////////////////////////////////////////////////////////////
  // The following functions are used to help wrap and manager the underlying //
  // C++ pointer that this class holds                                        //
  //////////////////////////////////////////////////////////////////////////////
  /** 
   * @brief Initializes the native classes static variables
   */
  private static native void staticInit();
  
  /**
   * Creates a new GeographicLib::Geodesic object and returns the pointer to it.
   * @param a equatorial radius (meters).
   * @param f flattening of ellipsoid. Setting f = 0 gives a sphere.
   *      Negative f gives a prolate ellipsoid. If f > 1, set flattening to 1/f.
   * @return The pointer to the C++ object.
   */
  private static native long newGeodesicCppObject(final double a,
                                                  final double f);

  /**
   * Deletes a GeographicLib::Geodesic object created by newGeodesicCppObject()
   * @param geodesicCppObjectPtr the pointer to the Geodesic C++ objec to delete
   */
  private static native void deleteGeodesicCppObject(final long geodesicCppObjectPtr);

  /**
   * Creates a Geodesic with the parameters for the WGS84 ellipsoid.
   * @return A Geodesic object with the WGS84 parameters.
   */
  private static native Geodesic newGeodesicWgs84Object();

  /**
   * Inherited from java.io.Closeable. <br>
   * Used to destroy the underlying C++ object.
   */
  public final void close() {
    if (geodesicCppPtr != 0) {
      // delete the underlying C++ Geodesic object
      deleteGeodesicCppObject(geodesicCppPtr);
      geodesicCppPtr = 0;
    }
  }

  /**
   * Inherited from java.lang.Object. <br>
   * Used to destroy the underlying C++ object.
   * @exception Throwable if an error occurs
   */
  protected final void finalize() throws Throwable {
    try {
      // delete the underlying C++ Geodesic object
      close();
    } finally {
      super.finalize();
    }
  }

  ///////////////////////////////////////////////////
  // Functions which mimic GeographicLib::Geodesic //
  ///////////////////////////////////////////////////
  /**
   * Describe <code>direct</code> method here.
   *
   * @param lat1 a <code>double</code> value
   * @param lon1 a <code>double</code> value
   * @param azi1 a <code>double</code> value
   * @param s12 a <code>double</code> value
   * @return a <code>DirectResult</code> value
   */
  public final DirectResult direct(final double lat1,
                                   final double lon1,
                                   final double azi1,
                                   final double s12) {
    return nativeDirect(this, this.geodesicCppPtr, lat1, lon1, azi1, s12);
  }
  private static native DirectResult nativeDirect(final Geodesic geodesic,
                                                  final long geodesicCppPtr,
                                                  final double lat1,
                                                  final double lon1,
                                                  final double azi1,
                                                  final double s12);
 
  /**
   * Describe <code>arcDirect</code> method here.
   *
   * @param lat1 a <code>double</code> value
   * @param lon1 a <code>double</code> value
   * @param azi1 a <code>double</code> value
   * @param a12 a <code>double</code> value
   * @return a <code>DirectResult</code> value
   */
  public final DirectResult arcDirect(final double lat1,
                                      final double lon1,
                                      final double azi1,
                                      final double a12) {
    return nativeArcDirect(this, this.geodesicCppPtr, lat1, lon1, azi1, a12);
  }
  private static native DirectResult nativeArcDirect(final Geodesic geodesic,
                                                     final long geodesicCppPtr,
                                                     final double lat1,
                                                     final double lon1,
                                                     final double azi1,
                                                     final double a12);
 
  /**
   * Describe <code>genDirect</code> method here.
   *
   * @param lat1 a <code>double</code> value
   * @param lon1 a <code>double</code> value
   * @param azi1 a <code>double</code> value
   * @param arcmode a <code>boolean</code> value
   * @param s12_a12 a <code>double</code> value
   * @param outmask a <code>long</code> value
   * @return a <code>DirectResult</code> value
   */
  public final DirectResult genDirect(final double lat1,
                                      final double lon1,
                                      final double azi1,
                                      final boolean arcmode,
                                      final double s12_a12,
                                      final long outmask) {
    return nativeGenDirect(this, this.geodesicCppPtr, lat1, lon1, azi1, arcmode,
                           s12_a12, outmask);
  }
  private static native DirectResult nativeGenDirect(final Geodesic geodesic,
                                                     final long geodesicCppPtr,
                                                     final double lat1,
                                                     final double lon1,
                                                     final double azi1,
                                                     final boolean arcmode,
                                                     final double s12_a12,
                                                     final long outmask);
 
  /**
   * Describe <code>inverse</code> method here.
   *
   * @param lat1 a <code>double</code> value
   * @param lon1 a <code>double</code> value
   * @param lat2 a <code>double</code> value
   * @param lon2 a <code>double</code> value
   * @return an <code>InverseResult</code> value
   */
  public final InverseResult inverse(final double lat1,
                                     final double lon1,
                                     final double lat2,
                                     final double lon2) {
    return nativeInverse(this, this.geodesicCppPtr, lat1, lon1, lat2, lon2);
  }
  private static native InverseResult nativeInverse(final Geodesic geodesic,
                                                    final long geodesicCppPtr,
                                                    final double lat1,
                                                    final double lon1,
                                                    final double lat2,
                                                    final double lon2);
  
  /**
   * Describe <code>genInverse</code> method here.
   *
   * @param lat1 a <code>double</code> value
   * @param lon1 a <code>double</code> value
   * @param lat2 a <code>double</code> value
   * @param lon2 a <code>double</code> value
   * @param outmask a <code>long</code> value
   * @return an <code>InverseResult</code> value
   */
  public final InverseResult genInverse(final double lat1,
                                        final double lon1,
                                        final double lat2,
                                        final double lon2,
                                        final long outmask) {
    return nativeGenInverse(this, this.geodesicCppPtr,
                            lat1, lon1, lat2, lon2, outmask);
  }
  private static native InverseResult nativeGenInverse(final Geodesic geodesic,
                                                       final long geodesicCppPtr,
                                                       final double lat1,
                                                       final double lon1,
                                                       final double lat2,
                                                       final double lon2,
                                                       final long outmask);
 
  /**
   * Describe <code>getMajorRadius</code> method here.
   *
   * @return a <code>double</code> value
   */
  public final double getMajorRadius() {
    return nativeMajorRadius(this.geodesicCppPtr);
  }
  private static native double nativeMajorRadius(final long geodesicCppPtr);
 
  /**
   * Describe <code>getFlattening</code> method here.
   *
   * @return a <code>double</code> value
   */
  public final double getFlattening() {
    return nativeFlattening(this.geodesicCppPtr);
  }
  private static native double nativeFlattening(final long geodesicCppPtr);
 
  /**
   * Describe <code>getEllipsoidArea</code> method here.
   *
   * @return a <code>double</code> value
   */
  public final double getEllipsoidArea() {
    return nativeEllipsoidArea(this.geodesicCppPtr);
  }
  private static native double nativeEllipsoidArea(final long geodesicCppPtr);

  // @todo Interface to GeodesicLine.
  // GeodesicLine 	Line (double lat1, double lon1, double azi1, unsigned caps=ALL) const throw ()

  /**
   * Get a string representation of this object.
   * 
   * @return a string representation of this object.
   * 
   * @see java.lang.Object#toString
   */
  public final String toString() {
    return new StringBuffer("MajorRadius=" + getMajorRadius())
        .append(", Flattening=" + getFlattening())
        .append(", EllipsoidArea=" + getEllipsoidArea())
        .toString();
  }

  /**
   * Calculate the hash code for this object.
   * 
   * <p>The rules laid out in J. Blosh's Effective Java are used
   * for the hash code calculation.</p>
   * 
   * @return the hash code.
   * 
   * @see java.lang.Object#hashCode
   */
  public final int hashCode() {
    int code = 11;

    double major_radius = getMajorRadius();
    double flattening = getFlattening();
    double ellipsoid_area = getEllipsoidArea();
    
    code = code * 37 + (int) (Double.doubleToLongBits(major_radius) ^ (Double.doubleToLongBits(major_radius) >> 32));
    code = code * 37 + (int) (Double.doubleToLongBits(flattening) ^ (Double.doubleToLongBits(flattening) >> 32));
    code = code * 37 + (int) (Double.doubleToLongBits(ellipsoid_area) ^ (Double.doubleToLongBits(ellipsoid_area) >> 32));
    
    return code;
  }

  /**
   * Check if this object is equal (equivalent) to another object.
   */
  public final boolean equals(final Object obj) {
    if (obj == this) {
      return true;
    }
    
    if ((obj == null) ||
        !getClass().equals(obj.getClass())) {
      return false;
    }
    
    Geodesic o = (Geodesic) obj;
    
    return (getMajorRadius() == o.getMajorRadius()) &&
        (getFlattening() == o.getFlattening());
  }


  /**
   * <code>GeodesicResult</code>, the result from calling one of the direct()
   * functions.
   *
   * @author <a href="mailto:nmaludy@gmail.com">Nick Maludy</a>
   * @version 1.0
   */
  public final class DirectResult {
    private final long outmask;
    private final double lat2;
    private final double lon2;
    private final double azi2;
    private final double m12;
    private final double M12;
    private final double M21;
    private final double S12;
    private final double a12;
    private final double s12;

    // don't allow default construction
    private DirectResult() {
      this.outmask = 0L;
      this.lat2 = 0;
      this.lon2 = 0;
      this.azi2 = 0;
      this.m12 = 0;
      this.M12 = 0;
      this.M21 = 0;
      this.S12 = 0;
      this.a12 = 0;
      this.s12 = 0;
    }
    
    /**
     * Creates a new <code>DirectResult</code> instance.
     *
     * @param outmask a <code>long</code> value
     * @param lat2 a <code>double</code> value
     * @param lon2 a <code>double</code> value
     * @param azi2 a <code>double</code> value
     * @param m12 a <code>double</code> value
     * @param M12 a <code>double</code> value
     * @param M21 a <code>double</code> value
     * @param S12 a <code>double</code> value
     * @param a12 a <code>double</code> value
     * @param s12 a <code>double</code> value
     */
    public DirectResult(final long outmask,
                        final double lat2,
                        final double lon2,
                        final double azi2,
                        final double m12,
                        final double M12,
                        final double M21,
                        final double S12,
                        final double a12,
                        final double s12) {
      this.outmask = outmask;
      this.lat2 = lat2;
      this.lon2 = lon2;
      this.azi2 = azi2;
      this.m12 = m12;
      this.M12 = M12;
      this.M21 = M21;
      this.S12 = S12;
      this.a12 = a12;
      this.s12 = s12;
    }

    /**
     * Get the <code>Latitude</code> value.
     * @return a <code>double</code> value
     */
    public final double getLat2() {
      return lat2;
    }
    /**
     * Describe <code>hasLat2</code> method here.
     *
     * @return a <code>boolean</code> value
     */
    public final boolean hasLat2() {
      return (outmask & MASK_LATITUDE) == 1;
    }
  
    /**
     * Get the <code>Longitude</code> value.
     * @return a <code>double</code> value
     */
    public final double getLon2() {
      return lon2;
    }
    /**
     * Describe <code>hasLon</code> method here.
     *
     * @return a <code>boolean</code> value
     */
    public final boolean hasLon() {
      return (outmask & MASK_LONGITUDE) == 1;
    }
  
    /**
     * Get the <code>Azimuth</code> value.
     * @return a <code>double</code> value
     */
    public final double getAzi2() {
      return azi2;
    }
    /**
     * Describe <code>hasAzi2</code> method here.
     *
     * @return a <code>boolean</code> value
     */
    public final boolean hasAzi2() {
      return (outmask & MASK_AZIMUTH) == 1;
    }
  
    /**
     * Get the <code>m12</code> value.
     * @return a <code>double</code> value
     */
    public final double getm12() {
      return m12;
    }
    /**
     * Describe <code>hasm12</code> method here.
     *
     * @return a <code>boolean</code> value
     */
    public final boolean hasm12() {
      return (outmask & MASK_REDUCEDLENGTH) == 1;
    }

    /**
     * Get the <code>M12</code> value.
     * @return a <code>double</code> value
     */
    public final double getM12() {
      return M12;
    }
    /**
     * Describe <code>hasM12</code> method here.
     *
     * @return a <code>boolean</code> value
     */
    public final boolean hasM12() {
      return (outmask & MASK_GEODESICSCALE) == 1;
    }
  
    /**
     * Get the <code>M21</code> value.
     * @return a <code>double</code> value
     */
    public final double getM21() {
      return M21;
    }
    /**
     * Describe <code>hasM21</code> method here.
     *
     * @return a <code>boolean</code> value
     */
    public final boolean hasM21() {
      return (outmask & MASK_GEODESICSCALE) == 1;
    }

    /**
     * Get the <code>S12</code> value.
     * @return a <code>double</code> value
     */
    public final double getS12() {
      return S12;
    }
    /**
     * Describe <code>hasS12</code> method here.
     *
     * @return a <code>boolean</code> value
     */
    public final boolean hasS12() {
      return (outmask & MASK_AREA) == 1;
    }

    /**
     * Get the <code>a12</code> value.
     * For Direct() this is set as the return value.
     * For ArcDirect() this is set to the argument given to the function.
     * @return a <code>double</code> value
     */
    public final double getA12() {
      return a12;
    }
    /**
     * Describe <code>hasA21</code> method here.
     *
     * @return a <code>boolean</code> value
     */
    public final boolean hasA21() {
      return true; // always calculated
    }
    
    /**
     * Get the <code>s12</code> value.
     * For ArcDirect() this is set as the return value.
     * For Direct() this is set to the argument given to the function.
     * @return a <code>double</code> value
     */
    public final double gets12() {
      return s12;
    }
    /**
     * Describe <code>hass12</code> method here.
     *
     * @return a <code>boolean</code> value
     */
    public final boolean hass12() {
      return (outmask & MASK_DISTANCE) == 1;
    }

    /**
     * Get a string representation of this object.
     * 
     * @return a string representation of this object.
     * 
     * @see java.lang.Object#toString
     */
    public String toString() {
      return new StringBuffer("outmask=" + outmask)
          .append(", lat2=" + lat2)
          .append(", lon2=" + lon2)
          .append(", azi2=" + azi2)
          .append(", m12=" + m12)
          .append(", M12=" + M12)
          .append(", M21=" + M21)
          .append(", S12=" + S12)
          .append(", a12=" + a12)
          .append(", s12=" + s12)
          .toString();
    }

    /**
     * Calculate the hash code for this object.
     * 
     * <p>The rules laid out in J. Blosh's Effective Java are used
     * for the hash code calculation.</p>
     * 
     * @return the hash code.
     * 
     * @see java.lang.Object#hashCode
     */
    public int hashCode() {
      int code = 13;
      
      code = code * 37 + (int) (outmask ^ (outmask >> 32));
      code = code * 37 + (int) (Double.doubleToLongBits(lat2) ^ (Double.doubleToLongBits(lat2) >> 32));
      code = code * 37 + (int) (Double.doubleToLongBits(lon2) ^ (Double.doubleToLongBits(lon2) >> 32));
      code = code * 37 + (int) (Double.doubleToLongBits(azi2) ^ (Double.doubleToLongBits(azi2) >> 32));
      code = code * 37 + (int) (Double.doubleToLongBits(m12) ^ (Double.doubleToLongBits(m12) >> 32));
      code = code * 37 + (int) (Double.doubleToLongBits(M12) ^ (Double.doubleToLongBits(M12) >> 32));
      code = code * 37 + (int) (Double.doubleToLongBits(M21) ^ (Double.doubleToLongBits(M21) >> 32));
      code = code * 37 + (int) (Double.doubleToLongBits(S12) ^ (Double.doubleToLongBits(S12) >> 32));
      code = code * 37 + (int) (Double.doubleToLongBits(a12) ^ (Double.doubleToLongBits(a12) >> 32));
      code = code * 37 + (int) (Double.doubleToLongBits(s12) ^ (Double.doubleToLongBits(s12) >> 32));
      
      return code;
    }

    /**
     * Check if this object is equal (equivalent) to another object.
     */
    public boolean equals(final Object obj) {
      if (obj == this) {
        return true;
      }
      
      if ((obj == null) ||
          !getClass().equals(obj.getClass())) {
        return false;
      }
      
      DirectResult o = (DirectResult) obj;
      
      return (outmask == o.outmask) &&
          (lat2 == o.lat2) &&
          (lon2 == o.lon2) &&
          (azi2 == o.azi2) &&
          (m12 == o.m12) &&
          (M12 == o.M12) &&
          (M21 == o.M21) &&
          (S12 == o.S12) &&
          (a12 == o.a12) &&
          (s12 == o.s12);
    }
    
  } // end DirectResult

  /**
   * <code>InverseResult</code>, the result from calling one of the inverse()
   * functions.
   *
   * @author <a href="mailto:nmaludy@gmail.com">Nick Maludy</a>
   * @version 1.0
   */
  public final class InverseResult {
    private final long outmask;
    private final double s12;
    private final double azi1;
    private final double azi2;
    private final double m12;
    private final double M12;
    private final double M21;
    private final double S12;
    private final double a12;

    // don't allow default construction
    private InverseResult() {
      this.outmask = 0L;
      this.s12 = 0;
      this.azi1 = 0;
      this.azi2 = 0;
      this.m12 = 0;
      this.M12 = 0;
      this.M21 = 0;
      this.S12 = 0;
      this.a12 = 0;
    }

    /**
     * Creates a new <code>InverseResult</code> instance.
     *
     * @param outmask a <code>long</code> value
     * @param s12 a <code>double</code> value
     * @param azi1 a <code>double</code> value
     * @param azi2 a <code>double</code> value
     * @param m12 a <code>double</code> value
     * @param M12 a <code>double</code> value
     * @param M21 a <code>double</code> value
     * @param S12 a <code>double</code> value
     * @param a12 a <code>double</code> value
     */
    public InverseResult(final long outmask,
                         final double s12,
                         final double azi1,
                         final double azi2,
                         final double m12,
                         final double M12,
                         final double M21,
                         final double S12,
                         final double a12) {
      this.outmask = outmask;
      this.s12 = s12;
      this.azi1 = azi1;
      this.azi2 = azi2;
      this.m12 = m12;
      this.M12 = M12;
      this.M21 = M21;
      this.S12 = S12;
      this.a12 = a12;
    }

    /**
     * Get the <code>s12</code> value. Only set for arcDirect(), not for direct().
     * @return a <code>double</code> value
     */
    public final double gets12() {
      return s12;
    }
    /**
     * Describe <code>hass12</code> method here.
     *
     * @return a <code>boolean</code> value
     */
    public final boolean hass12() {
      return (outmask & MASK_DISTANCE) == 1;
    }

    /**
     * Get the <code>Azimuth</code> value.
     * @return a <code>double</code> value
     */
    public final double getAzi1() {
      return azi1;
    }
    /**
     * Describe <code>hasAzi1</code> method here.
     *
     * @return a <code>boolean</code> value
     */
    public final boolean hasAzi1() {
      return (outmask & MASK_AZIMUTH) == 1;
    }
     
    /**
     * Get the <code>Azimuth</code> value.
     * @return a <code>double</code> value
     */
    public final double getAzi2() {
      return azi2;
    }
    /**
     * Describe <code>hasAzi2</code> method here.
     *
     * @return a <code>boolean</code> value
     */
    public final boolean hasAzi2() {
      return (outmask & MASK_AZIMUTH) == 1;
    }
  
    /**
     * Get the <code>m12</code> value.
     * @return a <code>double</code> value
     */
    public final double getm12() {
      return m12;
    }
    /**
     * Describe <code>hasm12</code> method here.
     *
     * @return a <code>boolean</code> value
     */
    public final boolean hasm12() {
      return (outmask & MASK_REDUCEDLENGTH) == 1;
    }

    /**
     * Get the <code>M12</code> value.
     * @return a <code>double</code> value
     */
    public final double getM12() {
      return M12;
    }
    /**
     * Describe <code>hasM12</code> method here.
     *
     * @return a <code>boolean</code> value
     */
    public final boolean hasM12() {
      return (outmask & MASK_GEODESICSCALE) == 1;
    }
  
    /**
     * Get the <code>M21</code> value.
     * @return a <code>double</code> value
     */
    public final double getM21() {
      return M21;
    }
    /**
     * Describe <code>hasM21</code> method here.
     *
     * @return a <code>boolean</code> value
     */
    public final boolean hasM21() {
      return (outmask & MASK_GEODESICSCALE) == 1;
    }

    /**
     * Get the <code>S12</code> value.
     * @return a <code>double</code> value
     */
    public final double getS12() {
      return S12;
    }
    /**
     * Describe <code>hasS12</code> method here.
     *
     * @return a <code>boolean</code> value
     */
    public final boolean hasS12() {
      return (outmask & MASK_AREA) == 1;
    }

    /**
     * Get the <code>a12</code> value. Only set for direct(), not for arcDirect().
     * @return a <code>double</code> value
     */
    public final double getA12() {
      return a12;
    }
    /**
     * Describe <code>hasA12</code> method here.
     *
     * @return a <code>boolean</code> value
     */
    public final boolean hasA12() {
      return true; // always calculated
    }

    /**
     * Get a string representation of this object.
     * 
     * @return a string representation of this object.
     * 
     * @see java.lang.Object#toString
     */
    public String toString() {
      return new StringBuffer("outmask=" + outmask)
          .append(", s12=" + s12)
          .append(", azi1=" + azi1)
          .append(", azi2=" + azi2)
          .append(", m12=" + m12)
          .append(", M12=" + M12)
          .append(", M21=" + M21)
          .append(", S12=" + S12)
          .append(", a12=" + a12)
          .toString();
    }

    /**
     * Check if this object is equal (equivalent) to another object.
     */
    public boolean equals(final Object obj) {
      if (obj == this) {
        return true;
      }
      
      if ((obj == null) ||
          !getClass().equals(obj.getClass())) {
        return false;
      }
      
      InverseResult o = (InverseResult) obj;
      
      return (outmask == o.outmask) &&
          (s12 == o.s12) &&
          (azi1 == o.azi1) &&
          (azi2 == o.azi2) &&
          (m12 == o.m12) &&
          (M12 == o.M12) &&
          (M21 == o.M21) &&
          (S12 == o.S12) &&
          (a12 == o.a12);
    }
    
  } // End DirectResult

} // end Geodesic
 
