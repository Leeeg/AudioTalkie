package lee.com.audiotalkie.audio;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import lee.com.audiotalkie.OpusJni;
import lee.com.audiotalkie.callBack.RecordDataCallback;


@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class VoiceManager {

    private final String TAG = "Lee_VoiceManager";
    private static VoiceManager voiceManager;
    private RecordThread recordThread;
    private TrackThread trackThread;

    public VoiceManager() {

    }

    /**
     * 获取单例引用
     */
    public static VoiceManager getInstance() {
        if (voiceManager == null) {
            synchronized (VoiceManager.class) {
                if (voiceManager == null) {
                    voiceManager = new VoiceManager();
                }
            }
        }
        return voiceManager;
    }


    /**
     * 初始化AudioRecord & AudioTrack
     */
    public void init() {

        int opusState = OpusJni.initOpus();
        Log.e(TAG, "OPUS init state = " + (opusState == 0 ? "success" : "failed"));

        recordThread = new RecordThread();
        recordThread.start();

        trackThread = new TrackThread();
        trackThread.start();

    }

    public void startRecord() {
        recordThread.begin();
    }

    public void startTrack() {
        trackThread.begin();
    }

    public void stopRecord() {
        recordThread.over();
    }

    public void stopTrack() {
        trackThread.over();
    }

    public boolean addShorts(short[] shorts) {
        return trackThread.addShorts(shorts);
    }

    public void addRecordCallback(RecordDataCallback recordDataCallback) {
        recordThread.addRecordDataCallback(recordDataCallback);
    }

    public void release() {
        trackThread.release();
    }

}
