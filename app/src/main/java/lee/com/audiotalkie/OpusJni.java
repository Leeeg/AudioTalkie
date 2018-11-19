package lee.com.audiotalkie;

public class OpusJni {
    static {
        System.loadLibrary("native-lib");
    }

    public static native int initOpus();

    public static native short[] opusEncoder(short[] buffer, int length);

    public static native byte[] opusEncoderByte(byte[] buffer, int length);

    public static native short[] opusDecode(short[] buffer, int bufferLength,int pcmLength);

    public static native byte[] opusDecodeByte(byte[] buffer, int bufferLength,int pcmLength);

    public static native int close();

}
