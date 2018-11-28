#include <jni.h>
#include <string>
#include <opus.h>
#include <malloc.h>
#include <rtp.h>
#include <heart.h>

extern "C" {
#include <opus-build.h>
}

OpusDecoder *opusDecoder;
OpusEncoder *opusEncoder;
int channels = 1;
int fs = 48000;

extern "C"
JNIEXPORT jint JNICALL
Java_lee_com_audiotalkie_Jni_initOpus(JNIEnv *env, jobject instance) {

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
Java_lee_com_audiotalkie_Jni_opusEncoder(JNIEnv *env, jclass type, jshortArray buffer_, jint length) {
    jshort *buffer = env->GetShortArrayElements(buffer_, NULL);
    short outBuffer[length];
    int len = opus_encodes(opusEncoder, buffer, length, outBuffer);
    jshortArray shortArray = env->NewShortArray(len);
    env->SetShortArrayRegion(shortArray, 0, len, outBuffer);
    env->ReleaseShortArrayElements(buffer_, buffer, 0);
    return shortArray;
}

extern "C"
JNIEXPORT jshortArray JNICALL
Java_lee_com_audiotalkie_Jni_opusDecode(JNIEnv *env, jclass type, jshortArray buffer_, jint bufferLength, jint pcmLength) {
    jshort *buffer = env->GetShortArrayElements(buffer_, NULL);
    opus_int16 outBufferPc[pcmLength];
    int lenPc = opus_decodes(opusDecoder, buffer, bufferLength, outBufferPc);
    if(lenPc < 0){
        return NULL;
    }
    jshortArray shortArrays = env->NewShortArray(lenPc);
    env->SetShortArrayRegion(shortArrays, 0, lenPc, outBufferPc);
    env->ReleaseShortArrayElements(buffer_, buffer, 0);
    return shortArrays;
}

extern "C"
JNIEXPORT jint JNICALL
Java_lee_com_audiotalkie_Jni_close(JNIEnv *env, jclass type) {
    close(opusEncoder, opusDecoder);
    return 0;
}

//extern "C"
//JNIEXPORT jbyteArray JNICALL
//Java_lee_com_audiotalkie_Jni_opusEncoderByte(JNIEnv *env, jclass type, jbyteArray buffer_, jint length) {
//    jbyte *buffer = env->GetByteArrayElements(buffer_, NULL);
//    short outBuffer[length];
//    int len = opus_encodes(opusEncoder, (short *) buffer, length, outBuffer);
//    jbyteArray byteArray = env->NewByteArray(len);
//    env->SetByteArrayRegion(byteArray, 0, len, (jbyte *) outBuffer);
//    env->ReleaseByteArrayElements(buffer_, buffer, 0);
//    return byteArray;
//
//}

//extern "C"
//JNIEXPORT jbyteArray JNICALL
//Java_lee_com_audiotalkie_Jni_opusDecodeByte(JNIEnv *env, jclass type, jbyteArray buffer_, jint bufferLength, jint pcmLength) {
//    jbyte *buffer = env->GetByteArrayElements(buffer_, NULL);
//    opus_int16 outBufferPc[pcmLength];
//    int lenPc = opus_decodes(opusDecoder, (short *) buffer, bufferLength, outBufferPc);
//    jbyteArray byteArray = env->NewByteArray(lenPc);
//    env->SetByteArrayRegion(byteArray, 0, lenPc, (jbyte *) outBufferPc);
//    env->ReleaseByteArrayElements(buffer_, buffer, 0);
//    return byteArray;
//}

extern "C"
JNIEXPORT jbyteArray JNICALL
Java_lee_com_audiotalkie_Jni_getHeaderBytes(JNIEnv *env, jclass type, jint timestamp, jshort seq, jint _ssrc, jshort len, jlong _userId, jlong _targetId) {

    char *chars = NULL;
    int char_len = 36;
    chars = new char[char_len + 1];

    capsulationHearder(timestamp, seq, _ssrc, len, _userId, _targetId, chars);

    jbyteArray jbyteArray = env->NewByteArray(char_len);

    env->SetByteArrayRegion(jbyteArray, 0, char_len, reinterpret_cast<const jbyte *>(chars));

    return jbyteArray;

}

extern "C"
JNIEXPORT jbyteArray JNICALL
Java_lee_com_audiotalkie_Jni_getHeartBytes(JNIEnv *env, jclass type, jint needRsp, jlong _userId) {

    char *chars = NULL;
    int char_len = 24;
    chars = new char[char_len + 1];

    capsulationHeart(needRsp, _userId, chars);

    jbyteArray jbyteArray = env->NewByteArray(char_len);

    env->SetByteArrayRegion(jbyteArray, 0, char_len, reinterpret_cast<const jbyte *>(chars));

    return jbyteArray;

}