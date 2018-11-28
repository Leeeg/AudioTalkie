package lee.com.audiotalkie.model;

import java.util.Arrays;

import lee.com.audiotalkie.utils.ByteUtil;
import lee.com.audiotalkie.utils.DataUtil;

/**
 * CreateDate：18-11-22 on 下午2:04
 * Describe:
 * Coder: lee
 */
public class RTPPackage {

    int type;//0: 语音   1： 心跳

    int needRsp;

    int timestamp = (int) (System.currentTimeMillis() / 1000);

    short seq;

    int ssrc;

    short len;

    long userId;

    long targetId;

    byte[] opusData;

    public int getType() {
        return type;
    }

    public RTPPackage setType(int type) {
        this.type = type;
        return this;
    }

    public int isNeedRsp() {
        return needRsp;
    }

    public RTPPackage setNeedRsp(int needRsp) {
        this.needRsp = needRsp;
        return this;
    }

    public short getSeq() {
        return seq;
    }

    public RTPPackage setSeq(short seq) {
        this.seq = seq;
        return this;
    }

    public int getSsrc() {
        return ssrc;
    }

    public RTPPackage setSsrc(int ssrc) {
        this.ssrc = ssrc;
        return this;
    }

    public short getLen() {
        return len;
    }

    public RTPPackage setLen(int len) {
        this.len = (short) len;
        return this;
    }

    public long getUserId() {
        return userId;
    }

    public RTPPackage setUserId(long userId) {
        this.userId = userId;
        return this;
    }

    public long getTargetId() {
        return targetId;
    }

    public RTPPackage setTargetId(long targetId) {
        this.targetId = targetId;
        return this;
    }

    public byte[] getOpusData() {
        return opusData;
    }

    public short[] getOpusDataByShort() {
        if (null == opusData) return null;
        return ByteUtil.toShortArray(opusData);
    }

    public RTPPackage setOpusData(byte[] opusData) {
        this.opusData = opusData;
        return this;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public RTPPackage setTimestamp(int timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public RTPPackage() {
    }

    public boolean parse(byte[] bytes) {
        this.seq = DataUtil.getSequenceNumber(bytes);
        this.ssrc = DataUtil.getSsrc(bytes);
        this.len = DataUtil.getLength(bytes);
        this.userId = DataUtil.getUserId(bytes);
        this.targetId = DataUtil.getTargetId(bytes);
        this.opusData = new byte[len];
        return DataUtil.getOpusData(this.opusData, bytes, len);
    }

    @Override
    public String toString() {
        return "RTPPackage{" +
                "type=" + type +
                ", needRsp=" + needRsp +
                ", timestamp=" + timestamp +
                ", seq=" + seq +
                ", ssrc=" + ssrc +
                ", len=" + len +
                ", userId=" + userId +
                ", targetId=" + targetId +
                ", opusData=" + Arrays.toString(opusData) +
                '}';
    }
}
