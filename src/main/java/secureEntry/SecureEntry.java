package secureEntry;

public class SecureEntry {
    public final String website;
    public final String username;
    public final String encryptedPassword;
    public final String salt;

    public SecureEntry(String website, String username, String password, String salt) {
        this.website = website;
        this.username = username;
        this.encryptedPassword = password;
        this.salt = salt;
    }

    @Override
    public String toString() {
        return "Website: " + website + "\n" +
                "Username: " + username + "\n" +
                "Password: " + encryptedPassword + "\n" +
                "Salt: " + salt + "\n";
    }
}
