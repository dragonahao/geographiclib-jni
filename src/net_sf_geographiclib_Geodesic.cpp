#include <GeographicLib/jni/net_sf_geographiclib_Geodesic.h>

#include <GeographicLib/Geodesic.hpp>

using GeographicLib::Geodesic;
using GeographicLib::Math;

struct JniCache
{
  JniCache()
      : mClass(NULL),
        mConstructorMid(NULL)
  {}
  
  jclass mClass;
  jmethodID mConstructorMid;
};

static JniCache GEODESIC_CACHE;
static JniCache DIRECT_RESULT_CACHE;
static JniCache INVERSE_RESULT_CACHE;

JNIEXPORT void JNICALL
Java_net_sf_geographiclib_Geodesic_staticInit(JNIEnv* pEnv,
                                              jclass geodesicClass)
{
  jmethodID mid;
  jclass result_class;
  
  // Get its constructor (the one that takes 2 doubles (a, f))
  mid = pEnv->GetMethodID(geodesicClass, "<init>", "(DD)V");
  if (pEnv->ExceptionCheck() || mid == NULL)
  {
    printf("Can't get MethodID for Geodesic constructor\n");
    return;
  }
  GEODESIC_CACHE.mClass = (jclass)pEnv->NewGlobalRef(geodesicClass);
  GEODESIC_CACHE.mConstructorMid = mid;
  
  // Get the handle of the net.sf.geographiclib.Geodesic.DirectResult class
  result_class = pEnv->FindClass("net/sf/geographiclib/Geodesic$DirectResult");
  if (pEnv->ExceptionCheck() || result_class == NULL)
  {
    printf("Can't FindClass(net/sf/geographiclib/Geodesic$DirectResult\n");
    return;
  }
  DIRECT_RESULT_CACHE.mClass = (jclass)pEnv->NewGlobalRef(result_class);
  
  // Get its constructor (the one that takes the parent class, one long, and 9 doubles)
  mid = pEnv->GetMethodID(DIRECT_RESULT_CACHE.mClass, "<init>",
                          "(Lnet/sf/geographiclib/Geodesic;JDDDDDDDDD)V");
  if (pEnv->ExceptionCheck() || mid == NULL)
  {
    printf("Can't get MethodID for DirectResult constructor\n");
    return;
  }
  DIRECT_RESULT_CACHE.mConstructorMid = mid;
  
  // Get the handle of the net.sf.geographiclib.Geodesic.DirectResult class
  result_class = pEnv->FindClass("net/sf/geographiclib/Geodesic$InverseResult");
  if (pEnv->ExceptionCheck() || result_class == NULL)
  {
    printf("Can't FindClass(net/sf.geographiclib/Geodesic$InverseResult\n");
    return;
  }
  INVERSE_RESULT_CACHE.mClass = (jclass)pEnv->NewGlobalRef(result_class);
  
  // Get its constructor (the one that takes the parent object, one long, and 8 doubles)
  mid = pEnv->GetMethodID(result_class, "<init>",
                          "(Lnet/sf/geographiclib/Geodesic;JDDDDDDDD)V");
  if (pEnv->ExceptionCheck() || mid == NULL)
  {
    printf("Can't get MethodID for InverseResult constructor\n");
    return;
  }
  INVERSE_RESULT_CACHE.mConstructorMid = mid;
}

/*
 * Class:     net_sf_geographiclib_Geodesic
 * Method:    newGeodesicCppObject
 * Signature: (DD)J
 */
JNIEXPORT jlong JNICALL
Java_net_sf_geographiclib_Geodesic_newGeodesicCppObject(JNIEnv* pEnv,
                                                        jclass geodesicClass,
                                                        jdouble a,
                                                        jdouble f)
{
  Geodesic* geodesic = new Geodesic(a, f);
  // cast the pointer to a jlong type
  return reinterpret_cast<jlong>(geodesic);
}

/*
 * Class:     net_sf_geographiclib_Geodesic
 * Method:    deleteGeodesicCppObject
 * Signature: (J)V
 */
