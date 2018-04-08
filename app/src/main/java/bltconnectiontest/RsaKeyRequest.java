package bltconnectiontest;

/**
 * Created by cernav1 on 24.3.2018.
 * <p>
 * Model for rsa public key request
 */

public class RsaKeyRequest implements IRequest {
    private String phoneId;

    public RsaKeyRequest() {
    }

    public RsaKeyRequest(String phoneId) {
        this.phoneId = phoneId;
    }

    public String getPhoneId() {
        return phoneId;
    }

    public void setPhoneId(String phoneId) {
        this.phoneId = phoneId;
    }
}
