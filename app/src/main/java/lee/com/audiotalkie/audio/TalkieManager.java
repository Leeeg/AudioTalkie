package lee.com.audiotalkie.audio;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import lee.com.audiotalkie.Jni;
import lee.com.audiotalkie.callBack.TrackCallback;
import lee.com.audiotalkie.callBack.RecordDataCallback;
import lee.com.audiotalkie.callBack.SocketCallback;
import lee.com.audiotalkie.channel.TalkieChannel;
import lee.com.audiotalkie.model.OutCallInformation;
import lee.com.audiotalkie.model.RTPPackage;
import lee.com.audiotalkie.net.UdpClient;
import lee.com.audiotalkie.utils.ByteUtil;
import lee.com.audiotalkie.utils.DataUtil;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class TalkieManager implements RecordDataCallback, SocketCallback, TrackCallback {

    private final String TAG = "Lee_TalkieManager";
    private static TalkieManager talkieManager;
    private RecordThread recordThread;
    private TrackThread trackThread;
    private boolean isSpeaking, isListening;
    private UdpClient udpClient = null;


    public TalkieManager() {

    }

    /**
     * 获取单例引用
     */
    public static TalkieManager getInstance() {
        if (talkieManager == null) {
            synchronized (TalkieManager.class) {
                if (talkieManager == null) {
                    talkieManager = new TalkieManager();
                }
            }
        }
        return talkieManager;
    }

    /**
     * 初始化AudioRecord & AudioTrack
     */
    public int init(String ip, int port) {

        int opusState = Jni.initOpus();
        Log.e(TAG, "OPUS init state = " + (opusState == 0 ? "success" : "failed"));

        recordThread = new RecordThread();
        recordThread.start();

        trackThread = new TrackThread();
        trackThread.start();

        udpClient = new UdpClient(ip, port, this::socketReceive);
        udpClient.initSocket();

        addRecordCallback(this::recordData);
        addTrackCallback(this);

        return 1;

    }

    public void startRecord() {
        if (!OutCallInformation.isIntact()) return;
        if (!isListening && !isSpeaking && createOutChannel()) {
            Log.d(TAG, TalkieChannel.getInstance().getString());
            isSpeaking = true;
            recordThread.begin();
        }
    }

    public void stopRecord() {
        isSpeaking = false;
        recordThread.over();
        releaseChannel();
    }

    private void startTrack(byte[] data) {
        Log.d(TAG, "startTrack");
        if (!isSpeaking) {
            if (!isListening) {
                if (createInChannel(data)) {
                    Log.d(TAG, TalkieChannel.getInstance().getString());
                    isListening = true;
                    trackThread.begin();
                } else {
                    Log.e(TAG, "ERROR : createInChannel failed !");
                }
            } else {
                int callId = DataUtil.getSsrc(data);
                if (TalkieChannel.getInstance().isWorking() && callId == TalkieChannel.getInstance().getCallId()) {
                    boolean b = addIncoming(data);
                    if (!b){
                        Log.e(TAG, "addIncoming : Failed !");
                    }
                }
            }
        }
    }

    private void stopTrack() {
        isListening = false;
        trackThread.over();
        releaseChannel();
    }

    private boolean addIncoming(byte[] data) {
        RTPPackage rtpPackage = new RTPPackage();
        if (rtpPackage.parse(data)) {
            Log.d(TAG, "-----addIncoming------->>>>>" + rtpPackage.toString());
            return trackThread.addPacket(rtpPackage);
        }
        return false;
    }

    private boolean createOutChannel() {
        if (TalkieChannel.getInstance().isWorking()) {
            System.out.println("Busying");
            return false;
        }
        Log.d(TAG, OutCallInformation.getString());

        return TalkieChannel.getInstance().setCallId(OutCallInformation.getCallId())
                .reSetSeq()
                .setUserId(OutCallInformation.getUserId())
                .setTargetId(OutCallInformation.getTargetId())
                .work();
    }

    private boolean createInChannel(byte[] data) {
        if (TalkieChannel.getInstance().isWorking()) {
            System.out.println("Busying");
            return false;
        }
        RTPPackage rtpPackage = new RTPPackage();
        if (rtpPackage.parse(data)) {
            Log.d(TAG, "-----createInChannel------->>>>>" + rtpPackage.toString());
            trackThread.addPacket(rtpPackage);
            return TalkieChannel.getInstance().setCallId(rtpPackage.getSsrc())
                    .reSetSeq()
                    .setUserId(rtpPackage.getUserId())
                    .setTargetId(rtpPackage.getTargetId())
                    .work();
        }
        return false;
    }

    private void releaseChannel() {
        TalkieChannel.getInstance().clear();
    }

    private void addRecordCallback(RecordDataCallback recordDataCallback) {
        recordThread.addRecordDataCallback(recordDataCallback);
    }

    private void addTrackCallback(TrackCallback trackCallback) {
        trackThread.addMyTrackCallback(trackCallback);
    }

    public void release() {
        recordThread.release();
        trackThread.release();
        udpClient = null;
    }

    @Override
    public void catchCount(long count) {

    }

    @Override
    public void playTimeOut() {
        stopTrack();
    }

    @Override
    public void recordData(short[] data) {
        byte[] opusData = ByteUtil.toByteArray(data);
        RTPPackage rtpPackage = new RTPPackage();
        rtpPackage.setSeq((short) opusData.length)
                .setSsrc(TalkieChannel.getInstance().getCallId())
                .setUserId(TalkieChannel.getInstance().getUserId())
                .setTargetId(TalkieChannel.getInstance().getTargetId())
                .setLen(opusData.length)
                .setOpusData(opusData);
        Log.d(TAG, "------------addRTPPacket" + rtpPackage.toString());
        udpClient.addRTPPacket(rtpPackage);
    }

    @Override
    public void socketReceive(byte[] data) {
        startTrack(data);
    }

}