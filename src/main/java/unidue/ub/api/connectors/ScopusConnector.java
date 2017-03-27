package unidue.ub.api.connectors;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.filter.Filters;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import org.mycore.common.config.MCRConfiguration;
import org.mycore.common.MCRConstants;
import org.mycore.common.content.MCRContent;
import org.mycore.common.content.MCRStreamContent;
import org.xml.sax.SAXException;

import unidue.ub.bibliometrics.knowledgebase.PublicationAuthor;

public class ScopusConnector implements Connector, CitationGetter {

	private final static String API_URL;

	private final static String API_KEY;

	private final static String AFFIL_ID;

	private final Namespace MODS_NS = MCRConstants.MODS_NAMESPACE;

	private static final Logger LOGGER = Logger.getLogger(ScopusConnector.class);

	private final static Namespace ELSEVIER_Namespace = Namespace.getNamespace("",
			"http://www.elsevier.com/xml/svapi/abstract/dtd");
	
	private final static String pathToArticleLink = "document/link[@rel='scopus']";

	private final static String pathToCitationLink = "document/link[@rel='scopus-citedby']";

	private final static String pathToCitationCount = "document/citation-count";

	private final static String pathToNumber = "coredata/document-count";

	private final static String pathToDocumentIdentifier = "documents/abstract-document/dc:identifier";

	public GetMethod connection;

	static {
		// get parameters from mycore.properties
		MCRConfiguration config = MCRConfiguration.instance();
		API_URL = config.getString("MCR.api.ScopusURI");
		API_KEY = config.getString("MCR.api.ScopusAPI");
		AFFIL_ID = config.getString("MCR.api.ScopusAffilID");
	}

	public int getNumberOfPublications(PublicationAuthor author)
			throws JDOMException, IOException, SAXException {
		if (!author.getScopusAuthorID().isEmpty()) {
			String queryURL = API_URL + "/author/AUTHOR_ID:" + author.getScopusAuthorID()
					+ "?start=0&count=200&view=DOCUMENTS&apikey=" + API_KEY;
			XPathExpression<Element> xPath = XPathFactory.instance().compile(pathToNumber, Filters.element());
			Element numberElement = xPath.evaluateFirst(getResponse(queryURL).asXML().detachRootElement().clone());

			// read and return total number of publications from the Element
			return Integer.parseInt(numberElement.getValue());
		} else
			return 0;
	}

	public Set<Element> getPublicationsForAuthor(PublicationAuthor author)
			throws IOException, JDOMException, SAXException {
		if (!author.getScopusAuthorID().isEmpty()) {
			Set<Element> publications = new HashSet<>();
			String queryURL = API_URL + "/author/AUTHOR_ID:" + author.getScopusAuthorID()
					+ "?start=0&count=200&view=DOCUMENTS&apikey=" + API_KEY;
			XPathExpression<Element> xPath = XPathFactory.instance().compile(pathToDocumentIdentifier,
					Filters.element());
			List<Element> identifierElements = xPath
					.evaluate(getResponse(queryURL).asXML().detachRootElement().clone());
			for (Element idElement : identifierElements) {
				publications.add(getPublicationByID(idElement.getValue()));
			}
			return publications;
		} else
			return null;
	}

	public int getNumberOfPublications(String queryURL) throws JDOMException, IOException, SAXException {
		// build API URL
		String queryURLNumbers = queryURL + "&start=0&count=5&view=DOCUMENTS";

		// get response as XML-file
		Document response = getResponse(queryURLNumbers).asXML();

		// read and rteturn total number of publications from XML-file
		return Integer
				.parseInt(response.getRootElement().getChild("documents").getAttributeValue("total"));
	}

	public int getCitationCount(String scopusID) throws IOException, JDOMException, SAXException {
		// build API URL
		String queryURL = API_URL + "/abstract/citation-count?scopus_id=" + scopusID + "&apikey=" + API_KEY
				+ "&httpAccept=application%2Fxml";
		XPathExpression<Element> xPath = XPathFactory.instance().compile(pathToCitationCount, Filters.element());
		Element citationCountElement = xPath.evaluateFirst(getResponse(queryURL).asXML());
		return Integer.parseInt(citationCountElement.getValue());
	}

