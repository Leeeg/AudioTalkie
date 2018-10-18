#include <jni.h>
#include <string>
#include <opus.h>
#include <malloc.h>

extern "C" {
#include <opus-build.h>
}

OpusDecoder *opusDecoder;
OpusEncoder *opusEncoder;
int channels = 1;
int fs = 48000;

extern "C"
JNIEXPORT jint JNICALL
Java_net_opus_com_opus_OpusJni_initOpus(JNIEnv *env, jobject instance) {

    opusDecoder = initDecoderCreate(fs, channels);
    opusEncoder = initEncoderCreate(fs, channels);
    if (opusDecoder != NULL && opusEncoder != NULL) {
        return 0;
    } else {
        return 1;
    }
}


extern "C"
JNIEXPORT jshortArray JNICALL
Java_net_opus_com_opus_OpusJni_opusEncoder(JNIEnv *env, jclass type, jshortArray buffer_,
                                           jint length) {
    jshort *buffer = env->GetShortArrayElements(buffer_, NULL);
    short outBuffer[length];
    int len = opus_encodes(opusEncoder, buffer, length, outBuffer);
    jshortArray shortArray = env->NewShortArray(len);
    env->SetShortArrayRegion(shortArray, 0, len, outBuffer);
    env->ReleaseShortArrayElements(buffer_, buffer, 0);
    return shortArray;

}extern "C"
JNIEXPORT jshortArray JNICALL
Java_net_opus_com_opus_OpusJni_opusDecode(JNIEnv *env, jclass type, jshortArray buffer_,
                                          jint bufferLength, jint pcmLength) {
    jshort *buffer = env->GetShortArrayElements(buffer_, NULL);
    opus_int16 outBufferPc[pcmLength];
    int lenPc = opus_decodes(opusDecoder, buffer, bufferLength, outBufferPc);
    jshortArray shortArrays = env->NewShortArray(lenPc);
    env->SetShortArrayRegion(shortArrays, 0, lenPc, outBufferPc);
    env->ReleaseShortArrayElements(buffer_, buffer, 0);
    return shortArrays;
}extern "C"
JNIEXPORT jint JNICALL
Java_net_opus_com_opus_OpusJni_close(JNIEnv *env, jclass type) {
    close(opusEncoder, opusDecoder);
    return 0;
}