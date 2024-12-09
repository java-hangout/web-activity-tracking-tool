package com.jh.wat.config;

import java.io.*;
import java.util.Properties;

public class EmailConfigUtils {

    /**
     * Loads the email configuration properties. If the properties file does not exist,
     * it creates the file with default values.
     *
     * @param filePath The path where the properties file is stored.
     * @return A Properties object containing email configurations.
     */
    public static Properties loadEmailProperties(String filePath) {
        Properties properties = new Properties();
        File file = new File(filePath);

        // Check if the properties file exists
        if (!file.exists()) {
            // If the file doesn't exist, create it with default values
            System.out.println("Properties file not found. Creating new file with default values.");
            createDefaultConfigFile(file);
        }

        // Load properties from the file
        try (FileInputStream inputStream = new FileInputStream(file)) {
            properties.load(inputStream);
        } catch (IOException e) {
            System.out.println("Error loading email properties: " + e.getMessage());
            e.printStackTrace();
        }

        return properties;
    }

    /**
     * Creates the properties file with default values if it doesn't exist.
     *
     * @param file The properties file to create.
     */
    private static void createDefaultConfigFile(File file) {
        try {
            Properties defaultProperties = getProperties();

            // Create the file and write default properties
            if (file.createNewFile()) {
                try (FileOutputStream outputStream = new FileOutputStream(file)) {
                    defaultProperties.store(outputStream, "Default email config properties");
                    System.out.println("Default properties have been written to the file.");
                }
            }
        } catch (IOException e) {
            System.out.println("Error creating the default properties file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static Properties getProperties() {
        Properties defaultProperties = new Properties();
        defaultProperties.setProperty("sender.email", "contacts.veereshn@gmail.com");
        defaultProperties.setProperty("sender.password", "wkgs wpis zykx zauw");
        defaultProperties.setProperty("recipient.email", "contacts.veeresh@gmail.com,contacts.veeresh@gmail.com");
        defaultProperties.setProperty("subject.prefix", "Report - ");
        defaultProperties.setProperty("body.text", "Dear {recipient},\n\nPlease find the attached report.");
        defaultProperties.setProperty("smtp.host", "smtp.gmail.com");
        defaultProperties.setProperty("smtp.port", "587");
        defaultProperties.setProperty("smtp.starttls.enable", "true");
        defaultProperties.setProperty("smtp.auth", "true");
        return defaultProperties;
    }
}
