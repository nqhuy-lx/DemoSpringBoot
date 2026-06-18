package com.hnq.service;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine springTemplateEngine;

    @Value("${spring.mail.from}")
    private String emailFrom;

//    @Value("${spring.application.serverName}")
//    private String serverName;

    public String sendEmail(String to, String subject, String text, MultipartFile[] files) throws MessagingException, UnsupportedEncodingException {
        log.info("Sending email to {}, subject {}, text {}", to, subject, text);
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        helper.setFrom(emailFrom, "HuyNguyen");

        if (to.contains(",")) { // send to multiple users
            helper.setTo(InternetAddress.parse(to));
        } else { // send to single user
            helper.setTo(to);
        }

        if(files != null) {
            for (MultipartFile file : files) {
                helper.addAttachment(Objects.requireNonNull(file.getOriginalFilename()), file);
            }
        }

        helper.setSubject(subject);
        helper.setText(text, true);
        mailSender.send(mimeMessage);
        log.info("Email send successfully, to{}", to);

        return "sent";
    }

    public void sendConfirmLink(String email, Long id, String secretCode) throws MessagingException, UnsupportedEncodingException {
        log.info("Sending confirm link to {}, with id {}", email, id);
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());
        Context context = new Context();
        String linkConfirm = String.format("http://localhost:8080/users/confirm/%s?secretCode=%s", id, secretCode);
        Map<String,Object> properties = new HashMap<>();
        properties.put("linkConfirm", linkConfirm);
        context.setVariables(properties);

        helper.setFrom(emailFrom, "HuyNguyen");
        helper.setTo(email);
        helper.setSubject("Please confirm your email");

        String html = springTemplateEngine.process("confirm-email.html", context);
        helper.setText(html, true);
        mailSender.send(mimeMessage);
        log.info("Email confirm send successfully, to {}", email);
    }
}
