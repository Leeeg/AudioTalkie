package lee.com.audiotalkie.presenter;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import lee.com.audiotalkie.callBack.MyTrackCallback;
import lee.com.audiotalkie.callBack.RecordDataCallback;
import lee.com.audiotalkie.callBack.SocketCallback;
import lee.com.audiotalkie.model.NetConfig;
import lee.com.audiotalkie.net.TcpClient;
import lee.com.audiotalkie.net.UdpClient;
import lee.com.audiotalkie.utils.DataUtil;
import lee.com.audiotalkie.audio.VoiceManager;
import lee.com.audiotalkie.utils.HttpUtil;
import lee.com.audiotalkie.utils.LogUtil;
import lee.com.audiotalkie.view.TalkieView;

/**
 * CreateDate：18-10-18 on 下午3:45
 * Describe:
 * Coder: lee
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class TalkiePresenterImpl implements TalkiePresenter, RecordDataCallback, SocketCallback, MyTrackCallback {

    private final String TAG = "Lee_log_TalkiePresenterImpl";
    private TalkieView mView;

    private VoiceManager voiceManager;

    private TcpClient tcpClient = null;
    private UdpClient udpClient = null;
    private boolean isSpeaking, isListening;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    mView.logAdd(msg.obj.toString());
                    break;
                case 1:
                    mView.logAdd("recording : recordData.length = " + ((short[]) msg.obj).length);
                    break;
                case 2:
                    mView.logAdd(msg.obj.toString());
                    mView.socketReset();
                    break;
                case 3:
                    mView.logAdd("tracking : trackData.length = " + ((byte[]) msg.obj).length);
                    break;
            }
        }
    };

    @SuppressLint("LongLogTag")
    public TalkiePresenterImpl(TalkieView mView) {

        this.mView = mView;
        mView.setPresenter(this);

        voiceManager = VoiceManager.getInstance();
        voiceManager.init();
        voiceManager.addRecordCallback(this);

    }

    @Override
    public void recordStart() {
        Message message = handler.obtainMessage();
        message.what = 0;
        message.obj = "start record";
        handler.sendMessage(message);
        isSpeaking = true;
        voiceManager.startRecord();
    }

    @Override
    public void recordStop() {
        Message message = handler.obtainMessage();
        message.what = 0;
        message.obj = "stop record";
        handler.sendMessage(message);
        isSpeaking = false;
        voiceManager.stopRecord();

//        new Thread() {
//            @Override
//            public void run() {
//                super.run();
//                try {
//                    String url = "http://47.106.108.175:8089/cpsFirmware/uploadLogFile";
//                    File file = new File(LogUtil.getFileName());
//                    HttpUtil.uploadForm(null, "file", file, "upload.log", url);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }.start();

    }

    @Override
    public void trackStart() {
        mView.setSpeak(true);
        voiceManager.startTrack();
    }

    @Override
    public void trackStop() {
        mView.setSpeak(false);
        voiceManager.stopTrack();
    }

    @Override
    public void opusEncode() {

    }

    @Override
    public void opusDecode() {

    }

    @SuppressLint("LongLogTag")
    @Override
    public void socketInit(String ip, int port) {
//        if (null == tcpClient)
//            tcpClient = new TcpClient(ip, 8888, this);
//        tcpClient.initSocket();

        if (null == udpClient)
            udpClient = new UdpClient(ip, port, this);
        udpClient.initSocket();
    }

    @Override
    public void TcpClose() {
        if (null != tcpClient)
            tcpClient.closeSocket();
    }

    @SuppressLint("LongLogTag")
    @Override
    public void TcpTest() {
        if (null != tcpClient && null != tcpClient.getWriter())
            try {
                Log.e(TAG, "socketWrite success !");
                tcpClient.getWriter().write("TcpTest  -----  ".getBytes());
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "socketWrite error !");
            }
    }

    @Override
    public void UdpTest() {
        Message message = handler.obtainMessage();
        message.what = 0;
        message.obj = "send Udp test !";
        handler.sendMessage(message);
        if (null != udpClient)
            udpClient.getBlockingDeque().offer("Udp testUdp testUdp testUdp testUdp testUdp testUdp testUdp testUdp testUdp testUdp testUdp testUdp testUdp testUdp testUdp testUdp testUdp testUdp testUdp testUdp testUdp testUdp testUdp test".getBytes());
    }

    @SuppressLint("LongLogTag")
    @Override
    public void recordData(short[] data) {
        Message message = handler.obtainMessage();
        message.what = 1;
        message.obj = data;
        handler.sendMessage(message);

        Log.e(TAG, "recordData data : " + data.length);
        LogUtil.i("recordData : ", "" + data.length);
        byte[] b = DataUtil.toByteArray(data);
        Log.e(TAG, "recordData byte : " + b.length);

//        short[] s = DataUtil.toShortArray(b);
//        Log.e(TAG, "recordData short : " + s.length);

//        socketReceive(b);

//        if (null != tcpClient.getWriter()) {
//            try {
//                Log.e(TAG, "socketWrite success !");
//                tcpClient.getWriter().write(b);
//            } catch (IOException e) {
//                e.printStackTrace();
//                Log.e(TAG, "socketWrite error !");
//            }
//        }

        if (null != udpClient) {
            Log.d(TAG, "byte[] data : length = " + b.length);
            udpClient.getBlockingDeque().offer(b);
        }
    }

    @SuppressLint("LongLogTag")
    @Override
    public void socketSend(String msg) {
    }

    @SuppressLint("LongLogTag")
    @Override
    public void socketReceive(byte[] bytes) {
        Message message = handler.obtainMessage();
        message.what = 3;
        message.obj = bytes;
        handler.sendMessage(message);

        Log.i(TAG, "socketReceive !   bytes.length = " + bytes.length);
        short[] s = DataUtil.toShortArray(bytes);
        Log.e(TAG, "recordData short : " + s.length);

        if (!isSpeaking) {
            voiceManager.addShorts(s);
        }
    }

    @Override
    public void tcpAppendMsg(String msg) {
        Message message = handler.obtainMessage();
        message.what = 0;
        message.obj = msg;
        handler.sendMessage(message);
    }

    @Override
    public void tcpError(String msg) {
        Message message = handler.obtainMessage();
        message.what = 2;
        message.obj = "ERROR : " + msg + "  ..............  ";
        handler.sendMessage(message);
    }

    @Override
    public void catchCount(long count) {
        mView.setCatchCount(count);
    }

}
