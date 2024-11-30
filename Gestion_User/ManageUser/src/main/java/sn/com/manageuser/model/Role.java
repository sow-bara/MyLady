package sn.com.manageuser.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name="role")
@Data
public class Role {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id; 
	@Enumerated(EnumType.STRING)
	@Column(name="name")
	private Roles roleName;

}
