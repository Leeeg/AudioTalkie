//
// Created by xiaofeng on 2018/4/13.
//

#ifndef MYAPPLICATION_OPUS_BUILD_H
#include <opus.h>
#define MYAPPLICATION_OPUS_BUILD_H



OpusDecoder *initDecoderCreate(int fs,int channels);

OpusEncoder *initEncoderCreate(int fs,int channels);

int opus_encodes(OpusEncoder *opusEncoder, opus_int16 buffer[], int size,  short *out);

int opus_decodes(OpusDecoder *opusDecoder,  short buffer[], int bufferSize, opus_int16 *pcmBuffer);

int close(OpusEncoder *opusEncoder,OpusDecoder *opusDecoder);
#endif //MYAPPLICATION_OPUS_BUILD_H
