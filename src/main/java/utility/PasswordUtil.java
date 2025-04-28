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

    public enum Strength {
        VERY_WEAK, WEAK, MODERATE, STRONG, VERY_STRONG
    }

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

    public static Strength checkStrength(String password) {
        if (password == null || password.length() < 6) {
            return Strength.VERY_WEAK;
        }

        int strengthLevel = 0;
        boolean hasLower = false;
        boolean hasUpper = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;

        for (char c : password.toCharArray()) {
            if (Character.isLowerCase(c)) {
                hasLower = true;
                strengthLevel++;
            } else if (Character.isUpperCase(c)) {
                hasUpper = true;
                strengthLevel++;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
                strengthLevel++;
            } else if (!Character.isLetterOrDigit(c) && !Character.isWhitespace(c)) {
                hasSpecial = true;
                strengthLevel++;
            }
        }

        if (password.length() < 8 && strengthLevel > 1) return Strength.WEAK;
        if (password.length() >= 8 && password.length() < 10 && strengthLevel >= 2) return Strength.MODERATE;
        if (password.length() >= 10 && password.length() < 12 && strengthLevel >= 3) return Strength.STRONG;
        if (password.length() >= 12 && strengthLevel == 4) return Strength.VERY_STRONG;

        return Strength.WEAK; // Default
    }
}
