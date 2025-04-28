# Secure Password Manager (CLI & Future GUI)

[![GitHub Release](https://img.shields.io/github/v/release/SenadAlagic/password-manager-java?sort=semver)](https://github.com/SenadAlagic/password-manager-java/releases)
[![Conventional Commits](https://img.shields.io/badge/Conventional%20Commits-1.0.0-yellow.svg)](https://www.conventionalcommits.org/en/v1.0.0/)

This is a secure password manager application built in Java. It currently features a command-line interface (CLI) for securely storing and retrieving your passwords. The project aims to evolve into a more user-friendly GUI application in the future, potentially with browser integration.

## Features (CLI - Current)

* **Secure Vault:** Encrypts all stored password entries using AES encryption with a key derived from your master password and a unique salt for each entry and the entire vault.
* **Password Creation:** Allows users to manually input website, username, and password.
* **Auto-Generate Passwords:** Offers the option to generate strong, random passwords with customizable length and character sets.
* **Password Strength Meter:** Provides feedback on the strength of manually entered passwords.
* **List Entries:** Displays a list of stored website and username entries (encrypted).
* **Decrypt and View Password:** Securely decrypts and displays the password for a specified website after master password authentication.
* **Copy to Clipboard:** Option to copy the decrypted password directly to the system clipboard.
* **Vault Encryption:** The entire password vault file (`vault.txt`) is encrypted for enhanced security.
* **Master Password Protection:** Uses PBKDF2 with a high iteration count to derive encryption keys from the user's master password, making brute-force attacks significantly harder.

## Planned Features (Future)

* Graphical User Interface (GUI) for easier interaction.
* Browser Integration (potentially via extensions).
* Import/Export functionality for data portability and backup.
* Advanced password generation customization.
* Two-Factor Authentication (2FA) for vault access.

## How to Use (CLI)

1.  **Clone the repository:**
    ```bash
    git clone (https://github.com/SenadAlagic/password-manager-java.git)
    ```
2.  **Compile the Java code:**
    ```bash
    javac src/Main.java src/secureEntry/SecureEntry.java src/utility/*.java
    ```
3.  **Run the application:**
    ```bash
    java Main
    ```
4.  **Follow the prompts in the CLI:**
    * Type `create` to add a new secure entry.
    * Type `list` to view your stored entries (website and username).
    * Type `test-decrypt` to decrypt and view a password for a specific website.
    * Type `exit` to close the application.

## How to Contribute

We welcome contributions to make this password manager even better! If you'd like to contribute, please follow these guidelines:

1.  **Fork the repository** on GitHub.
2.  **Clone your fork** to your local machine.
3.  **Create a new branch** for your feature or bug fix:
    ```bash
    git checkout -b feat/your-new-feature
    git checkout -b fix/your-bug-fix
    ```
4.  **Make your changes** and ensure your code follows the existing style and conventions.
5.  **Pull the latest changes from the main repository** before pushing your own:
    ```bash
    git pull origin main
    ```
    This helps to avoid merge conflicts. Resolve any conflicts that arise.
6.  **Commit your changes** using the [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/) specification. This project uses automatic versioning based on these commit messages. Examples:
    * `feat: Implement secure password generation`
    * `fix: Correct decryption issue with special characters`
    * `feat(gui): Add basic window layout`
    * `feat(major): Introduce a new authentication mechanism`
7.  **Push your branch** to your forked repository:
    ```bash
    git push origin feat/your-new-feature
    ```
8.  **Create a Pull Request** to the `main` branch of the main repository on GitHub.

Please provide a clear and concise description of your changes in the pull request.

## License

This project is licensed under the [MIT License](LICENSE). See the `LICENSE` file for more information.

## Disclaimer

This password manager is provided as is, without any warranty. While security measures have been implemented, the authors are not responsible for any data breaches or loss. Users are advised to use strong master passwords and understand the risks involved in storing sensitive information.

## Author

Alagić Senad & Huseinbašić Hamza

## Acknowledgements

* [Mention any libraries or resources you found particularly helpful]
