package lee.com.audiotalkie.view;


import lee.com.audiotalkie.presenter.BasePresenter;

public interface BaseView<P extends BasePresenter> {

    void setPresenter(P presenter);

}
