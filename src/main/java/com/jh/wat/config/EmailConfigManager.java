package com.jh.wat.config;

import com.jh.wat.util.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;

public class EmailConfigManager {

    // List of keys to hide from user
    private static final Set<String> hiddenKeys = new HashSet<>();

    static {
        hiddenKeys.add("smtp.starttls.enable");
//        hiddenKeys.add("body.text");
        hiddenKeys.add("smtp.auth");
//        hiddenKeys.add("smtp.port");
//        hiddenKeys.add("subject.prefix");
//        hiddenKeys.add("smtp.host");
    }

    public static void main(String[] args) {
        updateConfig(args);
    }


    public static void updateConfig(String[] args) {
        // Ensure 'Y' or 'N' value from command line arguments
        if (args.length < 1) {
            System.out.println("No confirmation argument provided. Please provide 'Y' or 'N'.");
            return;
        }

        String proceed = args[0];

        if (proceed.equalsIgnoreCase("Y")) {
            String filePath = getConfigFilePath();
            Properties properties = EmailConfigUtils.loadEmailProperties(filePath);

            Scanner scanner = new Scanner(System.in);
            boolean continueUpdating = true; // Flag to continue the update loop

            while (continueUpdating) {
                // Display all keys with numbers, excluding hidden keys
                Set<String> keys = properties.stringPropertyNames();
                int count = 1;
                System.out.println("Select the key you want to update:");

                for (String key : keys) {
                    // Skip the keys that should be hidden
                    if (!hiddenKeys.contains(key)) {
                        System.out.println(count + ". " + key);
                        count++;
                    }
                }

                int selectedIndex = -1;
                boolean validSelection = false;

                // Loop until a valid selection is made or the user chooses to exit
                while (!validSelection) {
                    System.out.print("Enter the number of the key you want to update (or type 'exit' to quit): ");
                    String input = scanner.nextLine();

                    // Check if the user wants to exit
                    if (input.equalsIgnoreCase("exit")) {
                        System.out.println("Exiting update process.");
                        return;  // Exit the method if user chooses to exit
                    }

                    // Try to parse the input to an integer
                    try {
                        selectedIndex = Integer.parseInt(input);

                        // Check if the selected index is valid
                        if (selectedIndex >= 1 && selectedIndex <= keys.size() - hiddenKeys.size()) {
                            validSelection = true; // Exit the loop if the selection is valid
                        } else {
                            System.out.println("Invalid selection. Please enter a valid number or type 'exit' to quit.");
                        }
                    } catch (NumberFormatException ex) {
                        System.out.println("Invalid input. Please enter a valid number or type 'exit' to quit.");
                    }
                }

                // Get the key corresponding to the selected index, excluding hidden ones
                String selectedKey = null;
                count = 1;
                for (String key : keys) {
                    if (!hiddenKeys.contains(key)) {
                        if (count == selectedIndex) {
                            selectedKey = key;
                            break;
                        }
                        count++;
                    }
                }

                // If valid selection, proceed
                if (selectedKey != null) {
                    System.out.println("You selected: " + selectedKey);

                    // Special instructions for "recipient.email"
                    if (selectedKey.equals("recipient.email")) {
                        System.out.println("NOTE: If you want to update with multiple email IDs, separate them with a comma (e.g., email1@example.com,email2@example.com).");
                    }

                    // Initialize new value
                    String newValue = "";

                    // Only validate email format for "sender.email" and "recipient.email"
                    if (selectedKey.equals("sender.email") || selectedKey.equals("recipient.email")) {
                        boolean emailValid = false;
                        while (!emailValid) {
                            System.out.print("Enter the new value for " + selectedKey + " or type 'exit' to quit: ");
                            newValue = scanner.nextLine();

                            // Check if the user wants to exit
                            if (newValue.equalsIgnoreCase("exit")) {
                                System.out.println("Exiting update process.");
                                return;  // Exit the method if user chooses to exit
                            }

                            // Check if the email is valid
                            if (newValue.trim().isEmpty()) {
                                System.out.println("Error: The input cannot be empty or just whitespace. Please enter a valid value.");
                            } else if (!isValidEmail(selectedKey, newValue)) {
                                System.out.println("Error: Please enter a valid email address.");
                            } else {
                                emailValid = true;  // Valid email entered, exit loop
                            }
                        }
                    } else {
                        // For non-email keys, ensure the input is not empty or just spaces
                        boolean validInput = false;
                        while (!validInput) {
                            System.out.print("Enter the new value for " + selectedKey + ": ");
                            newValue = scanner.nextLine();

                            // Check if the input is empty or just whitespace
                            if (newValue.trim().isEmpty()) {
                                System.out.println("Error: The value cannot be empty or just whitespace. Please enter a valid value.");
                            } else {
                                validInput = true;  // Valid value entered, exit loop
                            }
                        }
                    }
                    if(selectedKey.equalsIgnoreCase("sender.password")){
                        newValue=EmailConfigUtils.encodeBase64(newValue);
                        System.out.println("Password encrypted: "+newValue);
                    }
                    // Update the selected key with the new value
                    properties.setProperty(selectedKey, newValue);
                    System.out.println("Updated: " + selectedKey + " = " + newValue);

                    // Save the updated properties back to the file
                    try (FileOutputStream output = new FileOutputStream(filePath)) {
                        properties.store(output, null); // Save changes back to the properties file
                        System.out.println("Properties updated successfully.");
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }

                // Ask user if they want to update another key or exit
                System.out.print("Do you want to update another key? (Y/N): ");
                String userChoice = scanner.nextLine();

                if (!userChoice.equalsIgnoreCase("Y")) {
                    continueUpdating = false; // Exit the loop if the user chooses 'N'
                    System.out.println("Exiting update process.");
                }
            }

        } else {
            System.out.println("Update cancelled.");
        }
    }


    private static boolean isValidEmail(String key, String value) {
        // Email validation for sender.email and recipient.email
        if (key.equals("sender.email") || key.equals("recipient.email")) {
            // If it's recipient.email, we handle multiple emails
            if (key.equals("recipient.email")) {
                String[] emails = value.split(",");
                for (String email : emails) {
                    // Validate each email individually
                    if (!isValidSingleEmail(email.trim())) {
                        return false;  // If any email is invalid, return false
                    }
                }
                return true;  // All emails are valid
            } else {
                // For sender.email (single email validation)
                return isValidSingleEmail(value);
            }
        }
        return false;  // Default to false if it's not sender.email or recipient.email
    }

    private static boolean isValidSingleEmail(String email) {
        // Validate a single email address
        if (email.contains("@") && email.indexOf('@') == email.lastIndexOf('@')) {
            String domainPart = email.substring(email.indexOf('@') + 1); // Get the domain part
            if (domainPart.contains(".") && domainPart.indexOf('.') < domainPart.length() - 1) {
                // Ensure domain ends with a valid domain like .com, .org, etc.
                if (domainPart.matches("^[a-zA-Z0-9.-]+\\.(com|org|net|edu|gov|mil|int|co)$")) {
                    return true;
                }
            }
        }
        return false;
    }


    private static String getConfigFilePath() {
        String basePath = System.getProperty("user.home") + File.separator + "AppData" + File.separator + "Local" + File.separator + "WAT" + File.separator + "deploy" + File.separator + "config" + File.separator;
        String fileName = "emailConfig.properties";
        FileUtils.ensureDirectoryExists(basePath);
        return basePath + fileName;
    }
}
