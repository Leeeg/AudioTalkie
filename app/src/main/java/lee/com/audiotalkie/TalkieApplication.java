package lee.com.audiotalkie;

import android.app.Application;
import android.os.Build;
import android.support.annotation.RequiresApi;

import lee.com.audiotalkie.audio.VoiceManager;
import lee.com.audiotalkie.utils.LogUtil;


public class TalkieApplication extends Application {

    private static TalkieApplication application;

    public static TalkieApplication getInstance() {
        return application;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate() {
        super.onCreate();
        application = this;

//        LogUtil.init(application.getApplicationContext());

    }

}
