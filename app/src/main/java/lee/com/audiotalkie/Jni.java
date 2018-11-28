package lee.com.audiotalkie;

public class Jni {


    static {
        System.loadLibrary("native-lib");
    }

    public static native int initOpus();

    public static native short[] opusEncoder(short[] buffer, int length);

    public static native byte[] opusEncoderByte(byte[] buffer, int length);

    public static native short[] opusDecode(short[] buffer, int bufferLength, int pcmLength);

    public static native byte[] opusDecodeByte(byte[] buffer, int bufferLength, int pcmLength);

    public static native int close();

    public static native byte[] getHeaderBytes(int timestamp, short seq, int _ssrc, short len, long _userId, long _targetId);

    public static native byte[] getHeartBytes(int needRsp, long _userId);

}
