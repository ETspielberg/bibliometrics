package unidue.ub.userauth;

import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.util.Factory;
import org.apache.shiro.web.util.SavedRequest;
import org.apache.shiro.web.util.WebUtils;
import org.jdom2.Element;
import org.mycore.frontend.servlets.MCRServletJob;
import org.xml.sax.SAXException;

import unidue.ub.bibliometrics.BibliometricsServlet;

@WebServlet("/userLogging")
public class UserLoggingServlet extends BibliometricsServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final static Logger LOGGER = Logger.getLogger(UserLoggingServlet.class);

	public UserLoggingServlet() {
		Factory<org.apache.shiro.mgt.SecurityManager> shiroFactory = new IniSecurityManagerFactory();
		org.apache.shiro.mgt.SecurityManager securityManager = shiroFactory.getInstance();
		SecurityUtils.setSecurityManager(securityManager);
	}

	public void doGet(MCRServletJob job) throws IOException, TransformerException, SAXException {
		Element output = new Element("userLogging");
		if (job.getRequest().getParameter("logout") != null) {
			org.apache.shiro.subject.Subject currentUser = SecurityUtils.getSubject();
			currentUser.logout();
			job.getResponse().sendRedirect("index.html");
		}
		sendOutput(job, output);
	}

	public void doPost(MCRServletJob job) throws Exception {
		Element output = new Element("userLogging");
		String username = getParameter(job, "username");
		String password = getParameter(job, "password");
		boolean rememberMe = "true".equals(getParameter(job, "rememberMe"));

		boolean b = false;
		if (username == null)
			output.addContent((new Element("message")).addContent("login.message.noUserGiven"));

		else if (password == null)
			output.addContent((new Element("message")).addContent("login.message.noPasswordGiven"));

		else {
			b = tryLogin(username, password, rememberMe);
			if (b) {
				SavedRequest savedRequest = WebUtils.getAndClearSavedRequest(job.getRequest());
				if (savedRequest != null)
					job.getResponse().sendRedirect(savedRequest.getRequestUrl());
				else
					job.getResponse().sendRedirect("analysis/start");
			} else
				output.addContent((new Element("message")).addContent("login.message.loginFailed"));
		}
		sendOutput(job, output);
	}

	public boolean tryLogin(String username, String password, Boolean rememberMe) {
		org.apache.shiro.subject.Subject currentUser = SecurityUtils.getSubject();

		if (!currentUser.isAuthenticated()) {
			UsernamePasswordToken token = new UsernamePasswordToken(username, password);
			token.setRememberMe(rememberMe);

			try {
				currentUser.login(token);
				LOGGER.info("User [" + currentUser.getPrincipal().toString() + "] logged in successfully.");
				// save username in the session
				currentUser.getSession().setAttribute("username", username);
				return true;
			} catch (UnknownAccountException uae) {
				LOGGER.info("There is no user with username of " + token.getPrincipal());
			} catch (IncorrectCredentialsException ice) {
				LOGGER.info("Password for account " + token.getPrincipal() + " was incorrect!");
			} catch (LockedAccountException lae) {
				LOGGER.info("The account for username " + token.getPrincipal() + " is locked.  "
						+ "Please contact your administrator to unlock it.");
			}
		} else {
			return true;
		}

		return false;
	}

	public void logout() {
		org.apache.shiro.subject.Subject currentUser = SecurityUtils.getSubject();
		currentUser.logout();
	}
}
