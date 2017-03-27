package unidue.ub.userauth;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import unidue.ub.userauth.UserRole;

public class UserRoleDAO {
	/**
	 * retrieves the list of <code>UserRole</code>-objects by its email.
	 * 
	 * @param email
	 *            the email
	 * @return userRoles the roles
	 * 
	 */
	public static List<UserRole> getUserRolesByEmail(String username) {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("userData");
		EntityManager em = emf.createEntityManager();
		CriteriaBuilder cb = em.getCriteriaBuilder();

		CriteriaQuery<UserRole> q = cb.createQuery(UserRole.class);
		Root<UserRole> c = q.from(UserRole.class);
		q.select(c).where(cb.equal(c.get("username"), username));
		TypedQuery<UserRole> query = em.createQuery(q);
		List<UserRole> roles = query.getResultList();
		em.close();
		return roles;
	}

	/**
	 * retrieves the list of roles for a given username.
	 * 
	 * @param username
	 *            the username
	 * @return userRoles the roles
	 * 
	 */
	public static Set<String> getRoles(String username) {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("userData");
		EntityManager em = emf.createEntityManager();
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<UserRole> q = cb.createQuery(UserRole.class);
		Root<UserRole> c = q.from(UserRole.class);
		q.select(c).where(cb.equal(c.get("username"), username));
		TypedQuery<UserRole> query = em.createQuery(q);
		List<UserRole> roles = query.getResultList();
		Set<String> userRoles = new HashSet<String>();
		em.close();
		for (UserRole role : roles)
			userRoles.add(role.getRoleName());
		return userRoles;
	}

	/**
	 * persists a user role
	 * 
	 * @param r
	 *            the role
	 * 
	 */
	public static void insert(UserRole r) {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("userData");
		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		em.persist(r);
		tx.commit();
		em.close();
	}

	/**
	 * deletes a user role
	 * 
	 * @param r
	 *            the role
	 * 
	 */
	public static void delete(UserRole r) {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("userData");
		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaDelete<UserRole> delete = cb.createCriteriaDelete(UserRole.class);
		Root<UserRole> c = delete.from(UserRole.class);
		List<Predicate> predicates = new ArrayList<Predicate>();
		predicates.add(cb.equal(c.get("username"), r.getUsername()));
		predicates.add(cb.equal(c.get("roleName"), r.getRoleName()));
		delete.where(predicates.toArray(new Predicate[] {}));
		Query query = em.createQuery(delete);
		query.executeUpdate();
		tx.commit();
		em.close();
	}

}