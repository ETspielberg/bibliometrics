package unidue.ub.api;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import org.jdom2.Document;

import org.json.JSONObject;
import org.json.XML;
import org.mycore.common.content.MCRContent;
import org.mycore.common.content.MCRStringContent;
import org.mycore.frontend.servlets.MCRServlet;
import org.mycore.common.content.transformer.MCRXSL2XMLTransformer;
import org.mycore.frontend.servlets.MCRServletJob;

import unidue.ub.api.connectors.CrossRefConnector;

@WebServlet("/askCrossRef")
public class AskCrossRefServlet extends MCRServlet {

    private static final long serialVersionUID = 1L;

    private Document recievedXML = null;

    @Override
    public void doGetPost(MCRServletJob job) throws Exception {
        //prepare request
        HttpServletRequest req = job.getRequest();

        //get doi from URL
        String doi = getParameter(req, "id");


      //prepare connection
        CrossRefConnector connection = new CrossRefConnector();

        //send request to API
        MCRContent crossRefExport = connection.getPublicationByDOI(doi);

        //transform JSON-response to XML
        MCRStringContent xml = new MCRStringContent(XML.toString(new JSONObject(crossRefExport.asString()), "content"));

        //transform xml to mods
        MCRXSL2XMLTransformer transformer = new MCRXSL2XMLTransformer("xsl/CrossRef2mods.xsl");
        MCRContent mods = transformer.transform(xml);

        
        connection.close();
        mods.sendTo(job.getResponse().getOutputStream());

    }

    private String getParameter(HttpServletRequest req, String name) {
        String value = req.getParameter(name);
        return value == null ? "" : value.trim();
    }

    public Document getRecievedXML() {
        return recievedXML;
    }
}
