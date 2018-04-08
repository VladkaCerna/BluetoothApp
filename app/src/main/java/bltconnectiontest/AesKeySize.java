package bltconnectiontest;

/**
 * Created by cernav1 on 8.4.2018.
 *
 */

public enum AesKeySize {
    Aes_128(128),
    Aes_192(192),
    Aes_256(256);

    private final int id;

    AesKeySize(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }
}
