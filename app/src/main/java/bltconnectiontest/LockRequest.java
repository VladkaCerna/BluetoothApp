package bltconnectiontest;

/**
 * Created by cernav1 on 13.3.2018.
 * <p>
 * Model for lock request message
 */

public class LockRequest implements IRequest {
    private String phoneId;

    public LockRequest() {
    }

    public LockRequest(String phoneId) {
        this.phoneId = phoneId;
    }

    public String getPhoneId() {
        return phoneId;
    }

    public void setPhoneId(String phoneId) {
        this.phoneId = phoneId;
    }
}
