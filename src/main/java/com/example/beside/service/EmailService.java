package com.example.beside.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.example.beside.common.Exception.EmailValidateException;
import com.example.beside.repository.MailRepository;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class EmailService {

    private final MailRepository mailRepository;

    private final JavaMailSender javaMailSender;

    @Autowired
    public EmailService(JavaMailSender javaMailSender, MailRepository mailRepository) {
        this.javaMailSender = javaMailSender;
        this.mailRepository = mailRepository;
    }

    @Transactional
    public long saveVerificationLog(String email, String verificationCode) {
        return mailRepository.saveEmailValidateCode(email, verificationCode);
    }

    public Boolean checkEmailValidate(String email, String verificationCode) throws EmailValidateException {
        return mailRepository.checkEmailValidateTime(email, verificationCode);
    }

    @Transactional
    public void sendVerificationEmail(String to, String verificationCode) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        String htmlMsg = " <div style='background-color: #F6F7FB; \n" +
                "                padding-top:80px;\n" +
                "                width:750px;\n" +
                "                height:400px;\n" +
                "                padding-left:60px;'> \n" +
                " <h1 style='color: #1B1D1F;\n" +
                "                    font-style: Pretendard; \n" +
                "                    font-size:40px;\n" +
                "                    line-height:22px;\n" +
                "                    padding-bottom: 19px; \n" +
                "                    font-weight: 700; '>\n" +
                " MOIM 서비스 이메일 인증</h1>\n" +
                " <div style='color: #626870; \n" +
                "                    font-style:Pretendard;\n" +
                "                    font-size:28px; \n" +
                "                    line-height:46px;\n" +
                "                    padding-bottom: 36px; \n" +
                "                    font-weight: 500;'>\n" +
                " 아래 인증번호를 MOIM 화면에 입력해주세요 </div>\n" +
                " <div style='border:1px solid white;\n" +
                "                    background-color: white;\n" +
                "                    display: flex; \n" +
                "                    flex-direction: column;\n" +
                "                    justify-content: center;\n" +
                "                    align-items: center; \n" +
                "                    font-size: 14px;\n" +
                "                    height: 191px; \n" +
                "                    width: 690px;'>\n" +
                " <div style='padding:54px 0;'>\n" +
                " <div style='color: #626870;\n" +
                "                            font-style: Pretendard; \n" +
                "                            font-weight: 500;\n" +
                "                            font-size:22px;\n" +
                "                            line-height:20px;\n" +
                "                            justify-content: center;\n" +
                "                            padding-bottom:15px;\n" +
                "                '>EMAIL 인증 코드 입니다</div>\n" +
                " <div>\n" +
                " <strong style='color: #8673FF;\n" +
                "                                font-style: Pretendard;     \n" +
                "                                font-size: 48px;\n" +
                "                                font-weight: 700;\n" +
                "                                line-height:48px;\n" +
                "                                '>\n" + verificationCode +
                " </strong>\n" +
                " </div>\n" +
                " </div>\n" +
                " </div>\n" +
                " </div>\n";

        helper.setFrom("admin@moim.life");
        helper.setTo(to);
        helper.setSubject("MOIM 서비스 이메일 인증");
        helper.setText(htmlMsg, true);

        // 검증 로그 기록
        saveVerificationLog(to, verificationCode);
        javaMailSender.send(message);
    }
}
