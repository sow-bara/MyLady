package sn.com.manageuser.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import sn.com.manageuser.dao.RoleRepository;
import sn.com.manageuser.model.Roles;
import sn.com.manageuser.model.Role;

@Component
public class DefaultRolesInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        // Initialisation des rôles par défaut
        for (Roles roleEnum : Roles.values()) {
            if (!roleRepository.existsByRoleName(roleEnum)) {
                Role role = new Role();
                role.setRoleName(roleEnum);
                roleRepository.save(role);
                System.out.println("Role " + roleEnum + " initialized in the database.");
            }
        }
    }
}
