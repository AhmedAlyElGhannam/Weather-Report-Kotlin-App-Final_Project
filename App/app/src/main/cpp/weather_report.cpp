#include <jni.h>
#include <string>


/******************************TEMP********************************/
extern "C"
JNIEXPORT jdouble JNICALL
Java_com_example_weather_1report_utils_UnitSystemsConversions_00024Companion_celciusToKelvin(
        JNIEnv *env, jobject thiz, jdouble temp) {
    jdouble res = 0.0;

    res = (temp * (9.0/5.0)) + 32.0;

    return res;
}
extern "C"
JNIEXPORT jdouble JNICALL
Java_com_example_weather_1report_utils_UnitSystemsConversions_00024Companion_celciusToFahrenheit(
        JNIEnv *env, jobject thiz, jdouble temp) {
    jdouble res = 0.0;

    res = temp + 273.15;

    return res;
}
/******************************SPEED********************************/
extern "C"
JNIEXPORT jdouble JNICALL
Java_com_example_weather_1report_utils_UnitSystemsConversions_00024Companion_meterPerSecondToKilometerPerHour(
        JNIEnv *env, jobject thiz, jdouble wind_speed) {
    jdouble res = 0.0;

    res = wind_speed * 3.6;

    return res;
}
extern "C"
JNIEXPORT jdouble JNICALL
Java_com_example_weather_1report_utils_UnitSystemsConversions_00024Companion_meterPerSecondToMilePerHour(
        JNIEnv *env, jobject thiz, jdouble wind_speed) {
    jdouble res = 0.0;

    res = wind_speed * 2.23694;

    return res;
}
extern "C"
JNIEXPORT jdouble JNICALL
Java_com_example_weather_1report_utils_UnitSystemsConversions_00024Companion_meterPerSecondToFeetPerSecond(
        JNIEnv *env, jobject thiz, jdouble wind_speed) {
    jdouble res = 0.0;

    res = wind_speed * 3.28084;

    return res;
}
/******************************PRESSURE********************************/
extern "C"
JNIEXPORT jdouble JNICALL
Java_com_example_weather_1report_utils_UnitSystemsConversions_00024Companion_hectopascalToPsi(
        JNIEnv *env, jobject thiz, jdouble pressure) {
    jdouble res = 0.0;

    res = pressure * 0.0145038;

    return res;
}
extern "C"
JNIEXPORT jdouble JNICALL
Java_com_example_weather_1report_utils_UnitSystemsConversions_00024Companion_hectopascalToAtm(
        JNIEnv *env, jobject thiz, jdouble pressure) {
    jdouble res = 0.0;

    res = pressure / 1013.25;

    return res;
}
extern "C"
JNIEXPORT jdouble JNICALL
Java_com_example_weather_1report_utils_UnitSystemsConversions_00024Companion_hectopascalToBar(
        JNIEnv *env, jobject thiz, jdouble pressure) {
    jdouble res = 0.0;

    res = pressure / 1000;

    return res;
}
