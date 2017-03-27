package unidue.ub.userauth;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 * DAO for retrieving permissions.
 * 
 * @author Eike Spielberg
 * @version 1
 */
public class RolePermissionDAO {

	/**
	 * Build DAO-entity
	 */
	public RolePermissionDAO() {
	}

	/**
	 * retrieves the <code>User</code> by its email.
	 * 
	 * @param email
	 *            the email
	 * @return user the user
	 * 
	 */
	/**
	 * retrieves the <code>RolesPermission</code> by the role.
	 * 
	 * @param role
	 *            the role
	 * @return userPermissions the list of permissions of the user
	 * 
	 */
	public static Set<String> getPermission(String role) {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("userData");
		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<RolePermission> q = cb.createQuery(RolePermission.class);
		Root<RolePermission> c = q.from(RolePermission.class);
		q.select(c).where(cb.equal(c.get("roleName"), role));
		TypedQuery<RolePermission> query = em.createQuery(q);
		List<RolePermission> permissions = query.getResultList();
		Set<String> userPermissions = new HashSet<String>();
		for (RolePermission permission : permissions)
			userPermissions.add(permission.getPermission());
		tx.commit();
		em.close();
		return userPermissions;
	}
}
