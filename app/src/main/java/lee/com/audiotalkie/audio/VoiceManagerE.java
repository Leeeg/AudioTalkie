package lee.com.audiotalkie.audio;

import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import lee.com.audiotalkie.Jni;
import lee.com.audiotalkie.callBack.RecordDataCallback;
import lee.com.audiotalkie.model.RecordConfig;
import lee.com.audiotalkie.utils.DataUtil;


public class VoiceManagerE {

    private final String TAG = "Lee_VoiceManager";
    private static VoiceManagerE recordManager;
    private AudioRecord audioRecord;
    private AudioTrack audioTrack;
    private int minRecordBufferSize, minTrackBufferSize;
    private boolean isRecording, isPlaying;

    final short[] recordBuffer = new short[minRecordBufferSize];

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public VoiceManagerE() {
        initManager();
    }

    /**
     * 获取单例引用
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static VoiceManagerE getInstance() {
        if (recordManager == null) {
            synchronized (VoiceManagerE.class) {
                if (recordManager == null) {
                    recordManager = new VoiceManagerE();
                }
            }
        }
        return recordManager;
    }

    /**
     * 初始化AudioRecord & AudioTrack
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void initManager() {
        minRecordBufferSize = AudioRecord.getMinBufferSize(RecordConfig.SAMPLE_RATE_INHZ.getValue(), RecordConfig.CHANNEL_IN_CONFIG.getValue(), RecordConfig.AUDIO_FORMAT.getValue());
//        minRecordBufferSize = 480;
        audioRecord = new AudioRecord(
                RecordConfig.MIC.getValue(),
                RecordConfig.SAMPLE_RATE_INHZ.getValue(),
                RecordConfig.CHANNEL_IN_CONFIG.getValue(),
                RecordConfig.AUDIO_FORMAT.getValue(),
                minRecordBufferSize);

        //    STATE_INITIALIZED:1, 初始化成功，等待被使用；    STATE_UNINITIALIZED:0, 初始化失败。
        int recordState = audioRecord.getState();
        Log.e(TAG, "AudioRecord init state = " + recordState);

        minTrackBufferSize = AudioTrack.getMinBufferSize(RecordConfig.SAMPLE_RATE_INHZ.getValue(), RecordConfig.CHANNEL_OUT_CONFIG.getValue(), RecordConfig.AUDIO_FORMAT.getValue());
//        final int minRecordBufferSize = 480;
        audioTrack = new AudioTrack(
                new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build(),
                new AudioFormat.Builder()
                        .setSampleRate(RecordConfig.SAMPLE_RATE_INHZ.getValue())
                        .setChannelMask(RecordConfig.CHANNEL_OUT_CONFIG.getValue())
                        .setEncoding(RecordConfig.AUDIO_FORMAT.getValue())
                        .build(),
                minTrackBufferSize,
                AudioTrack.MODE_STREAM,
                AudioManager.AUDIO_SESSION_ID_GENERATE);

        // AudioTrack.STATE_UNINITIALIZED：没有初始化成功
        // AudioTrack.STATE_INITIALIZED：初始化成功AudioTrack实例，等待被使用
        // AudioTrack.STATE_NO_STATIC_DATA：初始化成功一个使用静态数据的AudioTrack实例，但是该实例还没有获取到任何的数据。
        int trackState = audioTrack.getState();
        Log.e(TAG, "AudioTrack init state = " + trackState);

        int opusState = Jni.initOpus();
        Log.e(TAG, "OPUS init state = " + opusState);

    }

    /**
     * 录制
     */
    public void startRecord(RecordDataCallback recordDataCallback) {
        Log.d(TAG, "startRecord");
        if (AudioRecord.STATE_INITIALIZED != audioRecord.getState()) {
            Log.e(TAG, "the audioRecord is uninitialized !  state = " + audioRecord.getState());
            return;
        }

        audioRecord.startRecording();
        isRecording = true;

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isRecording) {
                    int bufferReadResult = audioRecord.read(recordBuffer, 0, minRecordBufferSize);
                    // 如果读取音频数据没有出现错误，就将数据写入到文件
                    if (AudioRecord.ERROR_INVALID_OPERATION != bufferReadResult) {

                        Log.i(TAG, "recording ---- bufferReadResult = " + bufferReadResult);
                        Log.i(TAG, "recording ---- length = " + recordBuffer.length + "\n");

                        if (bufferReadResult == minRecordBufferSize) {
                            Log.i(TAG, "recording ---- encode  >>");
                            short[] encoderShort = Jni.opusEncoder(recordBuffer, bufferReadResult);
                            Log.i(TAG, "recording ---- encode  << \n");

//                            recordDataCallback.recordData(encoderShort);

//                            Log.i(TAG, "recording ---- decode  >>");
                            short[] decodeShort = Jni.opusDecode(encoderShort, encoderShort.length, bufferReadResult);
//                            Log.i(TAG, "recording ---- decode  << \n");

                        }

                    }
                }
            }
        }).start();
    }


    /**
     * 停止录制
     */
    public void stopRecord() {
        Log.d(TAG, "stopRecord");
        if (isRecording)
            isRecording = false;
        if (null != audioRecord) {
            audioRecord.stop();
        }
    }


    /**
     * 播放
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void startTrack(byte[] bytes) {
        Log.d(TAG, "startTrack");
        if (AudioTrack.STATE_INITIALIZED != audioTrack.getState()) {
            Log.e(TAG, "the audioTrack is uninitialized !  state = " + audioTrack.getState());
            return;
        }

        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
//                        byte[] tempBuffer = new byte[minRecordBufferSize];
                        while (isPlaying) {
//                            int readCount = dataInputStream.read(tempBuffer);
//                            if (readCount == AudioTrack.ERROR_INVALID_OPERATION || readCount == AudioTrack.ERROR_BAD_VALUE) {
//                                continue;
//                            }
//                            if (readCount != 0 && readCount != -1) {
//                            short[] shortBuffer = DataUtil.toShortArray(bytes);
//                            short[] decodeShort = Jni.opusDecode(shortBuffer, shortBuffer.length, minRecordBufferSize);
//                            audioTrack.write(decodeShort, 0, minRecordBufferSize);
                        }
//                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止播放
     */
    private void stopTrack() {
        Log.d(TAG, "stopTrack");
        if (isPlaying)
            isPlaying = false;
        if (audioTrack != null)
            audioTrack.stop();
    }

    /**
     * 释放资源
     */
    public void releaseManager() {
        Log.d(TAG, "releaseManager");
        isRecording = false;
        isPlaying = false;
        // 释放资源
        if (null != audioRecord) {
            audioRecord.stop();
            audioRecord.release();
            audioRecord = null;
        }
        if (null != audioTrack) {
            audioTrack.stop();
            audioTrack.release();
            audioTrack = null;
        }

        recordManager = null;
    }

}
