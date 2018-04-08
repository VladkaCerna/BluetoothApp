package bltconnectiontest;

/**
 * Created by cernav1 on 24.3.2018.
 * <p>
 * Model for echo request message
 */

public class EchoRequest implements IRequest {
    private String phoneId;
    private String randomString;

    public EchoRequest() {
    }

    public EchoRequest(String phoneId, String randomString) {
        this.phoneId = phoneId;
        this.randomString = randomString;
    }

    public String getRandomString() {
        return randomString;
    }

    public void setRandomString(String randomString) {
        this.randomString = randomString;
    }

    public String getPhoneId() {
        return phoneId;
    }

    public void setPhoneId(String phoneId) {
        this.phoneId = phoneId;
    }
}
