package secureEntry;

public class SecureEntry {
    private final String website;
    private final String username;
    private final String encryptedPassword;
    private final String salt;

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
