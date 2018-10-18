package lee.com.audiotalkie;

import android.app.Application;
import android.os.Environment;

import lee.com.audiotalkie.utils.MediaManager;


public class TalkieApplication extends Application {

    private static TalkieApplication application;

    public static TalkieApplication getInstance(){
        return application;
    }

    private MediaManager recordManager;

    public MediaManager getRecordManager() {
        return recordManager;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;

//        recordManager = MediaManager.getInstance(getExternalFilesDir(Environment.DIRECTORY_MUSIC));
        recordManager = MediaManager.getInstance(Environment.getExternalStorageDirectory());

    }

}
