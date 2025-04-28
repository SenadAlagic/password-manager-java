package dev.senad;

import com.google.gson.Gson;
import secureEntry.SecureEntry;
import utility.EncryptionUtil;
import vault.Vault;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Main {
    private static final List<SecureEntry> secureEntries = new ArrayList<>();

    public static void main(String[] args) {
        initializeVault();
        runningLoop();
    }

    public static void runningLoop() {
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
                case "test-decrypt":
                    readSecureEntry();
                    break;
                default:
                    System.out.println("Unknown command.");
                    break;
            }
        }
    }

    public static void createSecureEntry() {
        char[] masterPassword = grabMasterPassword(true);
        Scanner input = new Scanner(System.in);

        System.out.print("Website: ");
        String website = input.nextLine();

        System.out.print("Username: ");
        String username = input.nextLine();

        System.out.print("Password: ");
        String password = input.nextLine();

        String salt = EncryptionUtil.generateSalt();
        byte[] decodedSalt = Base64.getDecoder().decode(salt);
        byte[] key = EncryptionUtil.generateKey(masterPassword, decodedSalt);
        String encryptedPassword = EncryptionUtil.encrypt(password, key);

        SecureEntry secureEntry = new SecureEntry(website, username, encryptedPassword, salt);
        secureEntries.add(secureEntry);
        String fileName = "vault.txt";
        Path path = Paths.get(fileName);
        if (!Files.exists(path)) {
            System.out.println("Error: File does not exist.");
            return;
        }
        Gson gson = new Gson();
        String json = gson.toJson(secureEntries);
        encryptVault(json);
        System.out.println("Secure entry saved to vault.");
    }

    public static void encryptVault(String jsonEntries) {
        char[] masterPassword = grabMasterPassword(true);
        String salt = EncryptionUtil.generateSalt();
        byte[] decodedSalt = Base64.getDecoder().decode(salt);
        byte[] key = EncryptionUtil.generateKey(masterPassword, decodedSalt);
        String encryptedJsonEntries = EncryptionUtil.encrypt(jsonEntries, key);
        Vault vault = new Vault(salt, encryptedJsonEntries);
        Gson gson = new Gson();
        String json = gson.toJson(vault);
        String fileName = "vault.txt";
        Path path = Paths.get(fileName);
        try {
            Files.write(path, json.getBytes());
        } catch (IOException e) {
            System.out.println("Error writing vault to file: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void decryptVault(Vault vault) {
        char[] masterPassword = grabMasterPassword(false);
        String payload = EncryptionUtil.decrypt(masterPassword, vault.salt, vault.encryptedPayload);
        Gson gson = new Gson();
        SecureEntry[] entries = gson.fromJson(payload, SecureEntry[].class);
        secureEntries.addAll(Arrays.asList(entries));
    }

    public static char[] grabMasterPassword(boolean confirmation) {
        Scanner input = new Scanner(System.in);
        System.out.print("Master password: ");
        char[] masterPassword = input.nextLine().toCharArray();
        if (confirmation) {
            System.out.print("Master password: ");
            char[] confirmationMasterPassword = input.nextLine().toCharArray();

            if (!Arrays.equals(masterPassword, confirmationMasterPassword)) {
                System.out.println("Master passwords do not match.");
                return null;
            }
        }
        return masterPassword;
    }

    public static void listSecureEntries() {
        if (secureEntries.isEmpty()) {
            System.out.println("No secure entries found.");
        } else {
            for (SecureEntry entry : secureEntries)
                System.out.println(entry.toString());
        }
    }

    public static void readSecureEntry() {
        Scanner input = new Scanner(System.in);

        char[] masterPassword = grabMasterPassword(false);

        System.out.print("Enter the website name of the entry to decrypt: ");
        String websiteName = input.nextLine();
        for (SecureEntry entry : secureEntries) {
            System.out.println(entry.toString());
        }
        Optional<SecureEntry> entry = secureEntries.stream().filter(located -> located.website.equals(websiteName)).findFirst();
        if (entry.isEmpty()) {
            System.out.println("Invalid website name.");
            return;
        }
        String decryptedPassword = EncryptionUtil.decrypt(masterPassword, entry.get().salt, entry.get().encryptedPassword);
        System.out.println("Decrypted Password: " + decryptedPassword);
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
                Vault vault = gson.fromJson(content, Vault.class);
                if (vault != null) {
                    decryptVault(vault);
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