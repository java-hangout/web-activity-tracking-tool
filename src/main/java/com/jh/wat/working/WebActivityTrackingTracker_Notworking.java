package com.jh.wat.working;

import com.jh.wat.util.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WebActivityTrackingTracker_Notworking {

    private static final String BASE_PATH = getBasePathForSharedDrive();

    public static void main(String[] args) {
        String systemName = SystemUtils.getSystemName();
        String ipAddress = NetworkUtils.getSystemIpAddress();
        List<String> loggedInUsers = SystemUtils.getLoggedInUsers();
        if (!loggedInUsers.isEmpty()) {
            loggedInUsers.forEach(userName -> {
                System.out.println("Processing user: " + userName);
                generateVisitedSitesTimeTrackerRecord(ipAddress, systemName, userName);
            });
        }
    }

    private static void generateVisitedSitesTimeTrackerRecord(String ipAddress, String systemName, String userName) {
        String onlyUserName = extractUserName(userName);
        String timestamp = new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date());
        String folderCurrentDate = new SimpleDateFormat("ddMMMyyyy").format(new Date());

        Path path = Paths.get(BASE_PATH, folderCurrentDate);
        ensureDirectoryExists(path);

        String jsonFileName = path.resolve(systemName + "_" + timestamp + ".json").toString();
        String csvFileName = path.resolve(systemName + "_" + timestamp + ".csv").toString();

        // Generate JSON Report
        JSONObject jsonReport = createJsonReport(ipAddress, systemName, userName, onlyUserName);

        // Write the JSON and CSV files
        FileUtils.writeJsonToFile(jsonFileName, jsonReport);
        List<String[]> csvData = generateCsvData(ipAddress, systemName, onlyUserName);
        FileUtils.writeCsvToFile(csvFileName, csvData);

        // Optional: Send the report via email (uncomment as needed)
        GmailUtils.sendGmailWithAttachment(csvFileName, systemName);
    }

    private static String extractUserName(String userName) {
        return userName.contains("\\") ? userName.split("\\\\")[1] : userName;
    }

    private static JSONObject createJsonReport(String ipAddress, String systemName, String userName, String onlyUserName) {
        JSONObject jsonReport = JsonUtils.createJsonReport(ipAddress, systemName, userName);
        JSONArray usersArray = new JSONArray();
        JSONObject userObject = new JSONObject();
        userObject.put("userName", onlyUserName);
        usersArray.put(userObject);
        jsonReport.put("Users", usersArray);
        return jsonReport;
    }

    private static List<String[]> generateCsvData(String ipAddress, String systemName, String onlyUserName) {
        List<String[]> csvData = new ArrayList<>();
        csvData.add(new String[]{"IP Address", "System Name", "User Name", "Browser Name", "Title", "URL", "Visited Time", "Total Time Spent (m)"});

        List<String> installedBrowsers = BrowserUtils.getInstalledBrowsers();
        installedBrowsers.forEach(browser -> {
            JSONArray visitedSitesArray = BrowserUtils.getVisitedSitesForBrowser(browser, onlyUserName);
            visitedSitesArray.forEach(site -> {
                JSONObject siteJson = (JSONObject) site;
                String[] csvRow = new String[]{
                        ipAddress,
                        systemName,
                        onlyUserName,
                        browser,
                        siteJson.optString("title"),
                        siteJson.optString("url"),
                        siteJson.optString("visitedTime"),
                        String.valueOf(siteJson.optDouble("totalTimeSpentInMinutes"))
                };
                csvData.add(csvRow);
            });
        });
        return csvData;
    }

    private static void ensureDirectoryExists(Path path) {
        try {
            if (Files.notExists(path)) {
                Files.createDirectories(path);
            }
        } catch (IOException e) {
            System.err.println("Error creating directories: " + e.getMessage());
        }
    }

    private static String getBasePathForSharedDrive() {
        // Fully qualified UNC path to shared drive (adjust as necessary)
        String sharedDrivePath = "\\\\ServerName\\SharedDrive\\Reports\\WebActivityTrackingTool\\";

        // Ensure the shared drive is reachable
        Path sharedDrive = Paths.get(sharedDrivePath);
        if (!Files.exists(sharedDrive)) {
            // Log that the shared drive is not available and fallback to local path
            System.err.println("Error: Shared drive path not accessible. Falling back to default path.");
            return "C:\\Program Files\\Java\\jdk-21\\bin\\WebActivityTrackingTool\\";  // Local fallback path
        }
        System.out.println("Shared drive path is accessible: " + sharedDrivePath);
        return sharedDrivePath;
    }
}
