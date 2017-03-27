package unidue.ub.bibliometrics;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.httpclient.HttpException;
import org.apache.log4j.Logger;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.filter.Filters;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.mycore.common.MCRConstants;
import org.xml.sax.SAXException;

import unidue.ub.api.connectors.ScopusConnector;
import unidue.ub.bibliometrics.knowledgebase.PublicationAuthor;

/**
 * Analysis of the institutions where the authors of the publications are affiliated. The results are returned as geo-JSON objects wrapped in JdOM-Element.
 * @author Eike Spielberg
 *
 */
class AffiliationsStatistics {
	
	private final static Namespace DC_Namespace = Namespace.getNamespace("dc", "http://purl.org/dc/elements/1.1/");
	
	private final static Namespace ELSEVIER_Namespace = Namespace.getNamespace("scopus",
			"http://www.elsevier.com/xml/svapi/abstract/dtd");
    
	private Hashtable<String,Institution> actualHomeInstitution;
	
	private Hashtable<String,Institution> homeInstitutions;
	
	private Hashtable<String,Institution> partnerInstitutions;
	
	private final static Logger LOGGER = Logger.getLogger(AffiliationsStatistics.class);
	
	private final static String pathToActualHomeInstitution = "author-profile/affiliation-current/affiliation";
    
	private final static String pathToHomeInstitutions = "author-profile/affiliation-history/affiliation";
    
	private final static String pathToPartnerInstitution = "mods:extension/sourcetext[@type='scopus']/abstract-document/affiliation/afid";
    
	/**
	 * Builds an affiliation statistics object when fed with a lost of MODS entries, an author object and the type of information used to obtain the author information.
	 * 
	 * @param modsList a list of MODS entries
	 * @param author an author object
	 * @param source the source to be used
	 * @throws HttpException thrown upon connecting to the source
	 * @throws JDOMException thrown upon parsing the source response
	 * @throws IOException thrown upon reading profiles from disc
	 * @throws SAXException thrown when parsing the files from disc
	 */
	
	AffiliationsStatistics(Set<Element> modsList, PublicationAuthor author, String source) throws JDOMException, IOException, SAXException {
		//initialize list of institutions and cooperations
		homeInstitutions = new Hashtable<>();
		partnerInstitutions = new Hashtable<>();
		actualHomeInstitution = new Hashtable<>();
		
		//prepare necessary namespaces
		List<Namespace> namespaces = MCRConstants.getStandardNamespaces();
		namespaces.add(DC_Namespace);
		namespaces.add(ELSEVIER_Namespace);
		
		if (source.equals("scopus")) {
			//retrieve author profile for home institutions
			ScopusConnector scopusConnection = new unidue.ub.api.connectors.ScopusConnector();
			Element authorInfo = scopusConnection.getAuthorProfile(author.getScopusAuthorID()).asXML().detachRootElement().clone();
			LOGGER.info("Retrieved author profile from Scopus API");
			
			
			XPathExpression<Element> xPathActualHomeInstititution = XPathFactory.instance().compile(pathToActualHomeInstitution, Filters.element(),null,namespaces);
			List<Element> actualHomeInstitutionElements = xPathActualHomeInstititution.evaluate(authorInfo);
			for (Element actualHomeInstitutionElement : actualHomeInstitutionElements) {
				Institution institution = InstitutionBuilder.buildInstitutionsFromScopusShortProfile(actualHomeInstitutionElement);
				if (institution == null)
					continue;
				actualHomeInstitution.put(institution.getAffilID(),institution);
			}
				
			XPathExpression<Element> xPathHomeInstitutions = XPathFactory.instance().compile(pathToHomeInstitutions, Filters.element(),null,namespaces);
			List<Element> homeInstitutionElements = xPathHomeInstitutions.evaluate(authorInfo);
			for (Element homeInstitutionElement : homeInstitutionElements){
				Institution institution = InstitutionBuilder.buildInstitutionsFromScopusShortProfile(homeInstitutionElement);
				String key = institution.getAffilID();
				if (key == null)
					continue;
				if (homeInstitutions.contains(key))
					homeInstitutions.get(key).increaseCounter(1);
				else
					homeInstitutions.put(key,institution);
			}
			
			//work through list of mods entries to build the corresponding institutions from the entries
			
			XPathExpression<Element> xPathPartnerInstitutions = XPathFactory.instance().compile(pathToPartnerInstitution, Filters.element(),null,namespaces);
			for (Element mods : modsList) {
				List<Element> partnerInstitutionElements = xPathPartnerInstitutions.evaluate(mods);
				if (partnerInstitutionElements.isEmpty())
					continue;
				for (Element partnerInstitutionElement : partnerInstitutionElements) {
					String id = partnerInstitutionElement.getText();
					if (id.isEmpty())
						continue;
					if (homeInstitutions.contains(id))
						continue;
					if (partnerInstitutions.contains(id))
						partnerInstitutions.get(id).increaseCounter(1);
					else {
						Institution institution = InstitutionBuilder.buildByAffilID(id);
						partnerInstitutions.put(id, institution);
					}
				}
			}
		}
	}

