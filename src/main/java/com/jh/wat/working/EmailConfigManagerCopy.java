package com.jh.wat.working;

import com.jh.wat.util.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class EmailConfigManagerCopy {

    public static void updateConfig(String[] args) {
        // Create a scanner to read user input from the console
//        Scanner scanner = new Scanner(System.in);

        // Define the properties file location
//        String filePath = System.getProperty("user.home") + File.separator + "Documents" + File.separator + "WAT" + File.separator + "emailConfig.properties";
        String filePath = getConfigFilePath();

        // Load properties using EmailConfigUtils, which will create defaults if missing
        Properties properties = EmailConfigUtils.loadEmailProperties(filePath);

        // Ask the user to input key-value pairs to update
        /*while (true) {
            System.out.print("Enter key=value (or type 'exit' to stop): ");
            String input = scanner.nextLine();

            // If the user types 'exit', stop the loop
            if (input.equalsIgnoreCase("exit")) {
                break;
            }*/

            // Process the input to ensure it's in key=value format
        // Process command-line arguments to update properties
        for (int i = 0; i < args.length; i++) {
            // Command-line argument format: key=value
            String[] keyValue = args[i].split("=");
            if (keyValue.length == 2) {
                String key = keyValue[0];
                String value = keyValue[1];
                // Update the properties with the new values
                properties.setProperty(key, value);
                System.out.println("Updated: " + key + " = " + value);
            } else {
                System.out.println("Invalid argument format. Please use key=value.");
            }
        }

        // Save the updated properties back to the file
        try (FileOutputStream output = new FileOutputStream(filePath)) {
            properties.store(output, null); // Save changes back to the properties file
            System.out.println("Properties updated successfully.");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    private static String getConfigFilePath() {
        String bastPath= System.getProperty("user.home") + File.separator + "AppData" + File.separator + "Local" + File.separator + "WAT" + File.separator+ "deploy"+ File.separator + "config" + File.separator;
        String fileName = "emailConfig.properties";
        FileUtils.ensureDirectoryExists(bastPath);
        return bastPath+ fileName;
    }
}
