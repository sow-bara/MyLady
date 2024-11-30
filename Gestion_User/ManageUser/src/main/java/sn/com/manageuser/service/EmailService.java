package sn.com.manageuser.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import sn.com.manageuser.model.User;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendVerificationEmail(User user, String token) {
        String subject = "Confirmation de compte";
        String confirmationUrl = "http://localhost:9110/auth/verify?token=" + token; // URL de confirmation
        String message = "Bonjour " + user.getUserName() + ",\n\n" +
                "Veuillez cliquer sur le lien suivant pour activer votre compte :\n" +
                confirmationUrl + "\n\n" +
                "Merci.";

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(user.getEmail());
        email.setSubject(subject);
        email.setText(message);

        try {
            System.out.println("Envoi de l'email de confirmation à: " + user.getEmail());
            mailSender.send(email);
            System.out.println("Email envoyé avec succès à: " + user.getEmail());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de l'envoi de l'email: " + e.getMessage());
        }
    }
}
