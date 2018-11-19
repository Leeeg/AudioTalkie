package lee.com.audiotalkie.model;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * CreateDate：18-11-7 on 上午10:54
 * Describe:
 * Coder: lee
 */
public class VoiceSource {

    public static BlockingDeque<byte[]> voiceDeque = new LinkedBlockingDeque<>(1000);

}
