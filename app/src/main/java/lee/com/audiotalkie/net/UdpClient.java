package lee.com.audiotalkie.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

/**
 * CreateDate：18-10-25 on 上午10:17
 * Describe:
 * Coder: lee
 */
public class UdpClient {

    private String host;
    private int port;
    DatagramSocket datagramSocket = null;
    InetAddress local = null;
    DatagramPacket packet;
    BlockingDeque<byte[]> blockingDeque = new LinkedBlockingDeque<>(100);
    private int count;

    public UdpClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public BlockingDeque<byte[]> getBlockingDeque() {
        return blockingDeque;
    }

    public void initSocket() {
        try {
            datagramSocket = new DatagramSocket();
            local = InetAddress.getByName(host);
            new UdpThread().start();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    private void sendUdp(byte[] data) {
        packet = new DatagramPacket(data, data.length, local, port);
        try {
            datagramSocket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class UdpThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (true){
                try {
                    byte[] data = blockingDeque.poll(1, TimeUnit.SECONDS);
                    if (null != data){
                        System.out.println(" 取到数据 ： length = " + data.length + "           count = " + (count += data.length));
                        sendUdp(data);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
