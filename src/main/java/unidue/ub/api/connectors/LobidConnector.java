package unidue.ub.api.connectors;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;
import org.jdom2.JDOMException;
import org.mycore.common.config.MCRConfiguration;
import org.mycore.common.content.MCRContent;
import org.mycore.common.content.MCRStreamContent;
import org.xml.sax.SAXException;

import unidue.ub.bibliometrics.knowledgebase.PublicationAuthor;

public class LobidConnector {
    
    public GetMethod connection;
    
    private final static String API_URL;
    
    static {
        //get parameters from mycore.properties
        MCRConfiguration config = MCRConfiguration.instance();
        API_URL = config.getString("MCR.api.LobidURI");
    }
    
    private static final Logger LOGGER = Logger.getLogger(LobidConnector.class);
    
    public LobidConnector() {
        
    }
    
    public MCRContent retrieveGND(PublicationAuthor author) throws JDOMException, IOException, SAXException {
        String query = (API_URL + "/person?name=" + author.getFirstname() + " " + author.getSurname() + "&format=ids").replace(" ", "+");
        LOGGER.info("asking URL " + query);
        return getResponse(query);
    }
    
    public MCRStreamContent getResponse(String queryURL) throws IOException {
        //prepare http client
        HttpClient client = new HttpClient();

        //prepare GET-request
        connection = new GetMethod(queryURL);

        //execute request, get response from API and return as stream
        client.executeMethod(connection);
        InputStream response = connection.getResponseBodyAsStream();
        return new MCRStreamContent(response);
    }

}
