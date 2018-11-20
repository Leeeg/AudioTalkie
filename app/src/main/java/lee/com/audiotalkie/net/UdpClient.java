package lee.com.audiotalkie.net;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import lee.com.audiotalkie.callBack.SocketCallback;

/**
 * CreateDate：18-10-25 on 上午10:17
 * Describe:
 * Coder: lee
 */
public class UdpClient {

    private String host;
    private int port;
    private DatagramSocket datagramSocket = null;
    private DatagramSocket inDatagramSocket = null;
    private InetSocketAddress inetSocketAddress = null;
    private byte[] buffer = new byte[256];
    private InetAddress local = null;
    private DatagramPacket outPacket, inPacket;
    private SocketCallback socketCallback;
    private BlockingDeque<byte[]> blockingDeque = new LinkedBlockingDeque<>(1000);
    private int count;


    public UdpClient(String host, int port, SocketCallback socketCallback) {
//        this.host = "192.168.0.16";
//        this.port = 8887;
        this.host = host;
        this.port = port;
        this.socketCallback = socketCallback;
        Log.e("UdpClient : ", "host = " + host + "    port = " + port);
    }

    public BlockingDeque<byte[]> getBlockingDeque() {
        return blockingDeque;
    }

    public void initSocket() {

        try {

            datagramSocket = new DatagramSocket();
            local = InetAddress.getByName(host);
            inPacket = new DatagramPacket(buffer, buffer.length);

            new UdpSendThread().start();

            new UdpReceiveThread().start();

        } catch (SocketException e) {
            e.printStackTrace();
            Log.e("UdpClient : ", "UdpSendThread : ERROR : " + e);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            Log.e("UdpClient : ", "UdpSendThread : ERROR : " + e);
        }

    }

    private void sendUdp(byte[] data) {
        outPacket = new DatagramPacket(data, data.length, local, port);
        try {
            datagramSocket.send(outPacket);
        } catch (IOException e) {
            Log.e("UdpClient : ", "sendUdp ERROR : " + e);
            e.printStackTrace();
        }
    }

    class UdpSendThread extends Thread {
        @Override
        public void run() {
            Log.d("UdpClient : ", "UdpSendThread running ------------------ ");
            byte[] data;
            while (true) {
                try {
                    data = blockingDeque.takeFirst();
                    if (null != data) {
                        System.out.println(" 发送数据 ： length = " + data.length + "           count = " + count ++);
                        sendUdp(data);
                    }
                } catch (InterruptedException e) {
                    Log.e("UdpClient : ", "UdpSendThread : ERROR : " + e);
                    e.printStackTrace();
                }
            }
        }
    }

    class UdpReceiveThread extends Thread {
        @Override
        public void run() {
            Log.d("UdpClient : ", "UdpReceiveThread running ----------------- ");
//            try {
//                WifiManager manager = (WifiManager) TalkieApplication.getInstance().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//                manager.createMulticastLock("UDPWifi");
//                inetSocketAddress = new InetSocketAddress(port);
//                inDatagramSocket = new DatagramSocket(port);
//                inDatagramSocket.setBroadcast(true);
//            } catch (SocketException e) {
//                Log.e("UdpClient : ", "UdpSendThread : ERROR : " + e);
//                e.printStackTrace();
//            }
            byte[] data;
            while (true) {
                try {
                    datagramSocket.receive(inPacket);
                    System.out.println(" 收到数据 ： length = " + inPacket.getLength() + "           count = " + count ++);
                    data = new byte[inPacket.getLength()];
                    System.arraycopy(inPacket.getData(), inPacket.getOffset(), data, 0, inPacket.getLength());
                    socketCallback.socketReceive(data);
                } catch (IOException e) {
                    Log.e("UdpClient : ", "UdpSendThread : ERROR : " + e);
                    e.printStackTrace();
                }
            }
        }
    }

}