	public Element getCitationInformation(String scopusID)
			throws IOException, JDOMException, SAXException {
		// build API URL
		String queryURL = API_URL + "/abstract/citation-count?scopus_id=" + scopusID + "&apikey=" + API_KEY
				+ "&httpAccept=application%2Fxml";
		XPathExpression<Element> xPathCount = XPathFactory.instance().compile(pathToCitationCount, Filters.element());
		XPathExpression<Element> xPathLink = XPathFactory.instance().compile(pathToCitationLink, Filters.element());
		XPathExpression<Element> xPathArticleLink = XPathFactory.instance().compile(pathToArticleLink, Filters.element());
		Element response = getResponse(queryURL).asXML().detachRootElement().clone();
		String citationCount = xPathCount.evaluateFirst(response).getValue();
		String citationLink = xPathLink.evaluateFirst(response).getAttributeValue("href");
		String articleLink = xPathArticleLink.evaluateFirst(response).getAttributeValue("href");
		Element citationInformation = new Element("citationInformation");
		citationInformation.addContent(new Element("count").setText(citationCount));
		citationInformation.addContent(new Element("citationLink").setText(citationLink));
		citationInformation.addContent(new Element("articleLink").setText(articleLink));
		return citationInformation;
	}

	public List<MCRContent> getAll() throws IOException, JDOMException, SAXException {
		// prepare list of results blocks
		List<MCRContent> resultsSet = new ArrayList<>();
		// build basic part of API URL
		String baseQueryURL = API_URL + "/affiliation/AFFILIATION_ID:" + AFFIL_ID + "?apikey=" + API_KEY
				+ "&view=DOCUMENTS";

		// divide API request in blocks a XXX documents (in final version 200
		// documents per block, number of blocks determined by total number of
		// documents)
		// int numberOfPublications = getNumberOfPublications(baseQueryURL);
		// at the moment only testing, two blocks a 10 documents
		for (int i = 0; i < 2; i++) {
			int start = 10 * i;
			int count = 10;

			// build API URL
			String queryURL = baseQueryURL + "&start=" + start + "&count=" + count + "&view=DOCUMENTS";

			// add block to list of blocks
			resultsSet.add(getResponse(queryURL));
		}
		// return API-response
		return resultsSet;
	}

	public MCRContent getUpdate() throws IOException {
		String dayToQuery = LocalDate.now().minusDays(7).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		// build API URL
		String queryURL = API_URL + "/search/scopus?httpAccept=application/xml&apikey=" + API_KEY + "&query=AF-ID("
				+ AFFIL_ID + ")+AND+dateloaded(" + dayToQuery + ")";
		return getResponse(queryURL);
	}

	public MCRContent getAuthorProfile(String scopusID) throws IOException {
		// build API URL
		String queryURL = API_URL + "/author/AUTHOR_ID:" + scopusID + "?view=enhanced&apikey=" + API_KEY;
		LOGGER.info("retrived author profile for authorID" + scopusID);

		// return API-response
		return getResponse(queryURL);
	}

	public MCRContent getAffiliationProfile(String scopusID) throws IOException {
		// build API URL
		String queryURL = API_URL + "/affiliation/affiliation_id/" + scopusID + "?apikey=" + API_KEY;
		LOGGER.info("Retrieved affiliation profile for AffilID" + scopusID);

		// return API-response
		return getResponse(queryURL);
	}

	public MCRContent getAuthorPublications(String scopusID) throws IOException {
		// build API URL
		String queryURL = API_URL + "/author/AUTHOR_ID:" + scopusID + "?start=0&count=200&view=DOCUMENTS&apikey="
				+ API_KEY;

		// return API-response
		return getResponse(queryURL);
	}

	public MCRContent getContentByURL(String url) throws IOException {
		// build API URL
		String queryURL;
		if (url.contains("?"))
			queryURL = url + "&apikey=" + API_KEY;
		else
			queryURL = url + "?apikey=" + API_KEY;

		// return API-response
		return getResponse(queryURL);
	}

