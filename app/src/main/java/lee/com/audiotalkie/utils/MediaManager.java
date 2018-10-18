package lee.com.audiotalkie.utils;

import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import lee.com.audiotalkie.model.MediaPlayCallback;
import lee.com.audiotalkie.model.RecordConfig;


public class MediaManager {

    private final String TAG = "Lee_RecordManager";
    private AudioRecord audioRecord;
    private static MediaManager recordManager;
    private MediaPlayer mediaPlayer;
    private final int minBufferSize;
    private AudioTrack audioTrack;
    private boolean isRecording;
    private File filePath;
    private MediaPlayCallback callback;

    public MediaManager(File filePath) {
        minBufferSize = AudioRecord.getMinBufferSize(RecordConfig.SAMPLE_RATE_INHZ, RecordConfig.CHANNEL_CONFIG, RecordConfig.AUDIO_FORMAT);
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, RecordConfig.SAMPLE_RATE_INHZ, RecordConfig.CHANNEL_CONFIG, RecordConfig.AUDIO_FORMAT, minBufferSize);

        mediaPlayer = new MediaPlayer();

        this.filePath = filePath;
    }

    /**
     * 获取单例引用
     *
     * @param filePath
     * @return
     */
    public static MediaManager getInstance(File filePath) {
        if (recordManager == null) {
            synchronized (MediaManager.class) {
                if (recordManager == null) {
                    recordManager = new MediaManager(filePath);
                }
            }
        }
        return recordManager;
    }

    /**
     * 播放，使用stream模式
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void playInModeStream() {
        /*
         * SAMPLE_RATE_INHZ 对应pcm音频的采样率
         * channelConfig 对应pcm音频的声道
         * AUDIO_FORMAT 对应pcm音频的格式
         * */
        int channelConfig = AudioFormat.CHANNEL_OUT_MONO;
        final int minBufferSize = AudioTrack.getMinBufferSize(RecordConfig.SAMPLE_RATE_INHZ, channelConfig, RecordConfig.AUDIO_FORMAT);
        audioTrack = new AudioTrack(
                new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build(),
                new AudioFormat.Builder().setSampleRate(RecordConfig.SAMPLE_RATE_INHZ)
                        .setEncoding(RecordConfig.AUDIO_FORMAT)
                        .setChannelMask(channelConfig)
                        .build(),
                minBufferSize,
                AudioTrack.MODE_STREAM,
                AudioManager.AUDIO_SESSION_ID_GENERATE);
        audioTrack.play();

        File file = new File(filePath, "record.pcm");
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        byte[] tempBuffer = new byte[minBufferSize];
                        while (fileInputStream.available() > 0) {
                            int readCount = fileInputStream.read(tempBuffer);
                            if (readCount == AudioTrack.ERROR_INVALID_OPERATION ||
                                    readCount == AudioTrack.ERROR_BAD_VALUE) {
                                continue;
                            }
                            if (readCount != 0 && readCount != -1) {
                                audioTrack.write(tempBuffer, 0, readCount);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止播放
     */
    private void stopPlay() {
        if (audioTrack != null) {
            Log.d(TAG, "Stopping");
            audioTrack.stop();
            Log.d(TAG, "Releasing");
            audioTrack.release();
            Log.d(TAG, "Nulling");
        }
    }

    /**
     * 获取保存的pcm文件byte数组
     * @return
     */
    public byte[] getPcmFile() {
        final File file = new File(filePath, "record.pcm");
        if (file.exists()) {
            byte[] buffer = null;
            try {
                FileInputStream fis = new FileInputStream(file);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                byte[] b = new byte[1024];
                int n;
                while ((n = fis.read(b)) != -1) {
                    bos.write(b, 0, n);
                }
                fis.close();
                bos.close();
                buffer = bos.toByteArray();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return buffer;
        } else {
            Log.e(TAG, "the pcm file is not exists !");
            return null;
        }
    }

    /**
     * 开始录制
     */
    public void startRecord() {
//        final int minBufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE_INHZ, CHANNEL_CONFIG, AUDIO_FORMAT);
//        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE_INHZ, CHANNEL_CONFIG, AUDIO_FORMAT, minBufferSize);

        stopPlayMusic();

        final byte data[] = new byte[minBufferSize];
        final File file = new File(filePath, "record.pcm");
        Log.i(TAG, filePath + "     record.pcm");
        if (!file.mkdirs()) {
            Log.e(TAG, "Directory not created");
        }
        if (file.exists()) {
            file.delete();
        }

        audioRecord.startRecording();
        isRecording = true;

        new Thread(new Runnable() {
            @Override
            public void run() {

                FileOutputStream os = null;
                try {
                    os = new FileOutputStream(file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                if (null != os) {
                    while (isRecording) {
                        int read = audioRecord.read(data, 0, minBufferSize);
                        // 如果读取音频数据没有出现错误，就将数据写入到文件
                        if (AudioRecord.ERROR_INVALID_OPERATION != read) {
                            try {
                                os.write(data);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    try {
                        Log.i(TAG, "run: close file output stream !");
                        os.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    /**
     * 停止录制
     */
    public void stopRecord() {
        isRecording = false;
        if (null != audioRecord) {
            audioRecord.stop();
//            audioRecord.release();
//            audioRecord = null;
        }
    }

    public void playMusic(String url){
        if (null == mediaPlayer){
            mediaPlayer = new MediaPlayer();
        }else if (mediaPlayer.isPlaying()){
            mediaPlayer.stop();
        }
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener((mediaPlayer)-> mediaPlayer.start());
            mediaPlayer.setOnCompletionListener((mediaPlayer)-> callback.onPlayOver());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setMediaPlayerCallback(MediaPlayCallback callback){
        this.callback = callback;
    }

    public void stopPlayMusic(){
        if (null != mediaPlayer && mediaPlayer.isPlaying()){
            mediaPlayer.stop();
            mediaPlayer.reset();
        }
    }

    /**
     * 释放资源
     */
    public void releaseMediaManager(){
        isRecording = false;
        // 释放资源
        if (null != audioRecord) {
            audioRecord.stop();
            audioRecord.release();
            audioRecord = null;
        }
        if (null != mediaPlayer){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

}
