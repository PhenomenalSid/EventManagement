package com.example.eventmanagement.service;

import com.example.eventmanagement.model.Event;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import static java.lang.System.*;

@Service
public class EmailService {

    @Value("${spring.mail.username}")
    private String username;

    @Autowired
    private JavaMailSender mailSender;

    public void sendRSVPConfirmation(String to, String eventName) {
        String subject = "RSVP Confirmation for " + eventName;
        String body = "Thank you for RSVPing to " + eventName + ". We look forward to seeing you!";
        sendSimpleEmail(to, subject, body);
    }

    public void sendEmailReminder(String userEmail, Event event) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(userEmail);
            helper.setSubject("Event Reminder: " + event.getName());
            helper.setText("Dear user,\n\nThis is a reminder for the upcoming event: " + event.getName() +
                    "\nLocation: " + event.getLocation() +
                    "\nDate & Time: " + event.getDate() +
                    "\n\nWe look forward to your attendance!\n\nBest Regards,\nEvent Management Team");

            mailSender.send(message);
        } catch (MessagingException e) {
            out.println("Failed to send email: " + e.getMessage());
        }
    }

    public void sendEventUpdateNotification(String to, String eventName) {
        String subject = "Update: Changes in " + eventName;
        String body = "There has been an update in the details of " + eventName + ". Pleases check the latest details in the event section.";
        sendSimpleEmail(to, subject, body);
    }

    public void sendEventCancellation(String to, String eventName) {
        String subject = "Cancellation Notice: " + eventName;
        String body = "We regret to inform you that the event " + eventName + " has been canceled.";
        sendSimpleEmail(to, subject, body);
    }

    private void sendSimpleEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        message.setFrom(username);
        mailSender.send(message);
    }
}
