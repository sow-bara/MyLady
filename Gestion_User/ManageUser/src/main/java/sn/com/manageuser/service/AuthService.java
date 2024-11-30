package sn.com.manageuser.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sn.com.manageuser.dao.UserRepository;
import sn.com.manageuser.model.User;
import sn.com.manageuser.model.VerificationToken;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VerificationTokenService tokenService;

    @Autowired
    private EmailService emailService;

    public void registerUser(User user) {
        // Créer un jeton de vérification
        VerificationToken token = tokenService.createToken(user);

        // Envoyer un email de confirmation
        emailService.sendVerificationEmail(user, token.getToken());
    }

    public void verifyAccount(String token) {
        VerificationToken verificationToken = tokenService.getToken(token)
                .orElseThrow(() -> new RuntimeException("Token invalide"));

        if (verificationToken.isExpired()) {
            throw new RuntimeException("Token expiré");
        }

        // Récupérer l'utilisateur lié au token
        User user = verificationToken.getUser();

        // Mettre à jour l'utilisateur pour activer son compte
        if (!user.isEnabled()) {
            user.setEnabled(true); // Activer le compte
            userRepository.save(user); // Sauvegarder l'utilisateur activé
        } else {
            throw new RuntimeException("Le compte est déjà activé.");
        }
    }


}
