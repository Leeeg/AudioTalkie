package lee.com.audiotalkie.presenter;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.io.IOException;

import lee.com.audiotalkie.model.OutCallInformation;
import lee.com.audiotalkie.model.NetConfig;
import lee.com.audiotalkie.net.TcpClient;
import lee.com.audiotalkie.audio.TalkieManager;
import lee.com.audiotalkie.view.TalkieView;

/**
 * CreateDate：18-10-18 on 下午3:45
 * Describe:
 * Coder: lee
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class TalkiePresenterImpl implements TalkiePresenter {

    private final String TAG = "Lee_log_TalkiePresenterImpl";
    private TalkieView mView;

    private TalkieManager talkieManager;

    private TcpClient tcpClient = null;

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
                case 4:
                    mView.setCatchCount((long) msg.obj);
                    break;
            }
        }
    };

    @SuppressLint("LongLogTag")
    public TalkiePresenterImpl(TalkieView mView) {

        this.mView = mView;
        mView.setPresenter(this);

        talkieManager = TalkieManager.getInstance();
        talkieManager.init(NetConfig.HOST.getValue(), Integer.parseInt(NetConfig.PORT.getValue()));

        OutCallInformation.setCallId(123);
        OutCallInformation.setUserId(1234);
        OutCallInformation.setTargetId(2345);
    }

    @Override
    public void recordStart() {
        Message message = handler.obtainMessage();
        message.what = 0;
        message.obj = "start record";
        handler.sendMessage(message);
//        talkieManager.startRecord();

        talkieManager.sendHeartBeat(3219, 0);
    }

    @Override
    public void recordStop() {
        Message message = handler.obtainMessage();
        message.what = 0;
        message.obj = "stop record";
        handler.sendMessage(message);
        talkieManager.stopRecord();

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
    }

    @Override
    public void trackStop() {
        mView.setSpeak(false);
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
    }
}
