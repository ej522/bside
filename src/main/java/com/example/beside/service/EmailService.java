package com.example.beside.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private final JavaMailSender javaMailSender;

    @Autowired
    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendVerificationEmail(String to, String verificationCode) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        String htmlMsg = "<html><body>" +
                "<h1>MOIM 서비스 이메일 인증</h1> <br/>" +
                "<p>아래 인증번호를 MOIM 화면에 입력해주세요.</p> <br/>" +
                "<div align='center' style='border:1px solid black; width:400px; font-family:verdana'>" +
                "<h3 style='color:blue;'>Email 인증 코드입니다.</h3>" +
                "<div style='font-size:130%'>CODE : <strong>" + verificationCode + "</strong></div>" +
                "</div></body></html>";

        helper.setFrom("admin@moim.life");
        helper.setTo(to);
        helper.setSubject("MOIM 서비스 이메일 인증");
        helper.setText(htmlMsg, true);

        javaMailSender.send(message);
    }
}
