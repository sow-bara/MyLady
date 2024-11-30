package sn.com.manageuser.dao;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sn.com.manageuser.model.Role;
import sn.com.manageuser.model.Roles;


@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
	Optional<Role> findByRoleName(Roles role);
	boolean existsByRoleName(Roles roleName);
}
