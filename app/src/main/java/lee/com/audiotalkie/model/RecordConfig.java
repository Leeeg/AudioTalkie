package lee.com.audiotalkie.model;

import android.media.AudioFormat;
import android.media.MediaRecorder;


public enum RecordConfig {
    /**
     * AudioRecord
     * .
     * - 由于一些无效的参数或者其他错误会抛出IllegalArgumentException，所以在你构造了一个AudioRecord实例之后，需要立刻调用 getState() 方法来判断这个实例的状态是否可以使用。
     * 参数：
     * .
     * -audioSource：录音来源，查看MediaRecorder.AudioSource类对录音来源的定义。
     * MediaRecorder.AudioSource.DEFAULT   默认音频来源
     * MediaRecorder.AudioSource.MIC       麦克风
     * MediaRecorder.AudioSource.VOICE_UPLINK  上行线路的声音来源
     * 一般情况下，我们使用麦克风即可。
     * .
     * - sampleRateInHz：采样率，单位Hz(赫兹)。44100Hz是目前唯一一个能够在所有的设备上使用的频率，但是一些其他的例如22050、16000、11025也能够在一部分设备上使用。
     * .
     * - channelConfig：音频声道的配置(输入)
     * AudioFormat.CHANNEL_IN_MONO：单声道
     * AudioFormat.CHANNEL_IN_STEREO：立体声
     * 其中，CHANNEL_IN_MONO可以保证在所有设备上使用。
     * .
     * - audioFormat：返回的音频数据的编码格式
     * AudioFormat.ENCODING_INVALID：无效的编码格式
     * AudioFormat.ENCODING_DEFAULT：默认的编码格式
     * AudioFormat.ENCODING_PCM_16BIT：每份采样数据为PCM 16bit，保证所有设备支持
     * AudioFormat.ENCODING_PCM_8BIT：样本数据格式为PCM 8bit，不保证所有设备支持
     * AudioFormat.ENCODING_PCM_FLOAT：单精度浮点样本
     * .
     * - bufferSizeInBytes：在录音时期，音频数据写入的缓冲区的整体大小(单位字节)，
     * 即缓冲区的大小。我们能够从这个缓冲区中读取到不超过缓冲区长度的整块数据。
     * 可以通过 getMinBufferSize(int, int, int) 这个方法来决定我们使用的AudioRecord实例所需要的最小的缓冲区的大小，如果我们使用的数值比这个还要小，则会导致AudioRecord实例初始化失败。
     * .
     * ...............
     * .
     * AudioTrack
     * <p>
     * - streamType：音频流的类型
     * AudioManager.STREAM_VOICE_CALL:电话的音频流
     * AudioManager.STREAM_SYSTEM:系统的音频流
     * AudioManager.STREAM_RING:闹钟
     * AudioManager.STREAM_MUSIC:音乐
     * AudioManager.STREAM_ALARM:警告声
     * AudioManager.STREAM_NOTIFICATION:通知
     * .
     * - sampleRateInHz：来源的音频的采样频率，单位Hz
     * .
     * - channelConfig：音频声道的配置
     * AudioFormat.CHANNEL_OUT_MONO:单声道输出(左)
     * AudioFormat.CHANNEL_OUT_STEREO:立体声输出(左和右)
     * .
     * - audioFormat：音频格式
     * AudioFormat.ENCODING_INVALID：无效的编码格式
     * AudioFormat.ENCODING_DEFAULT：默认的编码格式
     * AudioFormat.ENCODING_PCM_16BIT：每份采样数据为PCM 16bit，保证所有设备支持
     * AudioFormat.ENCODING_PCM_8BIT：样本数据格式为PCM 8bit，不保证所有设备支持
     * AudioFormat.ENCODING_PCM_FLOAT：单精度浮点样本
     * .
     * - bufferSizeInBytes：缓冲区的大小
     * 该缓冲区是为了存放需要回放的音频流数据，单位为字节。AudioTrack实例不断的从该缓冲区内读取写入的音频流数据，然后播放出来。它的大小应该是框架层尺寸的数倍。
     * 如果该声轨的创建模式是"AudioTrack.MODE_STATIC"，
     * .
     * - mode：流或者是静态缓存
     * AudioTrack.MODE_STATIC:创建模式-在音频开始播放之前，音频数据仅仅只会从Java层写入到本地层中一次。即开始播放前一次性写入音频数据。
     * AudioTrack.MODE_STREAM:创建模式-在音频播放的时候，音频数据会同时会以流的形式写入到本地层中。即一边播放，一边写入数据。(很明显，如果实现一边录音一边播放的话，用这个模式创建声轨)
     */


    // 音频获取源
    MIC(MediaRecorder.AudioSource.VOICE_COMMUNICATION),

    //采样率，现在能够保证在所有设备上使用的采样率是44100Hz, 但是其他的采样率（22050, 16000, 11025）在一些设备上也可以使用。
    SAMPLE_RATE_INHZ(8000),

    //采集声道数，CHANNEL_IN_STEREO为双声道，CHANNEL_CONFIGURATION_MONO为单声道. 其中CHANNEL_IN_MONO是可以保证在所有设备能够使用的。
    CHANNEL_IN_CONFIG(AudioFormat.CHANNEL_IN_MONO),

    CHANNEL_OUT_CONFIG(AudioFormat.CHANNEL_OUT_MONO),

    //音频数据格式:PCM 16位每个样本。保证设备支持。PCM 8位每个样本。不一定能得到设备支持
    AUDIO_FORMAT(AudioFormat.ENCODING_PCM_16BIT);


    private int value;

    public int getValue() {
        return value;
    }

    RecordConfig(int value) {
        this.value = value;
    }

}
