package unidue.ub.api;

import java.io.File;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import org.mycore.common.config.MCRConfiguration;
import org.mycore.common.content.MCRContent;
import org.mycore.common.content.MCRJDOMContent;
import org.mycore.frontend.servlets.MCRServlet;
import org.mycore.frontend.servlets.MCRServletJob;

import unidue.ub.api.connectors.MyCoReConnector;

@WebServlet("/askMycore")
public class AskMyCoReServlet extends MCRServlet {

    private static final long serialVersionUID = 1L;

    private final static String resultsDir;
    
    static {
        MCRConfiguration config = MCRConfiguration.instance();
        resultsDir = config.getString("ub.bibliometrics.resultsDir");
    }

    @Override
    public void doGetPost(MCRServletJob job) throws Exception {
        //prepare request
        HttpServletRequest req = job.getRequest();

        //get doi from URL
        String id = getParameter(req, "id");

        MyCoReConnector connection = new MyCoReConnector();

        //send request to API
        MCRContent mycore = new MCRJDOMContent(connection.getPublicationByID(id));

        //save to file
        mycore.sendTo(new File(resultsDir,"MyCoReExport_ID"+id+".xml"));
        connection.close();
    }

    private String getParameter(HttpServletRequest req, String name) {
        String value = req.getParameter(name);
        return value == null ? "" : value.trim();
    }
}
