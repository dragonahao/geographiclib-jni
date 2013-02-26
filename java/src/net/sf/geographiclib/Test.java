package net.sf.geographiclib;

public class Test {

  /** 
   * Test to ensure that the WGS84 instance is working and loading correctly.
   */
  public static void wgs84test() {
    // The geodesic inverse problem
    Geodesic.InverseResult inv_result = Geodesic.WGS84.inverse(-41.32, 174.81,
                                                               40.96, -5.50);
    System.out.println("Inverse: " + inv_result.toString());
    
    // The geodesic direct problem
    Geodesic.DirectResult dir_result = Geodesic.WGS84.direct(40.6, -73.8,
                                                             45, 10000e3);
    System.out.println("Direct: " + inv_result.toString());
  }

  /** 
   * Test the Geodesic function using the try-with-resources statement
   */
  public static void java7test() {
    Geodesic wgs84 = Geodesic.WGS84;
    try (Geodesic g = new Geodesic(wgs84.getMajorRadius(),
                                   wgs84.getFlattening())) {
      Geodesic.DirectResult result = g.direct(39.03, -79.12, 90, 1500);
      System.out.println("Java 7 Direct test: " + result.toString());
      // g.close() is automatically called so that the C++ pointer is deleted
    }
  }
  
  public static void main(String[] args) {
    wgs84test();
    java7test();
  }
}
