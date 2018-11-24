//
// Created by lee on 18-11-23.
//

#ifndef AUDIOTALKIE_BASE_LOG_H
#define AUDIOTALKIE_BASE_LOG_H


#include <android/log.h>

#define LOG_TAG "System.out.cpp"


#define LOGV(...) __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, __VA_ARGS__)
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)



class base_log {

};


#endif //AUDIOTALKIE_BASE_LOG_H
