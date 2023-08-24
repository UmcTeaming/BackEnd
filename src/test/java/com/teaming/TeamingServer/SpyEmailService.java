package com.teaming.TeamingServer;

import com.teaming.TeamingServer.Service.EmailService;

public class SpyEmailService implements EmailService {
    @Override
    public void sendValidateEmailRequestMessage(String to, String verificationCode) {

    }

    @Override
    public void sendResetPasswordMessage(String email, String newPassword) {

    }
}
