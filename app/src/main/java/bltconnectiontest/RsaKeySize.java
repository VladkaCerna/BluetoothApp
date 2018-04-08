package bltconnectiontest;

/**
 * Created by cernav1 on 8.4.2018.
 */

public enum RsaKeySize {
    Rsa_1024(1024),
    Rsa_2048(2048),
    Rsa_4096(4096);

    private final int id;

    RsaKeySize(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }
}
