package com.jh.wat.working;

import com.jh.wat.util.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;

public class EmailConfigManager_01 {
    /*public static void main(String[] args) {
        updateConfig(args);
    }*/
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
                // Display all keys with numbers
                Set<String> keys = properties.stringPropertyNames();
                int count = 1;
                System.out.println("Select the key you want to update:");
                for (String key : keys) {
                    System.out.println(count + ". " + key);
                    count++;
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
                        if (selectedIndex >= 1 && selectedIndex <= keys.size()) {
                            validSelection = true; // Exit the loop if the selection is valid
                        } else {
                            System.out.println("Invalid selection. Please enter a valid number or type 'exit' to quit.");
                        }
                    } catch (NumberFormatException ex) {
                        System.out.println("Invalid input. Please enter a valid number or type 'exit' to quit.");
                    }
                }

                // Get the key corresponding to the selected index
                String selectedKey = (String) keys.toArray()[selectedIndex - 1];
                System.out.println("You selected: " + selectedKey);

                // Prompt for the new value for the selected key
                System.out.print("Enter the new value for " + selectedKey + ": ");
                String newValue = scanner.nextLine();

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

    private static String getConfigFilePath() {
        String basePath = System.getProperty("user.home") + File.separator + "AppData" + File.separator + "Local" + File.separator + "WAT" + File.separator + "deploy" + File.separator + "config" + File.separator;
        String fileName = "emailConfig.properties";
        FileUtils.ensureDirectoryExists(basePath);
        return basePath + fileName;
    }
}
