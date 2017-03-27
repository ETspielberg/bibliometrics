package unidue.ub.api;

import java.io.FileWriter;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import org.mycore.frontend.servlets.MCRServlet;
import org.mycore.frontend.servlets.MCRServletJob;

import unidue.ub.api.connectors.CrossRefConnector;
import unidue.ub.api.connectors.ScopusConnector;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.json.JSONObject;
import org.json.XML;
import org.mycore.common.MCRConstants;
import org.mycore.common.content.MCRContent;
import org.mycore.common.content.MCRStringContent;
import org.mycore.common.content.transformer.MCRXSL2XMLTransformer;

/**
 *
 * @author Martin Grunwald
 * @version 01.12.2015
 */

@WebServlet("/askAll")
public class AskAllServlet extends MCRServlet {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    CrossRefConnector crossRefConnection = new CrossRefConnector();
    ScopusConnector scopusConnection = new ScopusConnector();
    private final Namespace modsNS = MCRConstants.MODS_NAMESPACE;

    @Override
    public void doGetPost(MCRServletJob job) throws Exception {

        //prepare request
        HttpServletRequest req = job.getRequest();

        //get doi from URL
        String doi = getParameter(req, "id");

        //send request to CrossRef API
        Document crossRefDocument = xmlFromDOICrossRef(doi);
        Element crossRefExport = crossRefDocument.getRootElement().clone();
        Element crossRefElement = new Element("crossref-export");
        crossRefElement.addContent(crossRefExport);

        //send request to Scopus API
        Document scopusDocument = xmlFromDOIScopus(doi);
        Element scopusExport = scopusDocument.getRootElement().clone();
        Element scopusElement = new Element("scopus-export");
        scopusElement.addContent(scopusExport);
        System.out.println(scopusElement.getName());
        
        // check, if the scopus-export contains multiple results
        if(potentialScopusResult(scopusElement)) {
            ExportComparator comp = new ExportComparator();
            Element fittingElement = comp.getFittingElement(scopusElement, crossRefElement);
            Element newScopusElement = fittingElement
                    .getChild("extension", modsNS).clone();
            scopusElement.removeContent();
            scopusElement.setContent(newScopusElement);
        }

        //build document
        Document fullDoc = new Document();
        Element rootElement = new Element("api-retrieval");
        rootElement.addContent(crossRefElement);
        rootElement.addContent(scopusElement);

        fullDoc.setRootElement(rootElement);

        XMLOutputter xmlOutput = new XMLOutputter(Format.getPrettyFormat());
        xmlOutput.output(fullDoc, new FileWriter("AllExport_DOI_mods.xml"));

        scopusConnection.close();
        crossRefConnection.close();

    }

    private String getParameter(HttpServletRequest req, String name) {
        String value = req.getParameter(name);
        return value == null ? "" : value.trim();
    }

    private Document xmlFromDOICrossRef(String doi) throws Exception {

        //send request to API
        MCRContent crossRefExport = crossRefConnection.getPublicationByDOI(doi);

        //transform JSON-response to XML
        MCRStringContent xml = new MCRStringContent(XML.toString(new JSONObject(crossRefExport.asString()), "content"));

        //transform xml to mods
        MCRXSL2XMLTransformer transformer = new MCRXSL2XMLTransformer("CrossRef2mods.xsl");
        MCRContent mods = transformer.transform(xml);

        return mods.asXML();
    }

    private Document xmlFromDOIScopus(String doi) throws Exception {
        //collect response from API
        MCRContent scopusExport = scopusConnection.getPublicationByDOI(doi);
        
        //transform to mods
        MCRXSL2XMLTransformer transformer = new MCRXSL2XMLTransformer("scopus2mods.xsl");
        MCRContent mods = transformer.transform(scopusExport);
        return mods.asXML();
    }
    
    private boolean potentialScopusResult(Element e) {
        List<Element> result = e.getChild("mods", modsNS).getChildren();
        for (Element element : result) {
            if (element.getName().equals("potential-result")) {
                return true;
            }
        }
        return false;
    }

}