JNIEXPORT void JNICALL
Java_net_sf_geographiclib_Geodesic_deleteGeodesicCppObject(JNIEnv* pEnv,
                                                           jclass geodesicClass,
                                                           jlong geodesicCppPtr)
{
  if (geodesicCppPtr != 0)
  {
    Geodesic* geodesic = reinterpret_cast<Geodesic*>(geodesicCppPtr);
    delete geodesic;
  }
}

/*
 * Class:     net_sf_geographiclib_Geodesic
 * Method:    newGeodesicWgs84Object
 * Signature: ()Lnet/sf/geographiclib/Geodesic;
 */
JNIEXPORT jobject JNICALL
Java_net_sf_geographiclib_Geodesic_newGeodesicWgs84Object(JNIEnv* pEnv,
                                                          jclass geodesicClass)
{ 
  jdouble a = Geodesic::WGS84.MajorRadius();
  jdouble f = Geodesic::WGS84.Flattening();

  // Allocate the Geodesic
  return pEnv->NewObject(GEODESIC_CACHE.mClass,
                         GEODESIC_CACHE.mConstructorMid,
                         a, f);
}

/*
 * Class:     net_sf_geographiclib_Geodesic
 * Method:    nativeDirect
 * Signature: (Lnet/sf/geographiclib/Geodesic;JDDDD)Lnet/sf/geographiclib/Geodesic/DirectResult;
 */
JNIEXPORT jobject JNICALL
Java_net_sf_geographiclib_Geodesic_nativeDirect(JNIEnv* pEnv,
                                                jclass geodesicClass,
                                                jobject geodesicObject,
                                                jlong geodesicCppPtr,
                                                jdouble lat1,
                                                jdouble lon1,
                                                jdouble azi1,
                                                jdouble s12)
{
  return Java_net_sf_geographiclib_Geodesic_nativeGenDirect(pEnv,
                                                            geodesicClass,
                                                            geodesicObject,
                                                            geodesicCppPtr,
                                                            lat1,
                                                            lon1,
                                                            azi1,
                                                            false,
                                                            s12,
                                                            Geodesic::ALL); 
}


/*
 * Class:     net_sf_geographiclib_Geodesic
 * Method:    nativeArcDirect
 * Signature: (Lnet/sf/geographiclib/Geodesic;JDDDD)Lnet/sf/geographiclib/Geodesic/DirectResult;
 */
JNIEXPORT jobject JNICALL
Java_net_sf_geographiclib_Geodesic_nativeArcDirect(JNIEnv* pEnv,
                                                   jclass geodesicClass,
                                                   jobject geodesicObject,
                                                   jlong geodesicCppPtr,
                                                   jdouble lat1,
                                                   jdouble lon1,
                                                   jdouble azi1,
                                                   jdouble a12)
{
  return Java_net_sf_geographiclib_Geodesic_nativeGenDirect(pEnv,
                                                            geodesicClass,
                                                            geodesicObject,
                                                            geodesicCppPtr,
                                                            lat1,
                                                            lon1,
                                                            azi1,
                                                            true,
                                                            a12,
                                                            Geodesic::ALL);
}

/*
 * Class:     net_sf_geographiclib_Geodesic
 * Method:    nativeGenDirect
 * Signature: (Lnet/sf/geographiclib/Geodesic;JDDDZDJ)Lnet/sf/geographiclib/Geodesic/DirectResult;
 */
JNIEXPORT jobject JNICALL
Java_net_sf_geographiclib_Geodesic_nativeGenDirect(JNIEnv* pEnv,
                                                   jclass geodesicClass,
                                                   jobject geodesicObject,
                                                   jlong geodesicCppPtr,
                                                   jdouble lat1,
                                                   jdouble lon1,
                                                   jdouble azi1,
                                                   jboolean arcmode,
                                                   jdouble s12_a12,
                                                   jlong outmask)
{
  Geodesic* geodesic = reinterpret_cast<Geodesic*>(geodesicCppPtr);

  Math::real lat2 = 0;
  Math::real lon2 = 0;
  Math::real azi2 = 0;
  Math::real m12 = 0;
  Math::real M12 = 0;
  Math::real M21 = 0;
  Math::real S12 = 0;
  Math::real s12 = 0;
  Math::real a12 = geodesic->GenDirect(lat1, lon1, azi1, arcmode, s12_a12, outmask,
                                       lat2, lon2, azi2, s12, m12, M12, M21, S12);
  
  // Allocate the DirectResult
  return pEnv->NewObject(DIRECT_RESULT_CACHE.mClass,
                         DIRECT_RESULT_CACHE.mConstructorMid,
                         geodesicObject,
                         outmask,
                         lat2,
                         lon2,
                         azi2,
                         m12,
                         M12,
                         M21,
                         S12,
                         a12,
                         s12);
}

