//
// Created by lee on 18-11-26.
//

#include "heart.h"

void capsulationHeart(int needRsp, uint64_t userId, char *data) {

    RTPHBReq *rtphbReq = (RTPHBReq *) data;

    rtphbReq->magic = 0x1c;

    if (0 == needRsp) {
        rtphbReq->needRsp = 0x0;
    } else if (1 == needRsp) {
        rtphbReq->needRsp = 0x1;
    }
    rtphbReq->ver = 2;

    rtphbReq->uid = userId;

    rtphbReq->endTag = 0x1c;

}