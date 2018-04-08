package bltconnectiontest;

/**
 * Created by cernav1 on 13.3.2018.
 * <p>
 * Model for message
 */

public class Message {
    private MessageFormat encryption;
    private IPayload payload;
    private PayloadType type;

    public Message() {
    }

    public Message(MessageFormat encryption) {
        this.encryption = encryption;
    }

    public enum MessageFormat {
        EncryptedAes(0xFF),
        EncryptedRsa(0xF0),
        PlainText(0x00);

        private final byte byteId;

        MessageFormat(int id) {
            byteId = (byte) id;
        }

        public byte getId() {
            return byteId;
        }

        public static MessageFormat valueOf(byte b) {
            MessageFormat[] values = MessageFormat.values();
            for (MessageFormat value : values) {
                if (value.getId() == b) {
                    return value;
                }
            }
            throw new IllegalArgumentException("Invalid input byte");
        }
    }

    public enum PayloadType {
        LockRequest(1),
        UnlockRequest(2),
        RsaKeyRequest(3),
        AesKeyRequest(4),
        EchoRequest(5),
        LockResponse(10),
        UnlockResponse(20),
        RsaKeyResponse(30),
        AesKeyResponse(40),
        EchoResponse(50);

        private final int id;

        PayloadType(int id) {
            this.id = id;
        }

        public int getId() {
            return this.id;
        }
    }

    public MessageFormat getEncryption() {
        return this.encryption;
    }

    public void setEncryption(MessageFormat encryption) {
        this.encryption = encryption;
    }

    public void setPayload(IPayload payload) {
        this.payload = payload;
    }

    public IPayload getPayload() {
        return payload;
    }

    public PayloadType getType() {
        return type;
    }

    public void setType(PayloadType type) {
        this.type = type;
    }

    public String toString() {
        String ret = String.format("Message encyption: %s \n", encryption)
                + String.format("Message payload: \n  %s", payload.toString())
                + String.format("Request type: %s \n", type);
        return ret;
    }
}
