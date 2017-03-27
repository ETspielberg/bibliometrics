package unidue.ub.api;

import java.io.File;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import org.mycore.common.config.MCRConfiguration;
import org.mycore.common.content.MCRContent;
import org.mycore.common.content.transformer.MCRXSL2XMLTransformer;
import org.mycore.frontend.servlets.MCRServlet;
//import org.mycore.common.content.transformer.MCRXSL2XMLTransformer;
import org.mycore.frontend.servlets.MCRServletJob;

import unidue.ub.api.connectors.OrcidConnector;

@WebServlet("/askOrcid")
public class AskOrcidServlet extends MCRServlet {

    private static final long serialVersionUID = 1L;
    
    private final static String resultsDir;

    static {
        MCRConfiguration config = MCRConfiguration.instance();
        resultsDir = config.getString("ub.bibliometrics.resultsDir");
    }

    public void doGetPost(MCRServletJob job) throws Exception {
      //prepare request
        HttpServletRequest req = job.getRequest();
        
        //prepare Connection
        OrcidConnector connection = new OrcidConnector();
        
        //get parameter from URL
        String orcid = getParameter(req, "id");
        
        //send request to API
        MCRContent orcidExport = connection.getPublicationsByOrcid(orcid);
        
       //transform in MODS
        MCRXSL2XMLTransformer transformer = new MCRXSL2XMLTransformer("xsl/Orcid2mods.xsl");
        MCRContent mods = transformer.transform(orcidExport);
        
        //save results
        mods.sendTo(new File(resultsDir,"OrcidProfile_mods_OrcID" + orcid + ".xml"));
        
        connection.close();
    }

    private String getParameter(HttpServletRequest req, String name) {
        // read parameter "name" from URL
        String value = req.getParameter(name);
        return value == null ? "" : value.trim();
    }
}
