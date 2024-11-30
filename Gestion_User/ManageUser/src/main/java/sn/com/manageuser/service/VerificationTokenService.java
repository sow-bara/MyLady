package sn.com.manageuser.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sn.com.manageuser.dao.VerificationTokenRepository;
import sn.com.manageuser.model.User;
import sn.com.manageuser.model.VerificationToken;

import java.util.Optional;
import java.util.UUID;

@Service
public class VerificationTokenService {

    @Autowired
    private VerificationTokenRepository tokenRepository;

    public VerificationToken createToken(User user) {
        VerificationToken token = new VerificationToken();
        token.setToken(UUID.randomUUID().toString());
        token.setUser(user);
        token.setExpiryDate(1440); // Expiration en 24 heures
        tokenRepository.save(token);
        return token;
    }

    public Optional<VerificationToken> getToken(String token) {
        return tokenRepository.findByToken(token);
    }
}
