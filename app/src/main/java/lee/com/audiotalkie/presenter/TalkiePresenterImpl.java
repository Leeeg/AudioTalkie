package lee.com.audiotalkie.presenter;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;

import lee.com.audiotalkie.TalkieApplication;
import lee.com.audiotalkie.model.MediaPlayCallback;
import lee.com.audiotalkie.model.RecordDataCallback;
import lee.com.audiotalkie.model.SocketCallback;
import lee.com.audiotalkie.net.TcpClient;
import lee.com.audiotalkie.net.UdpClient;
import lee.com.audiotalkie.utils.DataUtil;
import lee.com.audiotalkie.utils.MediaManager;
import lee.com.audiotalkie.view.TalkieView;

/**
 * CreateDate：18-10-18 on 下午3:45
 * Describe:
 * Coder: lee
 */
public class TalkiePresenterImpl implements TalkiePresenter, MediaPlayCallback, RecordDataCallback, SocketCallback {

    private final String TAG = "Lee_log_TalkiePresenterImpl";
    private TalkieView mView;

    private MediaManager recordManager;

    private TcpClient tcpClient = null;
    private UdpClient udpClient = null;

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
            }
        }
    };

    public TalkiePresenterImpl(TalkieView mView) {

        this.mView = mView;
        mView.setPresenter(this);

        recordManager = TalkieApplication.getInstance().getRecordManager();
        recordManager.setMediaPlayerCallback(this);

    }

    @Override
    public void onPlayOver() {

    }

    @Override
    public void recordStart() {
        Message message = handler.obtainMessage();
        message.what = 0;
        message.obj = "start record";
        handler.sendMessage(message);
        recordManager.startRecord(this);
    }

    @Override
    public void recordStop() {
        Message message = handler.obtainMessage();
        message.what = 0;
        message.obj = "stop record";
        handler.sendMessage(message);
        recordManager.stopRecord();
    }

    @Override
    public void trackStart() {

    }

    @Override
    public void trackStop() {

    }

    @Override
    public void opusEncode() {

    }

    @Override
    public void opusDecode() {

    }

    @SuppressLint("LongLogTag")
    @Override
    public void socketInit() {
        String ip = DataUtil.getLocalIpAddress();
        Log.d(TAG, "local ip = " + ip);
        if (null == tcpClient)
            tcpClient = new TcpClient("192.168.0.16", 8888, this);
        tcpClient.initSocket();

        if (null == udpClient)
            udpClient = new UdpClient("192.168.0.16", 8889);
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
        if (null != tcpClient.getWriter())
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
        udpClient.getBlockingDeque().offer("Udp testUdp testUdp testUdp testUdp testUdp testUdp testUdp testUdp testUdp testUdp testUdp testUdp testUdp testUdp testUdp testUdp testUdp testUdp testUdp testUdp testUdp testUdp testUdp test".getBytes());
    }

    @SuppressLint("LongLogTag")
    @Override
    public void recordData(short[] data) {
        Message message = handler.obtainMessage();
        message.what = 1;
        message.obj = data;
        handler.sendMessage(message);
//        if (null != tcpClient.getWriter())
//            try {
//                Log.e(TAG, "socketWrite success !");
//                tcpClient.getWriter().write(DataUtil.toByteArray(data));
//            } catch (IOException e) {
//                e.printStackTrace();
//                Log.e(TAG, "socketWrite error !");
//            }
        if (null != udpClient) {
            Log.d(TAG, "short[] data : length = " + data.length);
            byte[] mData = DataUtil.toByteArray(data);
            Log.d(TAG, "byte[] data : length = " + mData.length);
            udpClient.getBlockingDeque().offer(mData);
        }
    }

    @SuppressLint("LongLogTag")
    @Override
    public void socketSend(String msg) {

    }

    @Override
    public void socketReceive(String msg) {

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
}
