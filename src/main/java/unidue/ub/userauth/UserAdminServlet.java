package unidue.ub.userauth;

import javax.servlet.annotation.WebServlet;

import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.jdom2.Element;
import org.mycore.frontend.servlets.MCRServletJob;

import unidue.ub.bibliometrics.BibliometricsServlet;
import unidue.ub.userauth.UserDAO;
import unidue.ub.userauth.UserRole;
import unidue.ub.userauth.UserRoleDAO;

/**
 * Offers a web interface to manage users and their roles
 * 
 * @author Eike Spielberg
 * @version 1
 */
@WebServlet("/bibliometrics/admin/userAdmin")
public class UserAdminServlet extends BibliometricsServlet {

	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = Logger.getLogger(UserAdminServlet.class);

	/**
     * deletes user and manages user roles upon http requests
     * 
     * @param job
    *            <code>MCRServletJob</code>
     */
	protected void doPost(MCRServletJob job) throws Exception {
		org.apache.shiro.subject.Subject currentUser = SecurityUtils.getSubject();
		if (currentUser.hasRole("userAdmin")) {
			String type = getParameter(job, "type");
			String name = getParameter(job, "name");
			if (type.equals("userDelete")) {
				UserDAO.deleteUser(name);
				LOGGER.info("deleted user " + name);

			} else if (type.equals("roleDelete")) {
				String roleName = getParameter(job, "roleName");
				UserRole role = new UserRole();
				role.setUsername(name);
				role.setRoleName(roleName);
				UserRoleDAO.delete(role);
				LOGGER.info("deleted role " + roleName + " for user " + name);
			} else if (type.equals("roleAdd")) {
				String roleName = getParameter(job, "roleName");
				UserRole role = new UserRole();
				role.setUsername(name);
				role.setRoleName(roleName);
				UserRoleDAO.insert(role);
				LOGGER.info("added role " + roleName + " for user " + name);
			} 
			job.getResponse().sendRedirect("userManagement");
		}else {
			Element output = new Element("error").addContent((new Element("message")).addContent("error.noPermission"));
			sendOutput(job,output);
		}
	}
}
