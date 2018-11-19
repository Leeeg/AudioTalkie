package lee.com.audiotalkie.model;

/**
 * CreateDate：18-11-19 on 上午10:02
 * Describe:
 * Coder: lee
 */
public enum  NetConfig {

    HOST("47.106.204.40"),
//    HOST("192.168.0.6"),
    PORT("8888");


    private String value;

    public String getValue() {
        return value;
    }

    NetConfig(String value) {
        this.value = value;
    }
}
