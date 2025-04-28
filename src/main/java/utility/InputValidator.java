package utility;

import java.util.Scanner;

public class InputValidator {
    public static boolean isValid(String input) {
        return input != null && !input.trim().isEmpty();
    }

    public static String secureInput(Scanner input, String prompt) {
        System.out.print(prompt);
        String userInput = input.nextLine();
        while (!isValid(userInput)) {
            System.out.println("Invalid input: This field cannot be empty.");
            System.out.print(prompt);
            userInput = input.nextLine();
        }
        return userInput;
    }
}