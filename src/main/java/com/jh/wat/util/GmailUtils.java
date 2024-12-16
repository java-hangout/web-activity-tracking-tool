package com.jh.wat.util;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.util.Properties;
import com.jh.wat.config.EmailConfigUtils;

public class GmailUtils {

    public static void sendGmailWithAttachment(String filePath, String systemName) {
        System.out.println("systemName  ---> " + systemName);
        System.out.println("filePath  ---> " + filePath);

        // Check if the file exists and has content
        File file = new File(filePath);
        if (!file.exists() || file.length() == 0) {
            System.out.println("Error: The attachment file is either missing or empty.");
            return;
        }

        // Load SMTP settings from the properties file
        Properties properties = EmailConfigUtils.loadEmailProperties(getConfigFilePath());

        String senderGmailId = properties.getProperty("sender.email");
        System.out.println("senderGmailId  ---> " + senderGmailId);
        String senderGmailPW = properties.getProperty("sender.password");
        String recipientEmails = properties.getProperty("recipient.email"); // Comma-separated list of emails
        System.out.println("recipientEmails  ---> " + recipientEmails);
        String subjectPrefix = properties.getProperty("subject.prefix");
        String emailBodyTemplate = properties.getProperty("body.text");

        // Set up the mail server properties
        String gmailHost = properties.getProperty("smtp.host");
        properties.setProperty("mail.smtp.host", gmailHost);
        System.out.println("gmailHost  ---> " + gmailHost);
        String gmailPort = properties.getProperty("smtp.port");
        properties.setProperty("mail.smtp.port", gmailPort);
        System.out.println("gmailPort  ---> " + gmailPort);
        properties.setProperty("mail.smtp.starttls.enable", properties.getProperty("smtp.starttls.enable"));
        properties.setProperty("mail.smtp.auth", properties.getProperty("smtp.auth"));
        // Decode the password from Base64
        senderGmailPW = EmailConfigUtils.decodeBase64(senderGmailPW);
        // Authenticate the sender's email
        String finalSenderGmailPW = senderGmailPW;
        Session session = Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderGmailId, finalSenderGmailPW);
            }
        });

        try {
            // Create the email content
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderGmailId));

            // Split the comma-separated list of recipient emails and add each one
            String[] recipientList = recipientEmails.split(",");
            for (String recipientEmail : recipientList) {
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail.trim()));
            }

            // Set the email subject
            message.setSubject(subjectPrefix + " " + systemName);

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
            attachmentBodyPart.setFileName("Web_activity_tracker_report_" + systemName + ".csv");

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
