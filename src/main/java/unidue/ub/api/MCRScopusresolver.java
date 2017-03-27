package unidue.ub.api;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import org.apache.log4j.Logger;

import org.mycore.common.content.MCRContent;

import unidue.ub.api.connectors.ScopusConnector;

public class MCRScopusresolver implements URIResolver {

    private final static Logger LOGGER = Logger.getLogger(MCRScopusDOIresolver.class);

    /**
     * Reads the response form the from the Scopus API.
     * allowed queries are 
     * scopus:doi:10.1013/abc
     * scopus:authorProfile:AuthorID
     * scopus:authorPublications:AuthorID
     * scopus:document:scopusID
     * 
     * 
     *    @author Eike Spielberg     
     *
     */

    public Source resolve(String href, String base) throws TransformerException {
        String params = href.substring(href.indexOf(":") + 1);
        String service, id;
        MCRContent content = null;
        service = params.substring(0, params.indexOf(":"));
        id = params.substring(params.indexOf(":") + 1);
        ScopusConnector connection = new ScopusConnector();
        try {
            switch (service) {
                case "doi":
                    LOGGER.debug("Reading MCRContent with DOI " + id);
                    content = connection.getPublicationByDOI(id);
                    if (content == null) {
                        return null;
                    }
                    LOGGER.debug("end resolving " + href);
                    break;

                case "authorProfile":
                    LOGGER.debug("Reading MCRContent with form author profile " + id);
                    content = connection.getAuthorProfile(id);
                    if (content == null) {
                        return null;
                    }
                    LOGGER.debug("end resolving " + href);
                    break;

                case "authorPublications":
                    LOGGER.debug("Reading MCRContent from authorID search " + id);
                    content = connection.getAuthorPublications(id);
                    if (content == null) {
                        return null;
                    }
                    LOGGER.debug("end resolving " + href);
                    break;

                case "document":
                    LOGGER.debug("Reading MCRContent from scopusID search " + id);
                    content = connection.getPublicationByScopusID(id);
                    if (content == null) {
                        return null;
                    }
                    LOGGER.debug("end resolving " + href);
                    break;

                default:
                    ;
            }
            connection.close();
            return content.getSource();
        } catch (Exception e) {
            throw new TransformerException(e);
        }
    }
}
