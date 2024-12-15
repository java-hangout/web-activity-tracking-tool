package com.jh.wat.report;

import com.jh.wat.config.EmailConfigManager;
import com.jh.wat.util.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WebActivityTrackingTracker {

    // Base path where the reports will be stored (in a shared drive)
    private static final String BASE_PATH = getBasePathForSharedDrive();
    public static void main(String[] args) {
        // Check if there are enough arguments
        if (args.length == 0) {
            System.out.println("Launching Web Activity Tracking Tool ..!!");
            generateReport();
        } else {
            System.out.println("Updating Web Activity Tracking Tool Config ..!!");
            EmailConfigManager.updateConfig(args);
        }
    }

    private static void generateReport() {
        String systemName = SystemUtils.getSystemName();
        String ipAddress = NetworkUtils.getSystemIpAddress();
        List<String> loggedInUsers = SystemUtils.getLoggedInUsers();
        if (!loggedInUsers.isEmpty()) {
            // For each logged-in user, process and generate their report
            loggedInUsers.forEach(userName -> {
                System.out.println("Processing user: " + userName);
                generateVisitedSitesTimeTrackerRecord(ipAddress, systemName, userName);
            });
        }
    }

    private static void generateVisitedSitesTimeTrackerRecord(String ipAddress, String systemName, String userName) {
        // Extract the actual username if domain is included (e.g., "DESKTOP-TCU61U2\\userName")
        String onlyUserName = userName.contains("\\") ? userName.split("\\\\")[1] : userName;

        // Generate a timestamp for the report file name
        String timestamp = new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date());
        String folderCurrentDate = new SimpleDateFormat("ddMMMyyyy").format(new Date());

        // Define the directory path to save the report
        String path = BASE_PATH + folderCurrentDate + File.separator;
        String jsonFileName = path + systemName + "_" + timestamp + ".json";
        String csvFileName = path + systemName + "_" + timestamp + ".csv";

        // Ensure the directory exists, or create it
        FileUtils.ensureDirectoryExists(path);

        // Create a JSON report with the user's basic details
        JSONObject jsonReport = JsonUtils.createJsonReport(ipAddress, systemName, userName);
        JSONArray usersArray = new JSONArray();
        JSONObject userObject = new JSONObject();
        userObject.put("userName", onlyUserName); // Add the user name without domain
        usersArray.put(userObject);
        jsonReport.put("Users", usersArray);

        // Get the list of installed browsers
        List<String> installedBrowsers = BrowserUtils.getInstalledBrowsers();
        JSONArray browsersArray = new JSONArray();
        List<String[]> csvData = new ArrayList<>();

        // Add CSV header
        csvData.add(new String[]{"System Name", "IP Address", "User Name", "Browser Name", "Title", "URL", "Visited Time", "Total Time Spent (m)"});

        // For each installed browser, gather visited sites data
        installedBrowsers.forEach(browser -> {
            JSONObject browserData = new JSONObject();
            browserData.put("browserName", browser);

            // Get visited sites for the current browser and user
            JSONArray visitedSitesArray = BrowserUtils.getVisitedSitesForBrowser(browser, userName);

            visitedSitesArray.forEach(site -> {
                JSONObject siteJson = (JSONObject) site;
                String[] csvRow = new String[]{
                        systemName,
                        ipAddress,
                        onlyUserName,  // Add the user name to the CSV
                        browser,
                        siteJson.optString("title"),
                        siteJson.optString("url"),
                        siteJson.optString("visitedTime"),
                        String.valueOf(siteJson.optDouble("totalTimeSpentInMinutes"))
                };
                csvData.add(csvRow);
            });

            browserData.put("visitedSites", visitedSitesArray);
            browsersArray.put(browserData);
        });

        // Add the browsers' data to the user's JSON inside the Users array
        userObject.put("browsers", browsersArray);

        // Write the JSON report to the specified file
        FileUtils.writeJsonToFile(jsonFileName, jsonReport);
        FileUtils.writeCsvToFile(csvFileName, csvData);

        // Send the report via email (commented out the Gmail function, using Outlook instead)
        GmailUtils.sendGmailWithAttachment(csvFileName, systemName);
//        OutlookUtils.sendOutlookWithAttachment(csvFileName, NetworkUtils.getSystemName());
    }

    /**
     * This method retrieves the base path for saving the reports to a shared drive.
     * For example: "\\\\SharedDrive\\Reports\\"
     *
     * @return The base path for saving reports on the shared drive.
     */
    private static String getBasePathForSharedDrive() {
        // Modify this path to point to your shared drive
//        String sharedDrivePath = "\\\\SharedDrive\\Reports\\WebActivityTrackingTool\\";
        
        // Check if the shared drive is accessible
//        File sharedDrive = new File(sharedDrivePath);
//        if (!sharedDrive.exists()) {
//            System.err.println("Error: Shared drive path not accessible: " + sharedDrivePath);
            return System.getProperty("user.home") + File.separator + "Documents" + File.separator + "WebActivityTrackingTool" + File.separator;  // fallback
//        }

//        return sharedDrivePath;
    }
}
