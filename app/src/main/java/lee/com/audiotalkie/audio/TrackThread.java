package lee.com.audiotalkie.audio;

import android.annotation.SuppressLint;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.locks.LockSupport;

import lee.com.audiotalkie.OpusJni;
import lee.com.audiotalkie.model.AudioThread;
import lee.com.audiotalkie.model.RecordConfig;

/**
 * CreateDate：18-11-7 on 下午3:01
 * Describe:
 * Coder: lee
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
@SuppressLint("LongLogTag")
public class TrackThread extends Thread implements AudioThread {

    private final String TAG = "Lee_VoiceManager_TrackThread";

    private AudioTrack audioTrack;
    private int minTrackBufferSize;
    private boolean isPlaying = true;
    private BlockingDeque<short[]> blockingDeque;
    private boolean isPark = true;

    public boolean addShorts(short[] shorts) {
        return blockingDeque.offer(shorts);
    }

    public TrackThread() {
        init();
    }

    @Override
    public void init() {
        Log.d(TAG, "init");

        minTrackBufferSize = 480;

        final int trackBufferSize = AudioTrack.getMinBufferSize(RecordConfig.SAMPLE_RATE_INHZ, RecordConfig.CHANNEL_OUT_CONFIG,
                RecordConfig
                        .AUDIO_FORMAT);
        Log.e(TAG, "AudioTrack minTrackBufferSize = " + minTrackBufferSize);

        audioTrack = new AudioTrack(
                new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build(),
                new AudioFormat.Builder()
                        .setSampleRate(RecordConfig.SAMPLE_RATE_INHZ)
                        .setChannelMask(RecordConfig.CHANNEL_OUT_CONFIG)
                        .setEncoding(RecordConfig.AUDIO_FORMAT)
                        .build(),
                trackBufferSize,
                AudioTrack.MODE_STREAM,
                AudioManager.AUDIO_SESSION_ID_GENERATE);

        // AudioTrack.STATE_UNINITIALIZED：没有初始化成功
        // AudioTrack.STATE_INITIALIZED：初始化成功AudioTrack实例，等待被使用
        // AudioTrack.STATE_NO_STATIC_DATA：初始化成功一个使用静态数据的AudioTrack实例，但是该实例还没有获取到任何的数据。
        int trackState = audioTrack.getState();
        Log.e(TAG, "AudioTrack init state = " + trackState);

        blockingDeque = new LinkedBlockingDeque<>();
    }

    @Override
    public void begin() {
        Log.d(TAG, "startTrack");
        if (AudioTrack.STATE_INITIALIZED != audioTrack.getState()) {
            Log.e(TAG, "the audioTrack is uninitialized !  state = " + audioTrack.getState());
            return;
        }

        if (null != audioTrack)
            audioTrack.play();

        if (isPark) {
            isPark = false;
            LockSupport.unpark(this);
        }

    }

    @Override
    public void over() {
        Log.d(TAG, "stopTrack");
        if (null != audioTrack)
            audioTrack.stop();

        if (!isPark)
            isPark = true;
    }

    @Override
    public void release() {
        Log.d(TAG, "release");
        if (null != audioTrack) {
            isPlaying = false;
            audioTrack.release();
            audioTrack = null;
            destroy();
        }
    }

    @Override
    public void run() {
        Log.d(TAG, "run");
        short[] shorts_pkg;
        Log.d(TAG, "TrackThread running -------------- ");
        while (isPlaying) {
            try {
                Log.d(TAG, "Playing -------------- ");
                short[] shortBuffer = blockingDeque.takeFirst();
                if (isPark) {
                    LockSupport.park();
                }
                if (null != shortBuffer) {
                    Log.i(TAG, "recording ---- decode  >>");
                    Log.d(TAG, "shortBuffer ： " + shortBuffer.length);
                    shorts_pkg = OpusJni.opusDecode(shortBuffer, shortBuffer.length, minTrackBufferSize);
                    Log.d(TAG, "shorts_pkg ： " + shorts_pkg.length);
                    Log.i(TAG, "recording ---- decode  << \n");
                    audioTrack.write(shorts_pkg, 0, shorts_pkg.length);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                Log.d(TAG, "Playing ERROR : " + e);
            }
        }
    }

}
