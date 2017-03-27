package unidue.ub.bibliometrics;

import java.io.File;
import java.io.IOException;

import org.apache.commons.httpclient.HttpException;
import org.apache.log4j.Logger;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import org.mycore.common.config.MCRConfiguration;
import org.mycore.common.content.MCRJDOMContent;
import org.xml.sax.SAXException;

import unidue.ub.api.connectors.GoogleMapsConnector;
import unidue.ub.api.connectors.ScopusConnector;

public class InstitutionBuilder {

	private final static String affiliationsDir;

	private final static String pathToInstitutionInAffilResponse = "institution-profile/parent-preferred-name";

	private final static String pathToDepartmentnAffilResponse = "institution-profile/preferred-name";

	private final static String pathToAddressnAffilResponse = "institution-profile/address";

	private final static String pathToInstitutionInAuthorProfile = "ip-doc/parent-preferred-name";

	private final static String pathToDepartmentInAuthorProfile = "ip-doc/preferred-name";

	private final static String pathToAddressInAuthorProfile = "ip-doc/address";
	
	private final static String pathToLatitude = "geoCoordinates/latitude";
	
	private final static String pathToLongitude = "geoCoordinates/longitude";

	private final static Logger LOGGER = Logger.getLogger(InstitutionBuilder.class);

	static {
		MCRConfiguration config = MCRConfiguration.instance();
		affiliationsDir = config.getString("ub.bibliometrics.affiliationsDir");
	}

	public static Institution buildByAffilID(String id) {
		if (!id.isEmpty()) {
		Institution institution = new Institution(id);
		
		//prepare the file of exported data
		File outputFile = new File(affiliationsDir, "scopus_affiliationExport_" + id + ".xml");
		
		//get the affiliation export from scopus, either from a file on disk or from the api.
		Element affilData;
		try {
			affilData = new SAXBuilder().build(outputFile).detachRootElement().clone();
		} catch (JDOMException | IOException e) {
			ScopusConnector connector = new ScopusConnector();
			LOGGER.info("Retrieving AffilData for affilID" + id + " from Scopus.");
			try {
				affilData = connector.getAffiliationProfile(id).asXML().detachRootElement().clone();
			} catch (JDOMException | IOException | SAXException e1) {
				LOGGER.info("could not get scopus response.");
				affilData = new Element("error")
						.addContent(new Element("status").setText("could not get scopus response"));
			}
		}
		
		//if no errors occurred, build the institution from the obtained data.
		if (affilData.getChild("status") == null) {
			//build institution with the scopus data
			//if geo-coordinates are present, they have been transferred into the institution as well. 
			addDataFromScopusProfile(affilData, "full", institution);
		}
		return institution;
		} else return null;
	}

	public static Institution buildInstitutionsFromScopusShortProfile(Element affiliation) {
		String id = affiliation.getAttributeValue("affiliation-id");
		Institution institution = buildByAffilID(id);
		return institution;
	}

	private static void addDataFromScopusProfile(Element profile, String type, Institution institution) {
		XPathExpression<Element> xPathInstitution;
		XPathExpression<Element> xPathDepartment;
		XPathExpression<Element> xPathAdress;
		if (type.equals("short")) {
			xPathInstitution = XPathFactory.instance().compile(pathToInstitutionInAuthorProfile, Filters.element());
			xPathDepartment = XPathFactory.instance().compile(pathToDepartmentInAuthorProfile, Filters.element());
			xPathAdress = XPathFactory.instance().compile(pathToAddressInAuthorProfile, Filters.element());
		} else {
			xPathInstitution = XPathFactory.instance().compile(pathToInstitutionInAffilResponse, Filters.element());
			xPathDepartment = XPathFactory.instance().compile(pathToDepartmentnAffilResponse, Filters.element());
			xPathAdress = XPathFactory.instance().compile(pathToAddressnAffilResponse, Filters.element());
		}
		Element institutionElement = xPathInstitution.evaluateFirst(profile);
		Element departmentElement = xPathDepartment.evaluateFirst(profile);

		if (institutionElement != null) {
			institution.setDepartment(departmentElement.getText());
			institution.setInstitution(institutionElement.getText());
		} else if (departmentElement != null) {
			institution.setInstitution(departmentElement.getText());
		}

		Element addressElement = xPathAdress.evaluateFirst(profile);
		institution.setCity(addressElement.getChildText("city"));
		institution.setCountry(addressElement.getChildText("country"));
		if (profile.getChild("geoCoordinates") != null) {
			XPathExpression<Element> xPathLatitude = XPathFactory.instance().compile(pathToLatitude, Filters.element());
			XPathExpression<Element> xPathLongitude = XPathFactory.instance().compile(pathToLongitude, Filters.element());
			String textLatitude = xPathLatitude.evaluateFirst(profile).getText();
			String textLongitude = xPathLongitude.evaluateFirst(profile).getText();
			institution.setLatitude(Double.parseDouble(textLatitude));
			institution.setLongitude(Double.parseDouble(textLongitude));
		}
	}

	public static void addMercatorCorrdinates(Institution institution)
			throws HttpException, JDOMException, IOException, SAXException {
		GoogleMapsConnector connector = new GoogleMapsConnector();
		String locationQuery = institution.getCity() + ", " + institution.getCountry();
		Element geoData = connector.getGeoData(locationQuery);
		if (geoData != null) {
			institution.setLatitude(Double.parseDouble(geoData.getChildText("lat")));
			institution.setLongitude(Double.parseDouble(geoData.getChildText("lng")));
		} else
			LOGGER.info("could not get geo data.");
	}
	
	public static void saveEnrichedScopusExport(Institution institution) {
		File outputFile = new File(affiliationsDir, "scopus_affiliationExport_" + institution.getAffilID() + ".xml");
			try {
				Element affilData = new SAXBuilder().build(outputFile).detachRootElement().clone();
				Element coordinates = new Element("geoCoordinates");
				coordinates.addContent((new Element("longitude")).addContent(String.valueOf(institution.getLongitude())));
				coordinates.addContent((new Element("latitude")).addContent(String.valueOf(institution.getLatitude())));
				affilData.addContent(coordinates);
				(new MCRJDOMContent(affilData)).sendTo(outputFile);
			} catch (JDOMException | IOException e) {
				LOGGER.info("could not add geo-coordinates to scopus export.");
			}
	}

}
