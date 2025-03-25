package com.sarva.distributed.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EmailService {
    
    @Autowired
    private JavaMailSender mailSender;

    public void sendMatchEmailToUser(String to, List<String> matchDetails) {
        String subject = "Your Personalized Sports Matches";

        // Check if matchDetails is empty
        String body;
        if (matchDetails == null || matchDetails.isEmpty()) {
            body = "No matches are scheduled for the selected date range.";
        } else {
            body = "Here are the latest matches for your favorite sports:\n\n" + String.join("\n", matchDetails);
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
        System.out.println("Email sent to: " + to);
    }
}
