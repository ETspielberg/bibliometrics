package unidue.ub.api;

import java.io.IOException;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import org.apache.log4j.Logger;

import org.mycore.common.content.MCRContent;
import org.mycore.common.content.transformer.MCRXSL2XMLTransformer;

import unidue.ub.api.connectors.ScopusConnector;

public class MCRScopusDOIresolver implements URIResolver {

    private final static Logger LOGGER = Logger.getLogger(MCRScopusDOIresolver.class);

    /**
     * Reads document description with a given doi from the Scopus API.
     *
     * 
     *         
     *
     */

    public Source resolve(String href, String base) throws TransformerException {
        String doi = href.substring(href.indexOf(":") + 1);
        LOGGER.debug("Reading MCRContent with DOI " + doi);

        ScopusConnector connection = new ScopusConnector();
        try {
            MCRContent content = connection.getPublicationByDOI(doi);
            if (content == null) {
                return null;
            }
            MCRXSL2XMLTransformer transformer = new MCRXSL2XMLTransformer("xsl/scopus2mods.xsl");
            MCRContent mods = transformer.transform(content);
            LOGGER.debug("end resolving " + href);
            return mods.getSource();
        } catch (IOException e) {
            throw new TransformerException(e);
        }
    }

}
