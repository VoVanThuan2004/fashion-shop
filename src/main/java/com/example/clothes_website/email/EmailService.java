package com.example.clothes_website.email;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void send(String toEmail, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(body);
            javaMailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace(); // hoặc log lỗi
        }
    }

    /**
     * Gửi email + otp khi đăng ký tài khoản
     */
    public void sendOTPEmailCustomerAccount(String toEmail, String otp) {
        String subject = "Mã OTP để đăng ký tài khoản";
        String content = "Chào " + toEmail + ",\n\n"
                + "Mã OTP để đăng ký tài khoản của bạn là: " + otp + "\n\n"
                + "Mã OTP có hiệu lực trong 5 phút.\n"
                + "Nếu bạn không yêu cầu, vui lòng bỏ qua email này.\n\n"
                + "Cảm ơn bạn.";

        send(toEmail, subject, content);
    }


    /**
     * Gửi email khi khôi phục password
     */
    public void sendOTPEmailRecoveryPassword(String toEmail, String otp) {
        String subject = "Mã OTP khôi phục mật khẩu";
        String content = "Chào " + toEmail + ",\n\n"
                + "Mã OTP để khôi phục mật khẩu của bạn là: " + otp + "\n\n"
                + "Mã OTP có hiệu lực trong 5 phút.\n"
                + "Nếu bạn không yêu cầu, vui lòng bỏ qua email này.\n\n"
                + "Cảm ơn bạn.";

        send(toEmail, subject, content);
    }
}
