package lee.com.audiotalkie.presenter;

import android.os.Handler;
import android.os.Message;

import lee.com.audiotalkie.TalkieApplication;
import lee.com.audiotalkie.model.MediaPlayCallback;
import lee.com.audiotalkie.model.RecordDataCallback;
import lee.com.audiotalkie.utils.MediaManager;
import lee.com.audiotalkie.view.TalkieView;

/**
 * CreateDate：18-10-18 on 下午3:45
 * Describe:
 * Coder: lee
 */
public class TalkiePresenterImpl implements TalkiePresenter, MediaPlayCallback, RecordDataCallback {

    private TalkieView mView;

    private MediaManager recordManager;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    mView.logAdd(msg.obj.toString());
                    break;
                case 1:
                    mView.logAdd("recording : data.length = " + ((byte[])msg.obj).length);
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

    @Override
    public void data(byte[] data) {
        Message message = handler.obtainMessage();
        message.what = 1;
        message.obj = data;
        handler.sendMessage(message);
    }
}
