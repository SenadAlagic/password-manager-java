package utility;

import java.io.Console;
import java.util.Arrays;

public class PasswordUtil {
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
}
