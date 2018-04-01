package bltconnectiontest;

/**
 * Created by cernav1 on 13.3.2018.
 */

// model for lock request message
public class LockRequest implements IRequest {
    private String phoneId;
    private String message;

    public LockRequest() {
    }

    public LockRequest(String phoneId, String message) {
        this.phoneId = phoneId;
        this.message = message;
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

    public String toString() {
        String ret = String.format("PhoneId: %s \n", phoneId);
        return ret;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
