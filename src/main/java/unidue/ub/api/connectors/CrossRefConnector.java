package unidue.ub.api.connectors;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.mycore.common.config.MCRConfiguration;
import org.mycore.common.content.MCRContent;
import org.mycore.common.content.MCRStreamContent;

public class CrossRefConnector {

    private final static String API_URL;

    public GetMethod connection;

    static {
        //get Parameter from mycore.properties 
        MCRConfiguration config = MCRConfiguration.instance();
        API_URL = config.getString("MCR.api.CrossRefURI");
    }

    public CrossRefConnector() {
    }

    public MCRContent getPublicationByDOI(String doi) throws HttpException, IOException {
        //build API URL
        String queryURL = API_URL + doi;
        return getResponse(queryURL);
    }

    private MCRContent getResponse(String queryURL) throws HttpException, IOException {
        
        //prepare http-client
        HttpClient client = new HttpClient();
        
        //prepare http GET request
        connection = new GetMethod(queryURL);
        
        //execute request
        client.executeMethod(connection);
        
        //return input as stream
        InputStream response = connection.getResponseBodyAsStream();
        return new MCRStreamContent(response);
    }

    public void close() {
        if (connection != null) {
            connection.releaseConnection();
            connection = null;
        }
    }

}
