package unidue.ub.bibliometrics;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

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

import unidue.ub.api.connectors.CitationGetter;
import unidue.ub.api.connectors.ScopusConnector;

public class CitationStatistics {

    private Hashtable<String, Integer> citationsPerPublication;

    private String source;

    private int totalCitations;

    private int uncited;

    private int numberOfDocumentsWithCitationData;

    private int hIndex;
    
    private final static Namespace DC_Namespace = Namespace.getNamespace("dc", "http://purl.org/dc/elements/1.1/");
    
    private final static Namespace ELSEVIER_Namespace = Namespace.getNamespace("elsevier", "http://www.elsevier.com/xml/svapi/abstract/dtd");

    private final static String sourceIDScopus = "mods:extension/sourcetext[@type='scopus']/abstract-document/dc:identifier";
    
    private final static Logger LOGGER = Logger.getLogger(CitationStatistics.class);

    public CitationStatistics(Set<Element> mcrEntries, String source) throws HttpException, IOException, JDOMException, SAXException {
        this.source = source;
        totalCitations = 0;
        uncited = 0;
        numberOfDocumentsWithCitationData = 0;
        hIndex = 0;        
        
        List<Namespace> namespaces = MCRConstants.getStandardNamespaces();
        namespaces.add(DC_Namespace);
        namespaces.add(ELSEVIER_Namespace);
        List<Integer> citationCounts = new ArrayList<>();

        for (Element mcrEntry : mcrEntries) {
        	LOGGER.info(mcrEntry);
            XPathExpression<Element> xPath = XPathFactory.instance().compile(sourceIDScopus, Filters.element(),null, namespaces);
        	Element idenitiferElement = xPath.evaluateFirst(mcrEntry);
        	if (idenitiferElement != null) {
            String scopusID = xPath.evaluateFirst(mcrEntry).getValue();
            if (scopusID.contains("SCOPUS_ID:"))
            	scopusID = scopusID.replace("SCOPUS_ID:", "");
            CitationGetter getter = new ScopusConnector();
            Element citationInformation = getter.getCitationInformation(scopusID);
            if (citationInformation != null) {
                mcrEntry.addContent(citationInformation);
                int numberOfCitations = Integer.parseInt(citationInformation.getChildText("count"));
                citationCounts.add(numberOfCitations);
                LOGGER.info("retrieved citation count " + numberOfCitations + " for document with scopus id " + scopusID);
                if (numberOfCitations == 0)
                    uncited++;
                totalCitations += numberOfCitations;
                numberOfDocumentsWithCitationData++;
            }
        	}

	       }
        hIndex = calculateHIndex(citationCounts);
    }
    
    public int getHIndex() {
    	return hIndex;
    }

    public JSONObject getCitationsPerPublicationrAsJSON() {
        JSONObject series = new JSONObject();
        JSONArray categories = new JSONArray();
        JSONArray data = new JSONArray();
        Enumeration<String> enumerator = citationsPerPublication.keys();
        while (enumerator.hasMoreElements()) {
            String publication = enumerator.nextElement();
            categories.put(publication);
            int count = citationsPerPublication.get(publication);
            data.put(count);
        }
        series.put("data", data).put("categories", categories);
        return series;
    }

    public Hashtable<String, Integer> getCitationsPerPublication() {
        return citationsPerPublication;
    }

    public Element prepareOutput() {
        Element citationStatistics = new Element("citationStatistics");
        citationStatistics.setAttribute("source", source);
        citationStatistics.addContent(new Element("totalCitations").setText(String.valueOf(totalCitations)));
        citationStatistics.addContent(new Element("uncited").setText(String.valueOf(uncited)));
        citationStatistics.addContent(new Element("numberOfDocumentsWithCitationData").setText(String.valueOf(numberOfDocumentsWithCitationData)));
        citationStatistics.addContent(new Element("hIndex").setText(String.valueOf(hIndex)));
        return citationStatistics;
    }
    
    private int calculateHIndex(List<Integer> citations) {
		Collections.sort(citations, Collections.reverseOrder());
		int hIndex = 0;
		while (citations.get(hIndex) >= hIndex + 1 && hIndex < citations.size() - 1) {
			hIndex++;
		}
		return hIndex;
	}

}
