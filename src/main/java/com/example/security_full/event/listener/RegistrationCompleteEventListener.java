package com.example.security_full.event.listener;

import com.example.security_full.event.RegistrationCompleteEvent;
import com.example.security_full.user.IUserService;
import com.example.security_full.user.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class RegistrationCompleteEventListener implements ApplicationListener<RegistrationCompleteEvent> {

    private final IUserService iUserService;
    private final JavaMailSender mailSender;
    private User theUser;

    @Override
    public void onApplicationEvent(RegistrationCompleteEvent event) {
        theUser = event.getUser();
        String verificationToken = UUID.randomUUID().toString();

        iUserService.saveUserVerificationToken(theUser, verificationToken);

        String url = event.getApplicationUrl() + "/register/verifyYourEmail?token=" + verificationToken;
        try {
            sendVerificationEmail(url);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

        log.info("Click the link to verify your registration : {}", url);
    }

    public void sendVerificationEmail(String url) throws MessagingException {
        String subject = "Email verification";
        String sender = "User Registration Portal Service";
        String mailContent = "<p> Hi, " + theUser.getFistName() + ", <p>" +
                "<p> Thank your for registering with us, " +
                "Please, follow the link below to complete your registration. <p>" +
                "<a href=\"" + url + "\"> Verify you email to active your account </a>" +
                "<p> Thank you <br> User Registration Portal Service";
        MimeMessage message = mailSender.createMimeMessage();
        message.setContent(mailContent, "text/html; charset=utf-8");
        message.setFrom("ibf@gmail.com");
        message.setSubject(subject);
        var messageHelper = new MimeMailMessage(message);
        messageHelper.setTo(theUser.getEmail());
        mailSender.send(message);
    }
}
