package com.jh.wat.report;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class JsonUtils {

    public static JSONObject createJsonReport(String ipAddress,String systemName,String userName) {
        JSONObject jsonReport = new JSONObject();

        // Get the current date in the format "dd-MM-yyyy"
        String currentDate = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        // Add user name and date to the JSON
//        jsonReport.put("userName", userName);
        jsonReport.put("date", currentDate);
        // Add systemName and IP address to the JSON report
        jsonReport.put("systemName", systemName);  // Fetch system name
        jsonReport.put("ipaddress", ipAddress);  // Fetch IP address

        return jsonReport;
    }
}