/*
 * Class:     net_sf_geographiclib_Geodesic
 * Method:    nativeInverse
 * Signature: (Lnet/sf/geographiclib/Geodesic;JDDDD)Lnet/sf/geographiclib/Geodesic/InverseResult;
 */
JNIEXPORT jobject JNICALL
Java_net_sf_geographiclib_Geodesic_nativeInverse(JNIEnv* pEnv,
                                                 jclass geodesicClass,
                                                 jobject geodesicObject,
                                                 jlong geodesicCppPtr,
                                                 jdouble lat1,
                                                 jdouble lon1,
                                                 jdouble lat2,
                                                 jdouble lon2)
{
  return Java_net_sf_geographiclib_Geodesic_nativeGenInverse(pEnv,
                                                             geodesicClass,
                                                             geodesicObject,
                                                             geodesicCppPtr,
                                                             lat1,
                                                             lon1,
                                                             lat2,
                                                             lon2,
                                                             Geodesic::ALL);
}

/*
 * Class:     net_sf_geographiclib_Geodesic
 * Method:    nativeGenInverse
 * Signature: (Lnet/sf/geographiclib/Geodesic;JDDDDJ)Lnet/sf/geographiclib/Geodesic/InverseResult;
 */
JNIEXPORT jobject JNICALL
Java_net_sf_geographiclib_Geodesic_nativeGenInverse(JNIEnv* pEnv,
                                                    jclass geodesicClass,
                                                    jobject geodesicObject,
                                                    jlong geodesicCppPtr,
                                                    jdouble lat1,
                                                    jdouble lon1,
                                                    jdouble lat2,
                                                    jdouble lon2,
                                                    jlong outmask)
{  
  Geodesic* geodesic = reinterpret_cast<Geodesic*>(geodesicCppPtr);

  Math::real s12 = 0;
  Math::real azi1 = 0;
  Math::real azi2 = 0;
  Math::real m12 = 0;
  Math::real M12 = 0;
  Math::real M21 = 0;
  Math::real S12 = 0;
  Math::real a12 = geodesic->GenInverse(lat1, lon1, lat2, lon2, outmask,
                                        s12, azi1, azi2, m12, M12, M21, S12);

  // Allocate the InverseResult
  return pEnv->NewObject(INVERSE_RESULT_CACHE.mClass,
                         INVERSE_RESULT_CACHE.mConstructorMid,
                         geodesicObject,
                         outmask,
                         s12,
                         azi1,
                         azi2,
                         m12,
                         M12,
                         M21,
                         S12,
                         a12);
}

JNIEXPORT jdouble JNICALL
Java_net_sf_geographiclib_Geodesic_nativeMajorRadius(JNIEnv* pEnv,
                                                     jclass geodesicClass,
                                                     jlong geodesicCppPtr)
{
  Geodesic* geodesic = reinterpret_cast<Geodesic*>(geodesicCppPtr);
  return geodesic->MajorRadius();
}

JNIEXPORT jdouble JNICALL
Java_net_sf_geographiclib_Geodesic_nativeFlattening(JNIEnv* pEnv,
                                                    jclass geodesicClass,
                                                    jlong geodesicCppPtr)
{
  Geodesic* geodesic = reinterpret_cast<Geodesic*>(geodesicCppPtr);
  return geodesic->Flattening();
}

JNIEXPORT jdouble JNICALL
Java_net_sf_geographiclib_Geodesic_nativeEllipsoidArea(JNIEnv* pEnv,
                                                       jclass geodesicClass,
                                                       jlong geodesicCppPtr)
{
  Geodesic* geodesic = reinterpret_cast<Geodesic*>(geodesicCppPtr);
  return geodesic->EllipsoidArea();
}
