//
// Created by lee on 18-11-22.
//

#ifndef AUDIOTALKIE_RTP_H
#define AUDIOTALKIE_RTP_H

#include <structs.h>

typedef struct RTPFixedHearder {
    /* byte 0 */
    unsigned char version:2;        /* 版本 expect 2*/
    unsigned char padding:1;        /* 包尾填充 expect 0*/
    unsigned char csrc_count:4;       /* csrc数 expect 1*/
    unsigned char extension:1;      /* 扩展头 expect 1*/
    /* byte 1 */
    unsigned char marker:1;        /* 标记 expect 1 */
    unsigned char payload_type:7;       /* 对标记的解释 rtp载荷类型 expect 1 */
    /* bytes 2, 3 */
    unsigned short sequence_number;         /* 序列号 初始值随机*/
    /* bytes 4-7 */
    unsigned int timestamp;       /* 时间戳*/
    /* bytes 8-11 */
    unsigned int ssrc;            /* 同步源 eg：mixStreamId */
    /* bytes 12-15 */
    unsigned int csrc;            /* 贡献源列表 eg：streamId0  streamId1  streamId02 */

    unsigned short extid;        /* 是否有扩展数据*/

    unsigned short payload_len;       /* 扩展数据长度*/

} RTPFixedHeader;

struct RTPExtensionHeader {
    __int64_t userId;
    __int64_t targetId;
};

void capsulationHearder(int _timestamp, short seq, int _ssrc, short len, __int64_t _userId, __int64_t _targetId, char *data);

void parse();

class rtp {

};


#endif //AUDIOTALKIE_RTP_H
