package unidue.ub.api;

import java.io.File;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import org.mycore.common.config.MCRConfiguration;
import org.mycore.common.content.MCRContent;
import org.mycore.frontend.servlets.MCRServlet;
import org.mycore.common.content.transformer.MCRXSL2XMLTransformer;
import org.mycore.frontend.servlets.MCRServletJob;

import unidue.ub.api.connectors.ScopusConnector;

@WebServlet("/askScopus")
public class AskScopusServlet extends MCRServlet {

    // TODO fehlerhafte DOI: 10.1002/bit.20803
    // TODO fehlerhafte DOI: 10.1158/1535-7163

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

        //read in service parameter
        String service = getParameter(req, "service");

        //prepare parameter needed in different cases
        String doi;
        String eid;
        String authorID;
        String scopusID;
        MCRXSL2XMLTransformer transformer;
        MCRContent mods;
        MCRContent scopusExport;

        //prepare connector
        ScopusConnector connection = new ScopusConnector();

        //depending on the service parameter execute different request to the API
        switch (service) {
            case "all":
                //get complete list of UDE publications, results are arranged as blocks in a list. Size of blocks and length are determined automatically in the ScopusConnector 
                List<MCRContent> scopusExportList = connection.getAll();

                //transform each block into a modsCollection
                //initialize counter for filenames
                int counter = 1;
                for (MCRContent scopusExportIndividual : scopusExportList) {
                    //transform block to modsCollection
                    transformer = new MCRXSL2XMLTransformer("xsl/scopusCollection2modsCollection.xsl");
                    mods = transformer.transform(scopusExportIndividual);

                    //save to file with incremental filename
                    mods.sendTo(new File(resultsDir, "ScopusExport_UDE_mods" + Integer.toString(counter) + ".xml"));

                    //increase counter
                    counter++;
                }
                break;
            case "update":
                //get Update 7 days ago, not yet implemented, transformation to mods is missing, realized by search ("query")
                scopusExport = connection.getUpdate();
                scopusExport.sendTo(new File("ScopusExport_Update.xml"));
                break;
            case "doi":
                //get abstract to a particular doi, needs an additional condition to determine the number of results (to exclude two answers to one doi)
                //read doi as id-parameter from URL
                doi = getParameter(req, "id");

                //collect response from API
                scopusExport = connection.getPublicationByDOI(doi);

                //transform to mods
                transformer = new MCRXSL2XMLTransformer("xsl/scopus2mods.xsl");
                mods = transformer.transform(scopusExport);

                //save to file
                // connection.updateCitationCount(mods.asXML());
                mods.sendTo(new File(resultsDir, "ScopusExport_DOI_mods.xml"));
                break;
            case "eid":
                //get abstract to a particular doi, needs an additional condition to determine the number of results (to exclude two answers to one doi)
                //read doi as id-parameter from URL
                eid = getParameter(req, "id");

                //collect response from API
                scopusExport = connection.getPublicationByEID(eid);

                //transform to mods
                transformer = new MCRXSL2XMLTransformer("xsl/scopus2mods.xsl");
                mods = transformer.transform(scopusExport);

                //save to file
                mods.sendTo(new File(resultsDir, "ScopusExport_EID_mods.xml"));
                break;
            case "authorProfile":
                //get author-profile from Scopus (name variants, history of affiliations, no documents up to now!
                //read authorID from URL
                authorID = getParameter(req, "id");

                //collect response from API
                scopusExport = connection.getAuthorProfile(authorID);

                //save to file
                scopusExport.sendTo(new File(resultsDir,"Scopus_AuthorProfile_AuthorID" + authorID + ".xml"));
                break;
            case "authorPublications":
                //get all publications for 1 authorID, realized by search ("query")
                //read authorID from URL
                authorID = getParameter(req, "id");

                //collect response from API
                scopusExport = connection.getAuthorPublications(authorID);

                //transform to mods
                transformer = new MCRXSL2XMLTransformer("xsl/scopusAuthorDocuments2modsCollection.xsl");
                mods = transformer.transform(scopusExport);

                //save to file
                mods.sendTo(new File(resultsDir, "Scopus_Publications_AuthorID" + authorID + "_mods.xml"));
                break;
            case "scopusID":
                //get abstract to a particular scopusID (not authorID!)
                //read scopusID from URL
                scopusID = getParameter(req, "id");

                //collect response from API
                scopusExport = connection.getPublicationByScopusID(scopusID);

                //transform to mods
                transformer = new MCRXSL2XMLTransformer("xsl/scopus2mods.xsl");
                mods = transformer.transform(scopusExport);

                //save to file
                scopusExport.sendTo(new File(resultsDir, "ScopusExport_ScopusID" + scopusID + "_mods.xml"));
                break;
            
            default:
                ;
        }
        connection.close();
    }

    private String getParameter(HttpServletRequest req, String name) {
        //read parameter "name" from URL
        String value = req.getParameter(name);
        return value == null ? "" : value.trim();
    }
}
