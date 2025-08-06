package com.survey.esa.LoginCredintials;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @SuppressWarnings("CallToPrintStackTrace")
    public void sendActivationEmail(String toEmail, String userName) {
        String subject = "Account Activated for Pulse Survey";
        String body = "<html>"
                + "<body style='font-family: Arial, sans-serif; margin: 0; padding: 0; color: #333;'>"
                + "<div style='background-color: #eaf2f4; padding: 20px;'>"
                + "<table style='max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 8px; box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);'>"
                + "<tr>"
                + "<td style='background-color: #4CAF50; padding: 30px 0; text-align: center;'>"
                + "<h1 style='color: white; font-size: 36px; margin: 0;'>Pulse Survey</h1>"
                + "<p style='color: white; font-size: 18px; margin: 0;'>Welcome aboard!</p>"
                + "</td>"
                + "</tr>"
                + "<tr>"
                + "<td style='padding: 40px 20px; font-size: 16px; color: #555;'>"
                + "<p style='font-size: 18px; color: #333;'>Dear " + userName + ",</p>"
                + "<p>We are excited to inform you that your account for Pulse Survey has been successfully <strong>activated</strong>.</p>"
                + "<p style='font-size: 16px;'>You can now log in and begin using the application to participate in various surveys. We value your feedback!</p>"
                + "<div style='margin: 20px 0; text-align: center;'>"
                + "<a href='http://172.20.10.4:5174/login' style='background-color: #4CAF50; color: white; padding: 12px 25px; text-decoration: none; font-size: 16px; border-radius: 5px;'>Login to Pulse Survey</a>"
                + "</div>"
                + "<p>If you have any issues logging in, please reach out to your admin.</p>"
                + "<p>Thank you for being part of Pulse Survey. We look forward to your valuable input!</p>"
                + "</td>"
                + "</tr>"
                + "<tr>"
                + "<td style='background-color: #f4f4f9; text-align: center; padding: 20px;'>"
                + "<p style='font-size: 14px; color: #777;'>The Pulse Team</p>"
                + "<p style='font-size: 12px; color: #777;'>If you did not request an account, please disregard this email.</p>"
                + "</td>"
                + "</tr>"

                + "</table>"
                + "</div>"
                + "</body>"
                + "</html>";
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
            messageHelper.setFrom("your_survey_email@example.com");
            messageHelper.setTo(toEmail);
            messageHelper.setSubject(subject);
            messageHelper.setText(body, true);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public void sendDeclineEmail(String toEmail, String userName) {
        String subject = "Account Declined for Pulse Survey";
        String body = "<html>"
                + "<body style='font-family: Arial, sans-serif; margin: 0; padding: 0; color: #333;'>"
                + "<div style='background-color: #f4f4f9; padding: 20px;'>"
                + "<table style='max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 8px; box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);'>"
                + "<tr>"
                + "<td style='background-color: #FF5733; padding: 30px 0; text-align: center;'>"
                + "<h1 style='color: white; font-size: 36px; margin: 0;'>Pulse Survey</h1>"
                + "<p style='color: white; font-size: 18px; margin: 0;'>We're sorry to inform you</p>"
                + "</td>"
                + "</tr>"
                + "<tr>"
                + "<td style='padding: 40px 20px; font-size: 16px; color: #555;'>"
                + "<p style='font-size: 18px; color: #333;'>Dear " + userName + ",</p>"
                + "<p>We regret to inform you that your account for Pulse Survey has been <strong>declined</strong>.</p>"
                + "<p style='font-size: 16px;'>If you have any questions or concerns, please do not hesitate to contact the admin for further assistance.</p>"
                + "<p>We value your interest in Pulse Survey and would be happy to assist you in the future.</p>"
                + "<p>If you have any further questions, please reach out to your Admin.</p>"
                + "<p>Thank you for your understanding.</p>"
                + "</td>"
                + "</tr>"
                + "<tr>"
                + "<td style='background-color: #f4f4f9; text-align: center; padding: 20px;'>"
                + "<p style='font-size: 14px; color: #777;'>The Pulse Team</p>"
                + "<p style='font-size: 12px; color: #777;'>If you believe this was a mistake, please contact your admin for further assistance.</p>"
                + "</td>"
                + "</tr>"

                + "</table>"
                + "</div>"
                + "</body>"
                + "</html>";
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
            messageHelper.setFrom("your_survey_email@example.com");
            messageHelper.setTo(toEmail);
            messageHelper.setSubject(subject);
            messageHelper.setText(body, true);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

}
