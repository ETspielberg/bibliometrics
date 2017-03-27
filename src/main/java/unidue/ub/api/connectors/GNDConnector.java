package unidue.ub.api.connectors;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.jdom2.JDOMException;
import org.mycore.common.config.MCRConfiguration;
import org.mycore.common.content.MCRContent;
import org.mycore.common.content.MCRStreamContent;
import org.xml.sax.SAXException;

import unidue.ub.bibliometrics.knowledgebase.PublicationAuthor;

public class GNDConnector {
    
 public GetMethod connection;
    
    private final static String API_URL;
    
    static {
        //get parameters from mycore.properties
        MCRConfiguration config = MCRConfiguration.instance();
        API_URL = config.getString("MCR.api.GNDURI");
    }
    
    public GNDConnector() {
    }
    
    public MCRContent getGND(PublicationAuthor author) throws JDOMException, IOException, SAXException {
        String query = API_URL + author.getGnd();
        return getResponse(query);
    }
    
    public MCRContent getResponse(String queryURL) throws IOException {
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
