package sn.com.manageuser.security;

import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import sn.com.manageuser.model.CustomUserBean;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

@Component
public class JwtTokenUtil {
	@Value("${security.jwt.secret}")
	private String jwtTokenSecret;
	@Value("${security.jwt.expiration}")
	private long jwtTokenExpiration;


	public String generateJwtToken(Authentication authentication) {
		CustomUserBean userPrincipal = (CustomUserBean) authentication.getPrincipal();

		return Jwts.builder()
				.setSubject(userPrincipal.getUsername())
				.setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + jwtTokenExpiration))
				.signWith(SignatureAlgorithm.HS256, jwtTokenSecret)
				.compact();
	}

	public String getUserNameFromJwtToken(String token) {
		return Jwts.parser()
				.setSigningKey(jwtTokenSecret)
				.parseClaimsJws(token)
				.getBody()
				.getSubject();
	}

	public boolean validateJwtToken(String authToken) {
		try {
			Jwts.parser().setSigningKey(jwtTokenSecret).parseClaimsJws(authToken);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
