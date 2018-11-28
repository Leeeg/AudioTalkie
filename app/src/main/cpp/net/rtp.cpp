//
// Created by lee on 18-11-22.
//

#include <base_log.h>
#include <sys/time.h>
#include "rtp.h"

void capsulationHearder(int _timestamp, short seq, int _ssrc, short len, __int64_t _userId, __int64_t _targetId, char *data) {

    RTPFixedHeader *rtpFixedHeader = (RTPFixedHeader *) data;

    rtpFixedHeader->version = 2;
    rtpFixedHeader->padding = 0;
    rtpFixedHeader->csrc_count = 1;
    rtpFixedHeader->extension = 1;

    rtpFixedHeader->marker = 1;
    rtpFixedHeader->payload_type = 1;

    rtpFixedHeader->timestamp = _timestamp;

    rtpFixedHeader->sequence_number = seq;

    rtpFixedHeader->ssrc = _ssrc;

    rtpFixedHeader->csrc = _ssrc;

    rtpFixedHeader->extid = 1;

    rtpFixedHeader->payload_len = len;


    RTPExtensionHeader *rtpExtensionHeader = (RTPExtensionHeader *) (data + sizeof(RTPFixedHeader));

    rtpExtensionHeader->userId = _userId;
    rtpExtensionHeader->targetId = _targetId;

    LOGD("%d", sizeof(RTPFixedHeader));

}

void parse(char recvData) {

}