package lee.com.audiotalkie.channel;

/**
 * CreateDate：18-11-23 on 下午3:39
 * Describe:
 * Coder: lee
 */
public class TalkieChannel {

    public static TalkieChannel getInstance() {
        return TalkieChannel.TalkieChannelHolder.instance;
    }

    private static class TalkieChannelHolder {
        private static final TalkieChannel instance = new TalkieChannel();
    }

    public TalkieChannel() {
        super();
    }

    private boolean isWorking;

    private int callId;

    private long userId;

    private long targetId;

    private short seq;

    public boolean isWorking() {
        return isWorking;
    }

    public boolean work() {
        isWorking = true;
        return true;
    }

    public int getCallId() {
        return callId;
    }

    public TalkieChannel setCallId(int callId) {
        this.callId = callId;
        return getInstance();
    }

    public long getUserId() {
        return userId;
    }

    public TalkieChannel setUserId(long userId) {
        this.userId = userId;
        return getInstance();
    }

    public long getTargetId() {
        return targetId;
    }

    public TalkieChannel setTargetId(long targetId) {
        this.targetId = targetId;
        return getInstance();
    }

    public short getSeq() {
        seq++;
        return seq;
    }

    public TalkieChannel reSetSeq() {
        this.seq = 0;
        return getInstance();
    }

    public void clear() {
        isWorking = false;
        callId = 0;
        seq = 0;
        userId = 0;
        targetId = 0;
    }

    public String getString() {
        return "---------TalkieChannel------->>" +
                "\nisWorking = " + isWorking +
                "\nseq = " + seq +
                "\nuserId = " + userId +
                "\ntargetId = " + targetId +
                "<<----------------";
    }

}
