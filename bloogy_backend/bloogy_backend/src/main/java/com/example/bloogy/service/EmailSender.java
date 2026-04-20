package com.example.bloogy.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Service class for sending emails.
 * Provides functionality to send simple text-based emails using Spring's JavaMailSender.
 */
@Service
public class EmailSender {

    private final JavaMailSender mailSender;

    /**
     * Constructor to initialize the JavaMailSender.
     *
     * @param mailSender the JavaMailSender used to send emails.
     */
    public EmailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Sends a simple email to a specified recipient.
     *
     * @param to the recipient's email address.
     * @param subject the subject of the email.
     * @param text the body text of the email.
     */
    public void sendEmail(String to, String subject, String text) {
        // Create a simple email message
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(to); // Set recipient
        mailMessage.setSubject(subject); // Set email subject
        mailMessage.setText(text); // Set email body text

        // Send the email
        mailSender.send(mailMessage);
    }
}
