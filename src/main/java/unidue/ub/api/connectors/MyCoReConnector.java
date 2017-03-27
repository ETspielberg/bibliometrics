package unidue.ub.api.connectors;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.filter.Filters;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import org.mycore.common.MCRConstants;
import org.mycore.common.config.MCRConfiguration;
import org.mycore.common.content.MCRContent;
import org.mycore.common.content.MCRStreamContent;
import org.xml.sax.SAXException;

import unidue.ub.bibliometrics.knowledgebase.PublicationAuthor;

public class MyCoReConnector implements Connector {
	
    private static final Logger LOGGER = Logger.getLogger(MyCoReConnector.class);

	private final static String API_URL;
	
	static {
        //get parameters from mycore.properties
        MCRConfiguration config = MCRConfiguration.instance();
        API_URL = config.getString("MCR.api.MyCoReURI");
    }
	
	private GetMethod connection;
	
	public MyCoReConnector() {
    }
	
	public Set<Element> getPublicationsForAuthor(PublicationAuthor author) throws IOException, JDOMException, SAXException {
		List<String> mcrIDs = getMCRIDs(author);
		Set<Element> publications = new HashSet<>();
		for (String mcrID : mcrIDs) {
			publications.add(getPublicationByID(mcrID));
		}
		
		return publications;
	}
	
	public Element getPublicationByID(String id) throws IOException, JDOMException, SAXException {
        //build API URL
        String queryURL = API_URL + "objects/" + id;
        String pathToMODS = "metadata/def.modsContainer/modsContainer/mods:mods";
        XPathExpression<Element> xPath = XPathFactory.instance().compile(pathToMODS, Filters.element(),null,MCRConstants.getStandardNamespaces());
        return xPath.evaluateFirst(getResponse(queryURL).asXML().detachRootElement().clone());
    }
	
	private List<String> getMCRIDs(PublicationAuthor author) throws IOException, JDOMException, SAXException {
        //prepare list of results blocks 
        String query = prepareAuthorQuery(author);

        //divide API request in blocks a 100 documents 
        int numberOfPublications = getNumberOfPublications(query);

        String pathToID = "result/doc/str[@name='id']";
       // String pathToID = "lst[@name='responseHeader']";
        
        XPathExpression<Element> xPath = XPathFactory.instance().compile(pathToID, Filters.element());
                
        List<String> foundMCRIds = new ArrayList<>();
        //at the moment only testing, two blocks a 10 documents
        for (int i = 0; i < ( (numberOfPublications / 100) + 1); i++) {
            int start = 100 * i;
                        //build API URL
            String queryURL = query + "&rows=100&start=" + start;
            Element response = getResponse(queryURL).asXML().detachRootElement().clone();
      
            List<Element> foundMCRIDElements = xPath.evaluate(response);
            LOGGER.info("found " + foundMCRIDElements.size() + " mods elements");
            for (Element mcrIDElement : foundMCRIDElements) {
            	String mcrID = mcrIDElement.getValue();
            	if (!mcrID.contains("-"))
            		foundMCRIds.add(mcrID);
            }
        }
        LOGGER.info("retrieved " + foundMCRIds.size() + " entries from MyCoRe repository.");
        //return API-response
        return foundMCRIds;
    }
	
	public int getNumberOfPublications(PublicationAuthor author) throws JDOMException, IOException, SAXException {
	    String authorQuery = prepareAuthorQuery(author);
	    LOGGER.info(authorQuery);
	    return getNumberOfPublications(authorQuery);
	}
	
	private String prepareAuthorQuery(PublicationAuthor author) {
	    String query = API_URL + "search?q=mods.name:" + author.getSurname();
	    /*
	    if (!author.getOrcid().isEmpty())
	        query += " OR mods.nameIdentifier:"+author.getOrcid();
	    if (!author.getScopusAuthorID().isEmpty())
	        query += " OR mods.nameIdentifier:"+author.getScopusAuthorID();
	    if (!author.getResearcherID().isEmpty())
	        query += " OR mods.nameIdentifier:"+author.getResearcherID();
	    if (!author.getLsfID().isEmpty())
	        query += " OR mods.nameIdentifier:"+author.getLsfID();
	        */
	    return query;
	}
	
	private int getNumberOfPublications(String queryURL) throws JDOMException, IOException, SAXException {
        //build API URL

        //get response as XML-file
        Document response = getResponse(queryURL).asXML();
        
        int numberOfPublications  =0;

        //read and return total number of publications from XML-file
        try {
        	numberOfPublications = Integer.parseInt(response.getRootElement().getChild("result").getAttributeValue("numFound"));
        } catch (Exception e) {
        	LOGGER.info("found no documents in repository");
        }
        return numberOfPublications;
    }
	
	private MCRContent getResponse(String queryURL) throws IOException {
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

}
