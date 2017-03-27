package unidue.ub.api;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.json.XML;
import org.mycore.common.content.MCRContent;
import org.mycore.common.content.MCRStringContent;
import org.mycore.common.content.transformer.MCRXSL2XMLTransformer;

import unidue.ub.api.connectors.CrossRefConnector;

public class MCRCrossRefResolver implements URIResolver {

    private final static Logger LOGGER = Logger.getLogger(MCRScopusDOIresolver.class);

        /**
         * Reads document description from the CrossRef API.
         *
         * 
         *   @author Eike Spielberg      
         *
         */

        public Source resolve(String href, String base) throws TransformerException {
            String id = href.substring(href.indexOf(":") + 1);
            MCRContent content = null;
            CrossRefConnector connection = new CrossRefConnector();
            try {
                MCRContent crossRefExport = connection.getPublicationByDOI(id);
                content = new MCRStringContent(XML.toString(new JSONObject(crossRefExport.asString()), "content"));
                LOGGER.debug("Reading MCRContent with DOI " + id);
                MCRXSL2XMLTransformer transformer = new MCRXSL2XMLTransformer("xsl/CrossRef2mods.xsl");
                MCRContent mods = transformer.transform(content);
                connection.close();
                return mods.getSource();
            } catch (Exception e) {
                throw new TransformerException(e);
            }
        }
    }
