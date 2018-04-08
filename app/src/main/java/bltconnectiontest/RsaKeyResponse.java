package bltconnectiontest;

/**
 * Created by cernav1 on 25.3.2018.
 * <p>
 * Model for rsa public key response
 */

public class RsaKeyResponse implements IResponse {
    private String phoneId;
    private String publicKey;

    public RsaKeyResponse() {
    }

    public RsaKeyResponse(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getPhoneId() {
        return phoneId;
    }

    public void setPhoneId(String phoneId) {
        this.phoneId = phoneId;
    }
}
