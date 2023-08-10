package com.mauricio.gastos.service;

import com.mauricio.gastos.repositories.UserRepository;
import com.mauricio.gastos.securityjwt.JwtUtils;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Optional;

@Service
@Transactional
public class EmailServiceImpl implements EmailService{

    @Autowired
    JwtUtils jwtUtils;
    private final JavaMailSender javaMailSender;
    @Autowired
    public EmailServiceImpl(JavaMailSender javaMailSender){
        this.javaMailSender = javaMailSender;
    }
    @Value("${correo.remitente}")
    private String remitente;

    @Autowired
    UserRepository userRepository;
    @Autowired
    private TemplateEngine templateEngine;
    @Override
    public void mailSenderVerification(String destinatario, String nombreUsuario) {
        MimeMessage mensaje = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mensaje,true);
            helper.setFrom(remitente);
            helper.setTo(destinatario);
            helper.setSubject("Verificacion de correo electronico");
            String contenidoPlantilla = cargarContenidoPlantilla(nombreUsuario, "http://localhost:3000/verification-email?token="+jwtUtils.generateTokenValidate(nombreUsuario));
            helper.setText(contenidoPlantilla, true);

            javaMailSender.send(mensaje);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean resendValidationEmail(String username) {
        try {
            Optional<String> emailByUsername = userRepository.findEmailByUsername(username);
            if(emailByUsername.isPresent()){
                mailSenderVerification(emailByUsername.get(),username);
                return true;
            }else{
                return false;
            }
        }catch (Exception e){
            return false;
        }
    }

    private String cargarContenidoPlantilla(String nombreUsuario, String enlaceVerificacion) {
        // Crear un contexto de Thymeleaf y agregar las variables necesarias para la plantilla
        Context context = new Context();
        context.setVariable("nombreUsuario", nombreUsuario);
        context.setVariable("enlaceVerificacion", enlaceVerificacion);

        // Procesar la plantilla Thymeleaf con los datos del usuario
        return templateEngine.process("correo", context);
    }
}