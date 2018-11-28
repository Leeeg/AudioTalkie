//
// Created by lee on 18-11-26.
//

#ifndef AUDIOTALKIE_HEART_H
#define AUDIOTALKIE_HEART_H


#include <structs.h>

typedef struct RTPHBReq {
    uint8_t magic:8;         // 心跳标志， 0x1C
    uint8_t needRsp:4;       // 0x0 表示不需要回复，0x1表示需要服务器回传外网ip：port
    uint8_t ver:4;           // 版本号
    uint16_t reserb:16;      // 保留
    uint64_t uid;            // 用户id
    uint32_t exp;            // 有效时长
    uint32_t reserb2:24;
    uint32_t endTag:8;        // 结束标志，0x1C
} RTPHBReq;

void capsulationHeart(int needRsp, uint64_t userId, char *data);

class heart {

};


#endif //AUDIOTALKIE_HEART_H
