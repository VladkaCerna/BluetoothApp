package bltconnectiontest;

/**
 * Created by cernav1 on 26.3.2018.
 */
// model for echo response message
public class EchoResponse implements IResponse{
    private String phoneId;
    private String randomString;

    public EchoResponse() {

    }

    public EchoResponse(String phoneId, String randomString) {
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
