package unidue.ub.bibliometrics;

import java.io.IOException;

import javax.xml.transform.TransformerException;

import org.apache.shiro.SecurityUtils;
import org.jdom2.Element;
import org.mycore.common.content.MCRJDOMContent;
import org.mycore.frontend.servlets.MCRServlet;
import org.mycore.frontend.servlets.MCRServletJob;
import org.xml.sax.SAXException;

/**
 * Extends the MCRServlet by some functionality to ease the handling of request parameters.
 * 
 * @author Eike Spielberg
 * @version 1
 */
public class BibliometricsServlet extends MCRServlet{
	
	private static final long serialVersionUID = 1L;
	/**
     * Takes the HTTP-Request from the <code>MCRServletJob</code> and retrieves the parameter.
     * 
     * @param name the name of the parameter
     * @param job
     *            <code>MCRServletJob</code>
     * @param name the name of the parameter from the HTML request
     * @return String the desired parameter from the <code>MCRServletJob</code>. If the parameter is not found,an empty string is returned
     */
	public static String getParameter(MCRServletJob job, String name) {
        String value = job.getRequest().getParameter(name);
        return value == null ? "" : value.trim();
    }
	
	/**
     * Prepares the basic output with a given root element name and the friendly name and the current timestamp
     * 
     * 
     * @param job
     *            <code>MCRServletJob</code>
     * @param outputName the name of the root element for the output
     * @return an org.jdom2.element acting as root element for the output
     */
    public static Element prepareOutput(MCRServletJob job, String outputName) {
	    Element output = new Element(outputName);
	    Element navbar = new Element("navbar");
	    org.apache.shiro.subject.Subject currentUser = SecurityUtils.getSubject();
	    String username = (String) job.getRequest().getSession().getAttribute("username");
	    navbar.addContent(new Element("loggedInAs").setText(username));
	    navbar.addContent(new Element("qualifiedAs").setText("analyst"));
        if (currentUser.hasRole("userAdmin"))
            navbar.addContent(new Element("admin").setText("true"));
        navbar.addContent(new Element("now").setText(String.valueOf(System.currentTimeMillis())));
        output.addContent(navbar);
	    return output;
	}
    
    /**
     * Prepares the basic output with a given root element name and the friendly name and the current timestamp
     * 
     * 
     * @param job
     *            <code>MCRServletJob</code>
     * @param outputName the name of the root element for the output
     * @param module the name of the module
     * @return an org.jdom2.element acting as root element for the output
     */
    public static Element prepareOutput(MCRServletJob job, String outputName, String module) {
        Element output = new Element(outputName);
        Element navbar = new Element("navbar");
        navbar.addContent(new Element("module").setText(module));
        org.apache.shiro.subject.Subject currentUser = SecurityUtils.getSubject();
        if (currentUser.hasRole("fachreferent")) {
            String friendlyName = (String) job.getRequest().getSession().getAttribute("friendlyName");
            navbar.addContent(new Element("loggedInAs").setText(friendlyName));
        }
        navbar.addContent(new Element("now").setText(String.valueOf(System.currentTimeMillis())));
        output.addContent(navbar);
        return output;
    }
    
    /**
     * Prepares the basic output with a given root element name and the friendly name and the current timestamp
     * 
     * 
     * @param job
     *            <code>MCRServletJob</code>
     * @param outputName the name of the root element for the output
     * @param module the name of the module
     * @param function the name of the function within the module
     * @return an org.jdom2.element acting as root element for the output
     */
    public static Element prepareOutput(MCRServletJob job, String outputName, String module, String function) {
        Element output = new Element(outputName);
        Element navbar = new Element("navbar");
        navbar.addContent(new Element("module").setText(module));
        navbar.addContent(new Element("function").setText(function));
        org.apache.shiro.subject.Subject currentUser = SecurityUtils.getSubject();
        if (currentUser.hasRole("fachreferent")) {
            String friendlyName = (String) job.getRequest().getSession().getAttribute("friendlyName");
            navbar.addContent(new Element("loggedInAs").setText(friendlyName));
        }
        navbar.addContent(new Element("now").setText(String.valueOf(System.currentTimeMillis())));
        output.addContent(navbar);
        return output;
    }
    
    /**
     * renders and sends the prepared output XML to the HTTP response
     * 
     * 
     * @param job
     *            <code>MCRServletJob</code>
     * @param output org.jdom2.element acting as  output
     * @exception IOException thrown while writing the output to the HTTP response
     * @exception TransformerException thrown while rendering the output element
     * @exception SAXException thrown while parsing the output element
     */
    public static void sendOutput(MCRServletJob job, Element output) throws IOException, TransformerException, SAXException {
        getLayoutService().doLayout(job.getRequest(), job.getResponse(), new MCRJDOMContent(output));
    }
}
