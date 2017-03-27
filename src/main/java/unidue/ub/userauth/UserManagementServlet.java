package unidue.ub.userauth;

import java.io.IOException;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.jdom2.Element;
import org.mycore.frontend.servlets.MCRServletJob;
import org.xml.sax.SAXException;

import unidue.ub.bibliometrics.BibliometricsServlet;
import unidue.ub.userauth.UserDAO;
import unidue.ub.userauth.UserRole;
import unidue.ub.userauth.UserRoleDAO;

/**
 * Controls all the user logging processes, sends errors as xml to be displayed
 * via XSLT.
 * 
 * @author Eike Spielberg
 * @version 1
 */
@WebServlet("/bibliometrics/admin/userManagement")
public class UserManagementServlet extends BibliometricsServlet {

	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = Logger.getLogger(UserManagementServlet.class);

	/**
	 * lists the users in the system and their roles and renders the output as html file
	 * @param job
	 *            <code>MCRServletJob</code>
	 * @exception IOException exception while rendering output
     * @exception TransformerException exception while rendering output
     * @exception SAXException exception while rendering output
     */
	protected void doGet(MCRServletJob job) throws IOException, TransformerException, SAXException {
		org.apache.shiro.subject.Subject currentUser = SecurityUtils.getSubject();
		Element output;
		if (currentUser.hasRole("userAdmin")) {
        	output = prepareOutput(job,"userAdmin","admin","userManagement");
        	List<String> users = UserDAO.listUsers();
        	for (String user : users) {
        		Element userElement = new Element("user");
        		userElement.addContent(new Element("username").addContent(user));
        		List<UserRole> userRoles = UserRoleDAO.getUserRolesByEmail(user);
        		LOGGER.info("found " + userRoles.size() + " roles.");
        		for (UserRole userRole : userRoles) {
        			Element roleElement = new Element("role");
        			roleElement.addContent(userRole.getRoleName());
        			userElement.addContent(roleElement);
        		}
        		
        		output.addContent(userElement);
        	}
        } else
        	output = new Element("error").addContent(new Element("message").addContent("error.noPermission"));
		sendOutput(job,output);
	}
}
