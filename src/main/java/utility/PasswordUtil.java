package utility;

import java.io.Console;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class PasswordUtil {
    private static final String UPPERCASE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE_CHARS = "abcdefghijklmnopqrstuvwxyz";
    private static final String NUMBER_CHARS = "0123456789";
    private static final String SPECIAL_CHARS = "!@#$%&*()_+-=[]{}|;':\",./<>?";

    public static char[] grabMasterPassword(boolean confirmation) {
        Console console = System.console();
        if (console == null) {
            throw new IllegalStateException("Console not available");
        }

        char[] masterPassword = console.readPassword("Master password: ");
        if (confirmation) {
            char[] confirmationPassword = console.readPassword("Confirm master password: ");
            if (!Arrays.equals(masterPassword, confirmationPassword)) {
                System.out.println("Master passwords do not match.");
                return null;
            }
        }
        return masterPassword;
    }

    public static String generateRandomPassword(int length, boolean includeUppercase, boolean includeLowercase, boolean includeNumbers, boolean includeSpecialChars) {
        StringBuilder password = new StringBuilder();
        List<String> allowedCharCategories = new ArrayList<>();
        if (includeUppercase) allowedCharCategories.add(UPPERCASE_CHARS);
        if (includeLowercase) allowedCharCategories.add(LOWERCASE_CHARS);
        if (includeNumbers) allowedCharCategories.add(NUMBER_CHARS);
        if (includeSpecialChars) allowedCharCategories.add(SPECIAL_CHARS);

        if (allowedCharCategories.isEmpty()) {
            throw new IllegalArgumentException("At least one character category must be selected.");
        }

        Random random = new Random();
        for (int i = 0; i < length; i++) {
            String charCategory = allowedCharCategories.get(random.nextInt(allowedCharCategories.size()));
            password.append(charCategory.charAt(random.nextInt(charCategory.length())));
        }
        return password.toString();
    }
}
