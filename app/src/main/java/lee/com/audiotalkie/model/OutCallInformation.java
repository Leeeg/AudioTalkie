package lee.com.audiotalkie.model;

import java.util.Random;

/**
 * CreateDate：18-11-23 on 下午5:30
 * Describe:
 * Coder: lee
 */
public class OutCallInformation {

    private static int callId;
    private static long userId;
    private static long targetId;

    public OutCallInformation() {
    }

    public static int getCallId() {
        return callId;
    }

    public static void setCallId(int callId) {
        OutCallInformation.callId = callId;
    }

    public static long getUserId() {
        return userId;
    }

    public static void setUserId(long userId) {
        OutCallInformation.userId = userId;
    }

    public static long getTargetId() {
        return targetId;
    }

    public static void setTargetId(long targetId) {
        OutCallInformation.targetId = targetId;
    }

    public static boolean isIntact() {
        if (0 != callId && 0 != userId && 0 != targetId)
            return true;
        return false;
    }

    public static String getString() {
        return "---------OutCallInformation-------->>" +
                "\n callId = " + callId +
                "\n userId = " + userId +
                "\n targetId = " + targetId +
                "<<----------------";
    }
}
