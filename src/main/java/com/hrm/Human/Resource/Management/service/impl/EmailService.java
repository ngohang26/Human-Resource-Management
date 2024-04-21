package com.hrm.Human.Resource.Management.service.impl;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;


@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String content) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom("Tolo HR <ngohangvn01@gmail.com>");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true); // Tham số 'true' cho biết rằng nội dung là HTML.
            mailSender.send(mimeMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendNewPasswordEmail(String to, String newPassword) {
        String subject = "Your new password";
        String content = "Your new password is: " + newPassword + "\nPlease change it immediately after login.";
        sendEmail(to, subject, content);
    }
}
