package unidue.ub.bibliometrics;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.xml.transform.TransformerException;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.mycore.frontend.servlets.MCRServletJob;
import org.xml.sax.SAXException;

/**
 * Allows a personalized start page.
 * 
 * @author Eike Spielberg
 * @version 1
 */
@WebServlet("/analysis/start")
public class StartServlet extends BibliometricsServlet {

	private static final long serialVersionUID = 1L;

	/**
	 * reads the user credentials and prepares a simple xml file that allows a
	 * personlized welcome page.
	 * 
	 * @param job
	 *            <code>MCRServletJob</code>
	 * @exception IOException
	 *                exception while reading systematik.xml file from disk
	 * @exception JDOMException
	 *                exception upon parsing the systematik.xml file
	 * @exception TransformerException
	 *                exception while rendering output
	 * @exception SAXException
	 *                exception while rendering output
	 */
	protected void doGetPost(MCRServletJob job)
			throws ServletException, IOException, JDOMException, TransformerException, SAXException {
		Element output = prepareOutput(job, "start");
		sendOutput(job, output);
	}
}
