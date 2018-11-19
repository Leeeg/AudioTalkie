package lee.com.audiotalkie.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import lee.com.audiotalkie.callBack.SocketCallback;

/**
 * CreateDate：18-10-19 on 上午10:22
 * Describe:
 * Coder: lee
 */
public class TcpClient {

    private DataOutputStream writer;
    private String host;
    private int port;
    private ClientThread clientThread;
    private SocketCallback socketCallback;
    private Boolean connectAble;

    public TcpClient(String host, int port, SocketCallback socketCallback) {
        this.host = host;
        this.port = port;
        this.socketCallback = socketCallback;
    }

    public void closeSocket() {
        connectAble = false;
    }

    public void initSocket() {
        connectAble = true;
        // 建立连接后就可以往服务端写数据了
        clientThread = new ClientThread();
        clientThread.start();
    }

    public DataOutputStream getWriter() {
        return writer;
    }

    class ClientThread extends Thread {
        private Socket socket = null;
        private DataInputStream reader = null;
        private int len = 0;
        byte buffer[] = new byte[140];
        private String temp = "";

        public ClientThread() {

        }

        @Override
        public void run() {
            super.run();
            try {
                socket = new Socket(host, port);
                socketCallback.tcpAppendMsg("已连上服务器");
                writer = new DataOutputStream(socket.getOutputStream());
                reader = new DataInputStream(socket.getInputStream());
                System.out.println("客户端子线程" + this.getId() + "开始工作");
            } catch (IOException e) {
                connectAble = false;
                System.out.println("socket connect failed ");
                socketCallback.tcpError("socket connect failed ！");
            }
            byte[] bytes;
            while (connectAble) {
                try {
                    if (socket.isClosed() == false) {
                        if (socket.isInputShutdown() == false) {
                            while (reader.read(buffer) != -1) {
                                temp = "收到服务器消息——>";
                                socketCallback.tcpAppendMsg(temp);
                                bytes = buffer.clone();
                                socketCallback.socketReceive(bytes);
                                System.out.println();
                            }
                        }
                    } else {
                        if (socket.getKeepAlive() == false) {
                            connectAble = false;
                            reader.close();
                            writer.close();
                            socket.close();
                            socketCallback.tcpAppendMsg("异常断开！");
                            this.stop();
                        }
                    }
                } catch (IOException e) {
                    socketCallback.tcpAppendMsg("异常断开！");
                    e.printStackTrace();
                }
            }
            try {
                socketCallback.tcpAppendMsg("正常断开");
                reader.close();
                writer.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullPointerException e){

            }
        }
    }
}
