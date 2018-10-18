package lee.com.audiotalkie.presenter;

import android.os.Handler;
import android.os.Message;

import lee.com.audiotalkie.TalkieApplication;
import lee.com.audiotalkie.model.MediaPlayCallback;
import lee.com.audiotalkie.utils.MediaManager;
import lee.com.audiotalkie.view.TalkieView;

/**
 * CreateDate：18-10-18 on 下午3:45
 * Describe:
 * Coder: lee
 */
public class TalkiePresenterImpl implements TalkiePresenter, MediaPlayCallback {

    private TalkieView mView;

    private MediaManager recordManager;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:

                    break;
                case 1:

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
}
