package unidue.ub.api.connectors;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.filter.Filters;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import org.mycore.common.config.MCRConfiguration;
import org.mycore.common.content.MCRContent;
import org.mycore.common.content.MCRStreamContent;
import org.xml.sax.SAXException;

public class GoogleMapsConnector {
	
	private final static String pathToGeoData = "result/geometry/location";

    private final static String API_URL;

    private final static String API_KEY;

    public GetMethod connection;
    
    private static final Logger LOGGER = Logger.getLogger(GoogleMapsConnector.class);

    static {
        //get parameters from mycore.properties
        MCRConfiguration config = MCRConfiguration.instance();
        API_URL = config.getString("MCR.api.GoogleURI");
        API_KEY = config.getString("MCR.api.GoogleAPI");
    }

    public GoogleMapsConnector() {
    }

    public Element getGeoData(String location) throws JDOMException, HttpException, IOException, SAXException {
        //build API URL
        String queryURL = API_URL + "?address="+ urlEncode(location.replace(" ", "+")) + "&key="+ API_KEY;
        LOGGER.info("Retrieving coordinates for " + location);
        
        Element response = getResponse(queryURL).asXML().detachRootElement().clone();
        
        XPathExpression<Element> xPathGeoData = XPathFactory.instance().compile(pathToGeoData,
				Filters.element());
        
        Element geodata = xPathGeoData.evaluateFirst(response);
        return geodata;
    }

    private MCRContent getResponse(String queryURL) throws HttpException, IOException {
        //prepare http client
        HttpClient client = new HttpClient();
        
        //prepare GET-request
        connection = new GetMethod(queryURL);

        //execute request, get response from API and return as stream
        client.executeMethod(connection);
        InputStream response = connection.getResponseBodyAsStream();
        return new MCRStreamContent(response);
    }

    public void close() {
        if (connection != null) {
            connection.releaseConnection();
            connection = null;
        }
    }

    @Override
    public void finalize() {
        close();
    }
    
    private static final String urlEncode(String str) {
        try {
            return URLEncoder.encode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
