package bltconnectiontest;

/**
 * Created by cernav1 on 25.3.2018.
 */

// model for aes key request message
public class AesKeyRequest implements IRequest {
    private String aesKey;
    private String phoneId;

    public AesKeyRequest() {

    }

    public AesKeyRequest(String aesKey, String phoneId) {
        this.aesKey = aesKey;
        this.phoneId = phoneId;
    }

    public String getAesKey() {
        return aesKey;
    }

    public void setAesKey(String aesKey) {
        this.aesKey = aesKey;
    }

    public String getPhoneId() {
        return phoneId;
    }

    public void setPhoneId(String phoneId) {
        this.phoneId = phoneId;
    }
}