	public Document updateCitationCount(Document doc) throws JDOMException, SAXException {
		Document newDoc = new Document();
		// extract DOI
		String doi = "";
		Element rootElement = doc.getRootElement();
		List<Element> children = rootElement.getChildren("identifier", MODS_NS);
		for (Element element : children) {
			List<Attribute> attributes = element.getAttributes();
			for (Attribute attribute : attributes) {
				if ("type".equals(attribute.getName()) && "doi".equals(attribute.getValue())) {
					doi = element.getValue();
				}
			}
		}
		// get Doc with new citation count
		try {
			newDoc = getPublicationByDOI(doi).asXML();
		} catch (IOException ex) {

		}

		// extract new citation count
		Element newRootElement = newDoc.getRootElement();
		Element newChild = newRootElement.getChild("extension", MODS_NS).getChild("sourcetext")
				.getChild("abstracts-retrieval-response", ELSEVIER_Namespace).getChild("coredata", ELSEVIER_Namespace)
				.getChild("citedby-count", ELSEVIER_Namespace);
		String newCitationCount = newChild.getValue();

		// replace old citation count
		Element child = rootElement.getChild("extension", MODS_NS).getChild("sourcetext")
				.getChild("abstracts-retrieval-response", ELSEVIER_Namespace).getChild("coredata", ELSEVIER_Namespace)
				.getChild("citedby-count", ELSEVIER_Namespace);
		child.setText(newCitationCount);

		return doc;

	}

	public Element getPublicationByID(String id) throws JDOMException, IOException, SAXException {
		if (id.toLowerCase().startsWith("scopus")) {
			id = id.substring(id.indexOf(":"));
			return getPublicationByScopusID(id).asXML().detachRootElement().clone();
		} else if (id.startsWith("doi")) {
			id = id.substring(id.indexOf(":"));
			return getPublicationByDOI(id).asXML().detachRootElement().clone();
		} else if (id.startsWith("eid")) {
			id = id.substring(id.indexOf(":"));
			return getPublicationByEID(id).asXML().detachRootElement().clone();
		} else
			return null;
	}

	public MCRContent getPublicationByDOI(String doi) throws IOException {
		// build API URL
		String queryURL = API_URL + "/abstract/doi/" + doi + "?citedby-count" + "&apikey=" + API_KEY;

		// return API-response
		return getResponse(queryURL);
	}

	public MCRContent getPublicationByEID(String eid) throws IOException {
		// build API URL
		String queryURL = API_URL + "/abstract/eid/" + eid + "?apikey=" + API_KEY;

		// return API-response
		return getResponse(queryURL);
	}

	public MCRContent getPublicationByScopusID(String scopusID) throws IOException {
		// build API URL
		String queryURL = API_URL + "/abstract/scopus_id/" + scopusID + "?apikey=" + API_KEY;
		LOGGER.info("retrieved publication data for scopusID" + scopusID);

		// return API-response
		return getResponse(queryURL);
	}

	public MCRContent getResponse(String queryURL) throws IOException {
		// prepare http client
		HttpClient client = new HttpClient();

		// prepare GET-request
		connection = new GetMethod(queryURL);
		connection.setRequestHeader("Accept", "application/xml");

		// execute request, get response from API and return as stream
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

	public MCRContent retrieveScopusAuthorID(PublicationAuthor author)
			throws JDOMException, IOException, SAXException {
		String query = API_URL + "/search/author?query=";
		if (!author.getSurname().equals(""))
			query += "authlast(" + author.getSurname() + ")";
		if (!author.getFirstname().equals(""))
			query += "%20and%20authfirst(" + author.getFirstname() + ")";
		if (!author.getInstitution().equals(""))
			query += "%20and%20affil(" + author.getInstitution() + ")";
		query += ("&apikey=" + API_KEY).replace(" ", "+");
		LOGGER.info("asking " + query);
		return getResponse(query);
	}

	@Override
	public void finalize() {
		close();
	}
}
