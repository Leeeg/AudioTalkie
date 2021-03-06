package lee.com.audiotalkie.presenter;

/**
 * CreateDate：18-10-18 on 下午3:44
 * Describe:
 * Coder: lee
 */
public interface TalkiePresenter extends BasePresenter {

    void recordStart();

    void recordStop();

    void trackStart();

    void trackStop();

    void opusEncode();

    void opusDecode();

    void socketInit(String ip, int port);

    void TcpClose();

    void TcpTest();

    void UdpTest();

}
