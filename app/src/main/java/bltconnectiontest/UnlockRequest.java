package bltconnectiontest;

/**
 * Created by cernav1 on 13.3.2018.
 */

//model for unlock request
public class UnlockRequest implements IRequest {
    private String PhoneId;

    public UnlockRequest() {
    }

    public UnlockRequest(String phoneId) {
        PhoneId = phoneId;
    }

    public String  getPhoneId() {
        return PhoneId;
    }

    public void setPhoneId(String phoneId) {
        PhoneId = phoneId;
    }
}
