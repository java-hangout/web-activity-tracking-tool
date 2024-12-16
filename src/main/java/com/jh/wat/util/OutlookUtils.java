package com.jh.wat.util;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.util.Base64;
import java.util.Properties;
import com.jh.wat.config.EmailConfigUtils;

public class OutlookUtils {

    public static void sendOutlookWithAttachment(String filePath, String userName) {
        System.out.println("filePath  ---> " + filePath);

        // Check if the file exists and has content
        File file = new File(filePath);
        if (!file.exists() || file.length() == 0) {
            System.out.println("Error: The attachment file is either missing or empty.");
            return;
        }

        // Load SMTP settings and email details from the properties file
        Properties properties = EmailConfigUtils.loadEmailProperties(getConfigFilePath());

        String senderOutlookId = properties.getProperty("sender.email");
        String senderOutlookPW = properties.getProperty("sender.password");
        String recipientEmails = properties.getProperty("recipient.email"); // Comma-separated list
        String subjectPrefix = properties.getProperty("subject.prefix");
        String emailBodyTemplate = properties.getProperty("body.text");

        // Decode the password from Base64
        senderOutlookPW = EmailConfigUtils.decodeBase64(senderOutlookPW);

        // Set up the mail server properties for Outlook SMTP
        properties.setProperty("mail.smtp.host", properties.getProperty("smtp.host"));
        properties.setProperty("mail.smtp.port", properties.getProperty("smtp.port"));
        properties.setProperty("mail.smtp.starttls.enable", properties.getProperty("smtp.starttls.enable"));
        properties.setProperty("mail.smtp.auth", properties.getProperty("smtp.auth"));

        // Create a session with authentication
        String finalSenderOutlookPW = senderOutlookPW;
        Session session = Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderOutlookId, finalSenderOutlookPW);
            }
        });

        try {
            // Create the email content
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderOutlookId));

            // Split the comma-separated list of recipient emails and add each one
            String[] recipientList = recipientEmails.split(",");
            for (String recipientEmail : recipientList) {
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail.trim()));
            }

            // Set the email subject
            message.setSubject(subjectPrefix + " for " + userName);

            // Determine the email prefix based on the number of recipients
            String emailPrefix = recipientList.length > 1 ? "All" : recipientList[0].split("@")[0];

            // Create the message body part
            BodyPart messageBodyPart = new MimeBodyPart();
            String emailBody = emailBodyTemplate.replace("{recipient}", emailPrefix);
            messageBodyPart.setText(emailBody);

            // Create the attachment part
            MimeBodyPart attachmentBodyPart = new MimeBodyPart();
            DataSource source = new FileDataSource(filePath);
            attachmentBodyPart.setDataHandler(new DataHandler(source));
            attachmentBodyPart.setFileName("web_activity_tracker_report_" + userName + ".csv");

            // Combine the body and the attachment
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);
            multipart.addBodyPart(attachmentBodyPart);

            // Set the content of the message
            message.setContent(multipart);

            // Send the email
            Transport.send(message);
            System.out.println("Email sent successfully!");

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private static String getConfigFilePath() {
        String basePath = System.getProperty("user.home") + File.separator + "AppData" + File.separator + "Local" + File.separator + "WAT" + File.separator + "deploy" + File.separator + "config" + File.separator;
        String fileName = "emailConfig.properties";
        FileUtils.ensureDirectoryExists(basePath);
        return basePath + fileName;
    }
}
