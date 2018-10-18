//
// Created by xiaofeng on 2018/4/13.
//


#include "opus-build.h"


OpusDecoder *initDecoderCreate(int fs, int channels) {
    int error;
    OpusDecoder *decoder;
    decoder = opus_decoder_create(fs, channels, &error);
    if (error < 0) {
        return NULL;
    } else {
        return decoder;
    }
}

OpusEncoder *initEncoderCreate(int fs, int channels) {
    int error;
    OpusEncoder *opusEncoder;
    opusEncoder = opus_encoder_create(fs, channels, OPUS_APPLICATION_AUDIO, &error);
    if (error < 0) {
        return NULL;
    } else {
        opus_encoder_ctl(opusEncoder, OPUS_SET_COMPLEXITY(5));
        return opusEncoder;
    }
}

int opus_encodes(OpusEncoder *opusEncoder, opus_int16 buffer[], int size, short *out) {
    int length;
    if (opusEncoder != NULL) {
        length = opus_encode(opusEncoder, buffer, size, out, 480);
    } else {
        length = 0;
    }
    return length;
}

int opus_decodes(OpusDecoder *opusDecoder, short buffer[], int bufferSize, opus_int16 *pcmBuffer) {
    int length;
    if (opusDecoder != NULL) {
        length = opus_decode(opusDecoder, buffer, bufferSize, pcmBuffer, 480 * 4, 0);
    } else {
        length = 0;
    }
    return length;
}

int close(OpusEncoder *opusEncoder, OpusDecoder *opusDecoder) {
    if (opusEncoder != NULL) {
        opus_encoder_destroy(opusEncoder);
    }
    if (opusDecoder != NULL) {
        opus_decoder_destroy(opusDecoder);
    }
}

