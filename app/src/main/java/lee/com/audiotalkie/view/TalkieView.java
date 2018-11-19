package lee.com.audiotalkie.view;

import lee.com.audiotalkie.presenter.TalkiePresenter;

/**
 * CreateDate：18-10-18 on 下午3:46
 * Describe:
 * Coder: lee
 */
public interface TalkieView extends BaseView<TalkiePresenter>{

    void logAdd(String msg);

    void logClean();

    void socketReset();

    void setSpeak(boolean isSpeaking);

    void setCatchCount(long count);

}
