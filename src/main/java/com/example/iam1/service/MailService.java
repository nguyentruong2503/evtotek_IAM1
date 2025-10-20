package com.example.iam1.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class MailService {
    @Autowired
    private JavaMailSender mailSender;

    public void sendOtp(String sendTo, String otp) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(sendTo);
        helper.setSubject("Mã OTP xác thực quên mật khẩu");
        helper.setText("Xin chào,\n\nMã OTP của bạn là: " + otp + "\nHiệu lực trong 2 phút.", false);
        mailSender.send(message);
    }

    public void sendEmailRegister(String sendTo) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(sendTo);
        helper.setSubject("Đăng ký tài khoản thành công");
        helper.setText("Xin chào,\n\nChúc mừng bạn đã đăng ký tài khoản thành công", false);
        mailSender.send(message);
    }
}
