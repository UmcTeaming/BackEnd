package com.teaming.TeamingServer.Service;

public interface EmailService {
    void sendValidateEmailRequestMessage(String to, String verificationCode);
    void sendResetPasswordMessage(String email, String newPassword) throws Exception;
}
