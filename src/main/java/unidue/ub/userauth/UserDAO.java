package unidue.ub.userauth;

import java.util.ArrayList;
import java.util.List;

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

import org.apache.log4j.Logger;

import unidue.ub.userauth.User;
import unidue.ub.userauth.UserRole;

public class UserDAO {
    
    private static final Logger LOGGER = Logger.getLogger(UserDAO.class);
    
    public UserDAO() {
    }

	public static User getUser(String username) {
	    EntityManagerFactory emf = Persistence.createEntityManagerFactory("userData");
	    EntityManager em = emf.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<User> q = cb.createQuery(User.class);
		Root<User> c = q.from(User.class);
		q.select(c).where(cb.equal(c.get("username"), username));
		TypedQuery<User> query = em.createQuery(q);
		List<User> users = query.getResultList();
		em.close();
		LOGGER.info("found " + users.size() + " users with username " + username);
		if (users.size() == 1)
			return users.get(0);
		else
			return null;
	}
	
	/**
	 * deletes the <code>User</code> with the given email as well as all the corresponding roles.
	 * 
	 * @param email
	 *            the email of the user
	 * 
	 */
	public static void deleteUser(String username) {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("userData");
		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		
		CriteriaDelete<User> deleteUser = cb.createCriteriaDelete(User.class);
		Root<User> cUser = deleteUser.from(User.class);
		List<Predicate> predicates = new ArrayList<Predicate>();
		predicates.add(cb.equal(cUser.get("username"), username));
		deleteUser.where(predicates.toArray(new Predicate[] {}));
		Query query = em.createQuery(deleteUser);
		query.executeUpdate();
		
		CriteriaDelete<UserRole> deleteRoles = cb.createCriteriaDelete(UserRole.class);
		Root<UserRole> cRoles = deleteRoles.from(UserRole.class);
		List<Predicate> predicatesRoles = new ArrayList<Predicate>();
		predicatesRoles.add(cb.equal(cRoles.get("username"), username));
		deleteRoles.where(predicates.toArray(new Predicate[] {}));
		Query queryRoles = em.createQuery(deleteRoles);
		queryRoles.executeUpdate();
		
		tx.commit();
		em.close();
	}
	
	/**
	 * retrieves the list of user emails registered.
	 * 
	 * @return users the list of user emails
	 * 
	 */
	public static List<String> listUsers() {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("userData");
		EntityManager em = emf.createEntityManager();
		Query query = em.createQuery("SELECT username FROM User");
		@SuppressWarnings("unchecked")
		List<String> users = query.getResultList();
		em.close();
		return users;
	}
}
