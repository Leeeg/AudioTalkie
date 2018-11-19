package lee.com.audiotalkie.audio;

import android.annotation.SuppressLint;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.util.Log;

import java.util.LinkedList;
import java.util.concurrent.locks.LockSupport;

import lee.com.audiotalkie.OpusJni;
import lee.com.audiotalkie.callBack.RecordDataCallback;
import lee.com.audiotalkie.model.AudioThread;
import lee.com.audiotalkie.model.RecordConfig;

/**
 * CreateDate：18-11-7 on 下午3:01
 * Describe:
 * Coder: lee
 */
@SuppressLint("LongLogTag")
public class RecordThread extends Thread implements AudioThread {

    private final String TAG = "Lee_VoiceManager_RecordThread";

    private AudioRecord audioRecord;
    private int minRecordBufferSize;
    private boolean isRecording = true;
    private boolean isPark = true;
    private short[] recordBuffer;
    private LinkedList<short[]> linkedList;
    private RecordDataCallback recordDataCallback;

    public void removeRecordDataCallback() {
        recordDataCallback = null;
    }

    public void addRecordDataCallback(RecordDataCallback recordDataCallback) {
        this.recordDataCallback = recordDataCallback;
    }

    public RecordThread() {
        init();
    }

    @Override
    public void init() {
        minRecordBufferSize = 480;
        final int recordBufferSize = AudioRecord.getMinBufferSize(RecordConfig.SAMPLE_RATE_INHZ, RecordConfig.CHANNEL_IN_CONFIG,
                RecordConfig
                .AUDIO_FORMAT);
        Log.e(TAG, "AudioRecord minRecordBufferSize = " + minRecordBufferSize);

        audioRecord = new AudioRecord(
                MediaRecorder.AudioSource.VOICE_COMMUNICATION,
                RecordConfig.SAMPLE_RATE_INHZ,
                RecordConfig.CHANNEL_IN_CONFIG,
                RecordConfig.AUDIO_FORMAT,
                recordBufferSize);

        //    STATE_INITIALIZED:1, 初始化成功，等待被使用；    STATE_UNINITIALIZED:0, 初始化失败。
        int recordState = audioRecord.getState();
        Log.e(TAG, "AudioRecord init state = " + recordState);

        recordBuffer = new short[minRecordBufferSize];
        linkedList = new LinkedList<>();

    }

    @Override
    public void begin() {
        Log.d(TAG, "startRecord");
        if (AudioRecord.STATE_INITIALIZED != audioRecord.getState()) {
            Log.e(TAG, "the audioRecord is uninitialized !  state = " + audioRecord.getState());
            return;
        }

        if (null != audioRecord)
            audioRecord.startRecording();
        if (isPark){
            isPark = false;
            LockSupport.unpark(this);
        }
    }

    @Override
    public void over() {
        Log.d(TAG, "stopRecord");
        if (null != audioRecord)
            audioRecord.stop();

        if (!isPark)
            isPark = true;
    }

    @Override
    public void release() {
        Log.d(TAG, "release");
        if (null != audioRecord) {
            isRecording = false;
            audioRecord.release();
            audioRecord = null;
            destroy();
        }
    }

    @Override
    public void run() {
        Log.d(TAG, "run");
        Log.d(TAG, "RecordThread running ------------");
        while (isRecording) {
            Log.d(TAG, "Recording ------------");
            if (isPark){
                LockSupport.park();
            }
            int bufferReadResult = audioRecord.read(recordBuffer, 0, minRecordBufferSize);
            short[] shorts_pkg;
            // 如果读取音频数据没有出现错误
            if (AudioRecord.ERROR_INVALID_OPERATION != bufferReadResult) {
                Log.i(TAG, "recording ---- bufferReadResult = " + bufferReadResult);
                Log.i(TAG, "recording ---- length = " + recordBuffer.length + "\n");
                if (bufferReadResult == minRecordBufferSize) {
                    Log.i(TAG, "recording ---- encode  >>");
                    short[] encoderShort = OpusJni.opusEncoder(recordBuffer, bufferReadResult);
                    Log.i(TAG, "recording ---- encode  << \n");
                    shorts_pkg = encoderShort.clone();
                    if (linkedList.size() >= 2) {
                        Log.d(TAG, "linkedList.size() = " + linkedList.size());
                        Log.d(TAG, "shorts_pkg.length = " + shorts_pkg.length);
                        if (null != recordDataCallback)
                            recordDataCallback.recordData(linkedList.removeFirst());
                    }
                    linkedList.add(shorts_pkg);
                }
            }
        }
    }

}
