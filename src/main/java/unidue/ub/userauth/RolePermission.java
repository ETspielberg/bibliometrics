package unidue.ub.userauth;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Plain old java object holding the roles and the related permissions. The
 * fields can be persisted.
 * 
 * @author Eike Spielberg
 * @version 1
 */
@Entity
@Table
public class RolePermission implements Serializable {

	private static final long serialVersionUID = -2070357513686679164L;

	@Id
	@GeneratedValue
	private Integer id;

	private String permission;
	private String roleName;

	/**
	 * Build role-permission-entity
	 */
	public RolePermission() {
	}

	/**
	 * return the permission related to the role
	 * 
	 * @return permission the permission
	 */
	public String getPermission() {
		return permission;
	}

	/**
	 * set the permission related to the role
	 * 
	 * @param permission
	 *            the permission
	 */
	public void setPermission(String permission) {
		this.permission = permission;
	}

	/**
	 * return the name of the role
	 * 
	 * @return roleName the name of the role
	 */
	public String getRoleName() {
		return roleName;
	}

	/**
	 * set the name of the role
	 * 
	 * @param roleName
	 *            the role name
	 */
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

}
