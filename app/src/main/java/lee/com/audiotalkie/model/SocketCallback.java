package lee.com.audiotalkie.model;

/**
 * CreateDate：18-10-19 on 上午10:27
 * Describe:
 * Coder: lee
 */
public interface SocketCallback {

    void socketSend(String msg);

    void socketReceive(String msg);

    void tcpAppendMsg(String msg);

    void tcpError(String msg);

}
