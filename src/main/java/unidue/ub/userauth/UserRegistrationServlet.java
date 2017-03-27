package unidue.ub.userauth;


import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.servlet.annotation.WebServlet;

import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.mycore.common.config.MCRConfiguration;
import org.mycore.frontend.servlets.MCRServletJob;

import unidue.ub.bibliometrics.BibliometricsServlet;

@WebServlet("/userRegistration")
public class UserRegistrationServlet extends BibliometricsServlet {
    
	private final static String applicationName;

	static {
		// get parameters from mycore.properties
		MCRConfiguration config = MCRConfiguration.instance();
		applicationName = config.getString("MCR.AppName");
	}
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private static final Logger LOGGER = Logger.getLogger(UserRegistrationServlet.class);

    public void doPost(MCRServletJob job) throws Exception {
        String username = getParameter(job, "username");
        String plainTextPassword = getParameter(job, "password");
        
        
        
        User user = new User();
        user.setUsername(username);
        
        registrate(user, plainTextPassword);
        
        UsernamePasswordToken token = new UsernamePasswordToken(username,plainTextPassword);
        org.apache.shiro.subject.Subject currentUser = SecurityUtils.getSubject();
        try {
            currentUser.login(token);
            
            LOGGER.info("User [" + currentUser.getPrincipal().toString() + "] logged in successfully.");
            currentUser.getSession().setAttribute("username", username);
            
        } catch (UnknownAccountException uae) {
          LOGGER.info("There is no user with username of "
                    + token.getPrincipal());
        } catch (IncorrectCredentialsException ice) {
            LOGGER.info("Password for account " + token.getPrincipal()
                    + " was incorrect!");
        } catch (LockedAccountException lae) {
            LOGGER.info("The account for username " + token.getPrincipal()
                    + " is locked.  "
                    + "Please contact your administrator to unlock it.");
        }
        
        
        job.getResponse().sendRedirect(applicationName + "/start");
    }

	private void registrate(User user, String plainTextPassword) {		  
		  generatePassword(user, plainTextPassword);
		  LOGGER.info("persisting user " + user.getUsername() + " with password " + user.getPassword() + " and salt " + user.getSalt());
		  EntityManagerFactory emf = Persistence.createEntityManagerFactory("userData");
		  EntityManager em = emf.createEntityManager();
		  EntityTransaction tx = em.getTransaction();
		  tx.begin();
		  em.persist(user);
		  LOGGER.info("user persisted");
		  tx.commit();
		  em.close();
		}

		private void generatePassword(User user, String plainTextPassword) {
		  RandomNumberGenerator rng = new SecureRandomNumberGenerator();
		  Object salt = rng.nextBytes();
		  String hashedPasswordBase64 = new Sha256Hash(plainTextPassword, salt,1024).toBase64();
		  user.setPassword(hashedPasswordBase64);
		  user.setSalt(salt.toString());
		}
}
