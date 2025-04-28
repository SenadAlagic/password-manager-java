package vault;

public class Vault {
    public String salt;
    public String encryptedPayload;

    public Vault(String salt, String encryptedPayload) {
        this.salt = salt;
        this.encryptedPayload = encryptedPayload;
    }
}
