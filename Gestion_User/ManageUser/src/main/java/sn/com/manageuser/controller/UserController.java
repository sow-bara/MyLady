package sn.com.manageuser.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins="*")
@RequestMapping("/user")
public class UserController {

	// Permet à tous les utilisateurs d'afficher tous les utilisateurs
	@GetMapping("/allusers")
	public String displayUsers() {
		return "Display All Users";
	}

	// Permet l'affichage aux utilisateurs avec le rôle 'USER' ou 'ADMIN'
	@GetMapping("/displayuser")
	@PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
	public String displayToUser() {
		return "Display to both user and admin";
	}

	// Permet l'affichage uniquement aux utilisateurs avec le rôle 'ADMIN'
	@GetMapping("/displayadmin")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public String displayToAdmin() {
		return "Display only to admin";
	}
}
