/**
 * 
 */
package unidue.ub.bibliometrics.knowledgebase;

import org.apache.log4j.Logger;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 * @author Eike Spielberg
 *
 */
public class PublicationAuthorDAO {

    private final static Logger LOGGER = Logger.getLogger(PublicationAuthorDAO.class);
	
	static void persistAuthor(PublicationAuthor author) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("publicationAuthors");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        em.persist(author);
        LOGGER.info("author persisted");
        tx.commit();
        em.close();
    }
	
	public static void persistAuthors(List<PublicationAuthor> authors) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("publicationAuthors");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        for (PublicationAuthor author : authors)
        	em.persist(author);
        tx.commit();
        em.close();
    }
	
	public static List<PublicationAuthor> getAuthors() {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("publicationAuthors");
		EntityManager em = emf.createEntityManager();
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<PublicationAuthor> q = cb.createQuery(PublicationAuthor.class);
		Root<PublicationAuthor> c = q.from(PublicationAuthor.class);
		q.select(c).where(cb.like(c.<String>get("surname"), "%"));
		TypedQuery<PublicationAuthor> query = em.createQuery(q);
		List<PublicationAuthor> authors = query.getResultList();
		em.close();
		return authors;
	}

}
