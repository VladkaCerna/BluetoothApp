package bltconnectiontest;

import java.util.UUID;

/**
 * Created by cernav1 on 13.3.2018.
 */

//model for unlock request
public class UnlockRequest implements IRequest {
    private UUID PhoneId;

    public UnlockRequest() {
    }

    public UUID getPhoneId() {
        return PhoneId;
    }

    public void setPhoneId(UUID phoneId) {
        PhoneId = phoneId;
    }
}
