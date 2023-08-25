package com.teaming.TeamingServer.Service;

import com.teaming.TeamingServer.Exception.BadRequestException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import java.io.UnsupportedEncodingException;
import java.util.Random;

@PropertySource("classpath:application.properties")
@Slf4j
@RequiredArgsConstructor
@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String id;

    private MimeMessage createValidateEmailRequestMessage(String to, String verificationCode) throws MessagingException, UnsupportedEncodingException {
        log.info("보내는 대상 : "+ to);
        log.info("인증 번호 : " + verificationCode);
        MimeMessage message = javaMailSender.createMimeMessage();

        message.addRecipients(MimeMessage.RecipientType.TO, to);
        message.setSubject("Teaming 회원가입 인증 코드 메일");

        // 메일 내용 메일의 subtype 을 html 로 지정하여 html 문법 사용 가능
        String msg="";
        msg += "<h1 style=\"font-size: 30px; padding-right: 30px; padding-left: 30px;\">이메일 인증</h1>";
        msg += "<p></p>";
        msg += "<p style=\"font-size: 17px; padding-right: 30px; padding-left: 30px;\">아래 확인 코드를 회원가입 화면에서 입력해주세요.</p>";
        msg += "<div style=\"padding-right: 30px; padding-left: 30px; margin: 32px 0 40px;\"><table style=\"border-collapse: collapse; border: 0; background-color: #F4F4F4; height: 70px; table-layout: fixed; word-wrap: break-word; border-radius: 6px;\"><tbody><tr><td style=\"text-align: center; vertical-align: middle; font-size: 30px;\">";
        msg += verificationCode;
        msg += "</td></tr></tbody></table></div>";

        message.setText(msg, "utf-8", "html"); // 내용, charset 타입, subtype
        message.setFrom(new InternetAddress(id,"Teaming")); // 보내는 사람의 메일 주소, 보내는 사람 이름

        return message;
    }

    private MimeMessage createResetPasswordMessage(String to, String newPassword) throws MessagingException, UnsupportedEncodingException {
        log.info("보내는 대상 : "+ to);
        log.info("새로운 비밀번호 : " + newPassword);

        MimeMessage message = javaMailSender.createMimeMessage();

        message.addRecipients(MimeMessage.RecipientType.TO, to); // to 보내는 대상
        message.setSubject("Teaming 비밀번호 초기화 메일"); //메일 제목

        String msg="";
        msg += "<h1 style=\"font-size: 30px; padding-right: 30px; padding-left: 30px;\">초기화된 비밀번호</h1>";
        msg += "<p></p>";
        msg += "<p style=\"font-size: 17px; padding-right: 30px; padding-left: 30px;\">아래 비밀번호를 로그인 시에 비밀번호로 사용해주세요.</p>";
        msg += "<p style=\"font-size: 17px; padding-right: 30px; padding-left: 30px;\">로그인 후에는 보안을 위해 꼭 비밀번호 변경을 해주세요.</p>";
        msg += "<div style=\"padding-right: 30px; padding-left: 30px; margin: 32px 0 40px;\"><table style=\"border-collapse: collapse; border: 0; background-color: #F4F4F4; height: 70px; table-layout: fixed; word-wrap: break-word; border-radius: 6px;\"><tbody><tr><td style=\"text-align: center; vertical-align: middle; font-size: 30px;\">";
        msg += newPassword;
        msg += "</td></tr></tbody></table></div>";

        message.setText(msg, "utf-8", "html");
        message.setFrom(new InternetAddress(id,"Teaming"));

        return message;
    }

    @Override
    public void sendValidateEmailRequestMessage(String to, String verificationCode) {
        try{
            javaMailSender.send(createValidateEmailRequestMessage(to, verificationCode)); // 메일 발송
        } catch(Exception e){
            e.printStackTrace();
            throw new BadRequestException("이메일 발송에 실패했습니다.");
        }
    }

    @Override
    public void sendResetPasswordMessage(String to, String newPassword) throws Exception {
        try {
            javaMailSender.send(createResetPasswordMessage(to, newPassword)); // 메일 발송
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("이메일 발송에 실패했습니다.");
        }
    }
}