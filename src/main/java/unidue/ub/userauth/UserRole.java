package unidue.ub.userauth;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table
public class UserRole implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8432414340180447723L;

	@Id
	@GeneratedValue
	private Integer id;

	private String roleName;
	private String username;

	public UserRole() {
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

}