	/**
	 * returns the JSON object used to display the home institutions as JDOM element.
	 * @return the element containing the JSON object
	 * @throws HttpException thrown upon connecting to the source
	 * @throws JDOMException thrown upon parsing the source response
	 * @throws IOException thrown upon reading profiles from disc
	 * @throws SAXException thrown when parsing the files from disc
	 */
	Element getHomeInstitutionsElement() throws JDOMException, IOException, SAXException {
		Element geoJSON = new Element("homeInstitutions");
		addJSONToElement(homeInstitutions, geoJSON);
		return geoJSON;
    }
	/**
	 * returns the JSON object used to display the actual home institutions as JDOM element
	 * @return the element containing the JSON object
	 * @throws HttpException thrown upon connecting to the source
	 * @throws JDOMException thrown upon parsing the source response
	 * @throws IOException thrown upon reading profiles from disc
	 * @throws SAXException thrown when parsing the files from disc
	 */
	Element getActualHomeInstitutionsElement() throws JDOMException, IOException, SAXException {
		Element geoJSON = new Element("acutalHomeInstitutions");
		addJSONToElement(actualHomeInstitution, geoJSON);
		return geoJSON;
    }
	
	/**
	 * returns the JDOM object used to display the partner institutions as JODM element
	 * @return the element conatining the JSON object
	 * @throws HttpException thrown upon connecting to the source
	 * @throws JDOMException thrown upon parsing the source response
	 * @throws IOException thrown upon reading profiles from disc
	 * @throws SAXException thrown when parsing the files from disc
	 */
	Element getPartnerInstitutionsElement() throws JDOMException, IOException, SAXException {
		Element geoJSON = new Element("partnerInstitutions");
		addJSONToElement(partnerInstitutions, geoJSON);
		return geoJSON;
    }
	
	private void addJSONToElement(Hashtable<String, Institution> institutions, Element parent) {
		JSONObject geoJSON = new JSONObject();
		JSONArray allGeoJSON = new JSONArray();
		if (institutions.size() > 0) {
		Iterator<Entry<String, Institution>> institutionIterator = institutions.entrySet().iterator();
		while (institutionIterator.hasNext()) {
			Institution institution = institutionIterator.next().getValue();
			try {
			if (institution.getLatitude() == 0) {
				InstitutionBuilder.addMercatorCorrdinates(institution);
				InstitutionBuilder.saveEnrichedScopusExport(institution);
			}
			allGeoJSON.put(institution.createGeoJsonPoint());
			} catch (Exception e) {
				LOGGER.info("could not set geo data");
			}
		}
		}
		geoJSON.put("features", allGeoJSON);
		geoJSON.put("type", "FeatureCollection");
		parent.addContent(geoJSON.toString());
	}
}
