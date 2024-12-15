package com.jh.wat.working;

import com.jh.wat.util.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This class generates a time tracker report for visited websites from multiple browsers for each logged-in user.
 * It creates a JSON report that contains information about the user, their IP address, and their visited sites data.
 */
public class WebActivityTrackingTracker_01_Working {

    // Base path where the reports will be stored, dynamically retrieved for the current system
    private static final String BASE_PATH = getBasePathForSystem();

    /**
     * Main entry point of the program that processes all logged-in users to generate visited sites time tracker reports.
     * It retrieves the list of logged-in users and calls the method to generate the report for each user.
     *
     * @param args Command-line arguments (not used in this case).
     */
    public static void main(String[] args) {
        List<String> loggedInUsers = SystemUtils.getLoggedInUsers();
        if (!loggedInUsers.isEmpty()) {
            // For each logged-in user, process and generate their report
            loggedInUsers.forEach(userName -> {
                System.out.println("Processing user: " + userName);
                generateVisitedSitesTimeTrackerRecord(userName);
            });
        }
    }

    /**
     * This method generates the visited sites time tracker record for a given user.
     * It creates a JSON report containing information about the user's visited sites from various browsers.
     * The report is stored in the user's Documents directory under a folder named 'BrowserHistoryReports'.
     *
     * @param userName The username for which the visited sites time tracker report will be generated.
     */
    private static void generateVisitedSitesTimeTrackerRecord(String userName) {
        // Extract the actual username if domain is included (e.g., "domain\\userName")
        String onlyUserName = userName.contains("\\") ? userName.split("\\\\")[1] : userName;
        // Generate a timestamp for the report file name
        String timestamp = new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date());
        String folderCurrentDate = new SimpleDateFormat("ddMMMyyyy").format(new Date());
        // Define the directory path to save the report
        String path = BASE_PATH + folderCurrentDate + File.separator;
        String jsonFileName = path + onlyUserName + "_" + timestamp + ".json";
        String csvFileName = path + onlyUserName + "_" + timestamp + ".csv";

        // Ensure the directory exists, or create it
        FileUtils.ensureDirectoryExists(path);

        // Create a JSON report with the user's basic details
        JSONObject jsonReport = JsonUtils_01_Working.createJsonReport(userName);
        jsonReport.put("ipaddress", NetworkUtils.getSystemIpAddress());

        // Get the list of installed browsers
        List<String> installedBrowsers = BrowserUtils.getInstalledBrowsers();
        JSONArray browsersArray = new JSONArray();
        List<String[]> csvData = new ArrayList<>();

        // Add CSV header
        csvData.add(new String[]{"Browser Name", "Title", "URL", "Visited Time", "Total Time Spent (m)"});

        // For each installed browser, gather visited sites data
        installedBrowsers.forEach(browser -> {
            JSONObject browserData = new JSONObject();
            browserData.put("browserName", browser);
            JSONArray visitedSitesArray = BrowserUtils.getVisitedSitesForBrowser(browser, userName);

            visitedSitesArray.forEach(site -> {
                JSONObject siteJson = (JSONObject) site;
                String[] csvRow = new String[]{
                        browser,
                        siteJson.optString("title"),
                        siteJson.optString("url"),
                        siteJson.optString("visitedTime"),
//                        String.valueOf(siteJson.optDouble("totalTimeSpentInSeconds")),
                        String.valueOf(siteJson.optDouble("totalTimeSpentInMinutes"))
                };
                csvData.add(csvRow);
            });

            browserData.put("visitedSites", visitedSitesArray);
            browsersArray.put(browserData);
        });

        // Add the browsers' data to the JSON report
        jsonReport.put("browsers", browsersArray);

        // Write the JSON report to the specified file
        FileUtils.writeJsonToFile(jsonFileName, jsonReport);
        FileUtils.writeCsvToFile(csvFileName, csvData);

        // Send the report via email (commented out the Gmail function, using Outlook instead)
        GmailUtils.sendGmailWithAttachment(csvFileName, userName);
//        OutlookUtils.sendOutlookWithAttachment(jsonFileName, userName);
    }

    /**
     * This method retrieves the base path for saving the reports based on the user's home directory.
     * It constructs the path: user.home/Documents/BrowserHistoryReports/
     *
     * @return The base path for saving reports.
     */
    private static String getBasePathForSystem() {
        return System.getProperty("user.home") + File.separator + "Documents" + File.separator + "WebActivityTrackingTool" + File.separator;
    }
}
