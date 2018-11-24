package lee.com.audiotalkie.utils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * CreateDate：18-10-19 on 下午2:44
 * Describe:
 * Coder: lee
 */
public class DataUtil {

    private final static String TAG = "Lee_log_DataUtil";

    public static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && (inetAddress instanceof Inet4Address)) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e(TAG, ex.toString());
        }
        return null;
    }

    public static Map<String,String> getAllLocalBroadIp() {
        Map<String,String> LocalIpAndbroadcastIp = new HashMap<>();
        try {
            Enumeration<NetworkInterface> b = NetworkInterface.getNetworkInterfaces();
            while (b.hasMoreElements()) {
                for (InterfaceAddress f : b.nextElement().getInterfaceAddresses()){
                    if(f.getBroadcast() != null){
                        LocalIpAndbroadcastIp.put(f.getAddress().getHostAddress(), f.getBroadcast().getHostAddress());
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return LocalIpAndbroadcastIp;
    }

    public static String getNetIp() {
        URL infoUrl = null;
        InputStream inStream = null;
        String ipLine = "";
        HttpURLConnection httpConnection = null;
        try {
            infoUrl = new URL("http://ip168.com/");
//            infoUrl = new URL("http://pv.sohu.com/cityjson?ie=utf-8");
            URLConnection connection = infoUrl.openConnection();
            httpConnection = (HttpURLConnection) connection;
            int responseCode = httpConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                inStream = httpConnection.getInputStream();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(inStream, "utf-8"));
                StringBuilder strber = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null){
                    strber.append(line + "\n");
                }
                Pattern pattern = Pattern
                        .compile("((?:(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d))))");
                Matcher matcher = pattern.matcher(strber.toString());
                if (matcher.find()) {
                    ipLine = matcher.group();
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inStream.close();
                httpConnection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        Log.e(TAG, "getNetIp " + ipLine);
        return ipLine;
    }


    /**
     * ------------------------------------------------------- 协议用 -----------------------------------------------------
     */

    public static int randomSequenceNumber(){
        return new Random().nextInt(10);
    }

    public static short getSequenceNumber(byte[] b) {
        if (null == b || b.length < 4){
            Log.e(TAG, "getSequenceNumber on a bad byteArray ! ");
            return 0;
        }
        return ByteUtil.byteToShort(Arrays.copyOfRange(b, 2, 4));

    }

    public static int getSsrc(byte[] b) {
        if (null == b || b.length < 12){
            Log.e(TAG, "getSsrc on a bad byteArray ! ");
            return 0;
        }
        return ByteUtil.byteToInt(Arrays.copyOfRange(b, 8, 12));

    }

    public static short getLength(byte[] b) {
        if (null == b || b.length < 20){
            Log.e(TAG, "getLength on a bad byteArray ! ");
            return 0;
        }
        return ByteUtil.byteToShort(Arrays.copyOfRange(b, 18, 20));

    }

    public static int getUserId(byte[] b) {
        if (null == b || b.length < 28){
            Log.e(TAG, "getUserId on a bad byteArray !");
            return 0;
        }
        return ByteUtil.byteToInt(Arrays.copyOfRange(b, 20, 28));
    }

    public static int getTargetId(byte[] b) {
        if (null == b || b.length < 36){
            Log.e(TAG, "getTargetId on a bad byteArray !");
            return 0;
        }
        return ByteUtil.byteToInt(Arrays.copyOfRange(b, 28, 36));
    }

    public static boolean getOpusData(byte[] opusBytes, byte[] b, int len) {
        if (null == b || b.length < len){
            Log.e(TAG, "getOpusData on a bad byteArray !");
            return false;
        }
        System.arraycopy(b, b.length - len, opusBytes, 0, len);
        return true;
    }
}
