package dev.senad;

import com.google.gson.Gson;
import secureEntry.SecureEntry;
import utility.EncryptionUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        initializeVault();
        boolean running = true;
        Scanner input = new Scanner(System.in);
        while (running) {
            System.out.print(">> ");
            String command = input.nextLine();
            switch (command) {
                case "exit":
                    running = false;
                    break;
                case "create":
                    createSecureEntry();
                    break;
                case "list":
                    listSecureEntries();
                    break;
                default:
                    System.out.println("Unknown command.");
                    break;
            }
        }
    }

    private static List<SecureEntry> secureEntries = new ArrayList<>();

    public static void createSecureEntry() {
        Scanner input = new Scanner(System.in);

        System.out.print("Website: ");
        String website = input.nextLine();

        System.out.print("Username: ");
        String username = input.nextLine();

        System.out.print("Password: ");
        String password = input.nextLine();

        String salt = EncryptionUtil.generateSalt();
        byte[] key = EncryptionUtil.generateKey(salt.getBytes());
        String encryptedPassword = EncryptionUtil.encryptPassword(password, key);

        SecureEntry secureEntry = new SecureEntry(website, username, encryptedPassword, salt);
        secureEntries.add(secureEntry);
        try {
            String fileName = "vault.txt";
            Path path = Paths.get(fileName);
            if (!Files.exists(path)) {
                System.out.println("Error: File does not exist.");
                return;
            }
            Gson gson = new Gson();
            String json = gson.toJson(secureEntries);
            Files.write(path, json.getBytes());
            System.out.println("Secure entry saved to vault.");
        } catch (IOException e) {
            System.err.println("An error occurred while creating the secure entry: " + e.getMessage());
        }
    }

    public static void listSecureEntries() {
        if (secureEntries.isEmpty()) {
            System.out.println("No secure entries found.");
        } else {
            for (SecureEntry entry : secureEntries)
                System.out.println(entry.toString());
        }
    }

    public static void initializeVault() {
        try {
            String fileName = "vault.txt";
            Path path = Paths.get(fileName);
            if (!Files.exists(path)) {
                Files.write(path, "[]".getBytes());
                System.out.println("File created successfully.");
            } else {
                String content = Files.readString(path);
                Gson gson = new Gson();
                SecureEntry[] entriesArray = gson.fromJson(content, SecureEntry[].class);
                if (entriesArray != null) {
                    secureEntries.addAll(Arrays.asList(entriesArray));
                    System.out.println("Vault data loaded successfully.");
                } else {
                    System.out.println("Vault file is empty or contains invalid data.");
                }
            }
        } catch (IOException e) {
            System.err.println("An error occurred while creating the file: " + e.getMessage());
        }
    }
}