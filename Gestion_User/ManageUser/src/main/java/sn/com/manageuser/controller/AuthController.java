package sn.com.manageuser.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import sn.com.manageuser.dao.RoleRepository;
import sn.com.manageuser.dao.UserRepository;
import sn.com.manageuser.model.AuthResponse;
import sn.com.manageuser.model.CustomUserBean;
import sn.com.manageuser.model.Role;
import sn.com.manageuser.model.Roles;
import sn.com.manageuser.model.SignupRequest;
import sn.com.manageuser.model.User;
import sn.com.manageuser.security.JwtTokenUtil;
import sn.com.manageuser.service.AuthService;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/auth")
public class AuthController {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private PasswordEncoder encoder;
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	@Autowired
	private AuthService authService;

	@PostMapping("/login")
	public ResponseEntity<?> login(@Valid @RequestBody User user) {
		try {
			System.out.println("Athentification de l'utlisateur: " + user.getUserName());

			Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(user.getUserName(), user.getPassword())
			);

			SecurityContextHolder.getContext().setAuthentication(authentication);

			String token = jwtTokenUtil.generateJwtToken(authentication);
			CustomUserBean userBean = (CustomUserBean) authentication.getPrincipal();

			List<String> roles = userBean.getAuthorities().stream()
					.map(auth -> auth.getAuthority())
					.collect(Collectors.toList());

			AuthResponse authResponse = new AuthResponse();
			authResponse.setToken(token);
			authResponse.setRoles(roles);

			System.out.println(" Utilisateur authentifié avec succès: " + user.getUserName());
			return ResponseEntity.ok(authResponse);
		} catch (Exception e) {
			System.err.println("Echec d'authentification de l'utilisateur: " + user.getUserName() + " - " + e.getMessage());
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Username ou Mot de passe invalide");
		}
	}

	@PostMapping("/signup")
	public ResponseEntity<?> userSignup(@Valid @RequestBody SignupRequest signupRequest) {
		if (userRepository.existsByUserName(signupRequest.getUserName())) {
			return ResponseEntity.badRequest().body("Username déja pris");
		}
		if (userRepository.existsByEmail(signupRequest.getEmail())) {
			return ResponseEntity.badRequest().body("Email déja pris");
		}

		// Création de l'utilisateur
		User user = new User();
		Set<Role> roles = new HashSet<>();
		user.setUserName(signupRequest.getUserName());
		user.setEmail(signupRequest.getEmail());
		user.setPassword(encoder.encode(signupRequest.getPassword()));
		user.setEnabled(false); // L'utilisateur doit confirmer son compte via email

		// Vérification des rôles spécifiés dans la requête
		String[] roleArr = signupRequest.getRoles();

		// Si aucun rôle n'est spécifié, ajouter par défaut le rôle USER
		if (roleArr == null || roleArr.length == 0) {
			roleArr = new String[]{"user"};  // Ajouter rôle par défaut
		}

		for (String role : roleArr) {
			String roleName = "ROLE_" + role.toUpperCase();

			try {
				Roles roleEnum = Roles.valueOf(roleName);  // Convertir le string en enum
				Optional<Role> roleOptional = roleRepository.findByRoleName(roleEnum);

				if (roleOptional.isEmpty()) {
					return ResponseEntity.badRequest().body("Role non trouvé: " + role);
				}
				roles.add(roleOptional.get());
			} catch (IllegalArgumentException e) {
				return ResponseEntity.badRequest().body("ROLE SPECIFIE INVALIDE: " + role);
			}
		}

		// Associer les rôles à l'utilisateur
		user.setRoles(roles);
		userRepository.save(user);

		// Envoi de l'email de confirmation
		authService.registerUser(user);

		return ResponseEntity.ok("Un email de confirmation a été envoyé à votre adresse !");
	}
	@GetMapping("/verify")
	public String verifyAccount(@RequestParam("token") String token) {
		try {
			authService.verifyAccount(token);  // Vérifier le token et activer le compte
			return "Votre compte a été activé avec succès!";
		} catch (Exception e) {
			System.err.println("Account verification failed: " + e.getMessage());
			return "Erreur : " + e.getMessage();
		}
	}
}
