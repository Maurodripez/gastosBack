package com.mauricio.gastos.service;
public interface EmailService {
    void mailSenderVerification(String destinatario, String nombreUsuario);

    boolean resendValidationEmail(String username);
}
