package unidue.ub.bibliometrics;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdom2.Element;
import org.jdom2.filter.Filters;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import org.joda.time.LocalDate;
import org.json.JSONArray;
import org.json.JSONObject;
import org.mycore.common.MCRConstants;

public class GeneralStatistics {
    
    private static final Pattern yearPattern = Pattern.compile("((19|20)\\d\\d)");
    
    private Set<Element> modsList;
    
    private int firstYear;
    
    private int lastYear;
    
    private int numberOfPublications;
    
    private Hashtable<Integer,Integer> publicationsPerYear;
    
    private Hashtable<String,Integer> typeOfPublications;
    
    private static final String pathToYear = "mods:originInfo/mods:dateIssued";
    
    private static final String pathToType = "mods:genre";
    
    
    public GeneralStatistics(Set<Element> modsList) {
    	this.modsList = modsList;
        firstYear = LocalDate.now().getYear();
        lastYear = LocalDate.now().getYear();
        numberOfPublications = 0;
        numberOfPublications = modsList.size();
        
        publicationsPerYear = new Hashtable<Integer,Integer>();
        typeOfPublications = new Hashtable<String,Integer>();
        
        for (Element mods : modsList) {
            XPathExpression<Element> yearPath = XPathFactory.instance().compile(pathToYear, Filters.element(),null, MCRConstants.getStandardNamespaces());
            Element numberDate = yearPath.evaluateFirst(mods);
            if (numberDate != null) {
            Matcher matcher = yearPattern.matcher(numberDate.getValue());
            matcher.find();
            int year = Integer.parseInt(matcher.group());
            if (year < firstYear)
                firstYear = year;
            if (year > lastYear)
                lastYear = year;
            if (publicationsPerYear.containsKey(year)) {
                Integer count = publicationsPerYear.get(year);
                count++;
                publicationsPerYear.replace(year, count);
            } else 
                publicationsPerYear.put(year, 1);
            }
           XPathExpression<Element> typePath = XPathFactory.instance().compile(pathToType, Filters.element(),null, MCRConstants.getStandardNamespaces());
           Element typeOfPublicationElement = typePath.evaluateFirst(mods);
           String typeOfPublication = typeOfPublicationElement.getAttributeValue("valueURI");
           if (typeOfPublication.contains("#"))
        	   typeOfPublication = typeOfPublication.substring(typeOfPublication.indexOf("#")+1);
           if (typeOfPublication != null) {
           if (typeOfPublications.containsKey(typeOfPublication)) {
               Integer count = typeOfPublications.get(typeOfPublication);
               count++;
               typeOfPublications.replace(typeOfPublication, count);
           } else
               typeOfPublications.put(typeOfPublication,1);
           }
        }
    }
    
    public int getNumberOfPublications() {
    	return numberOfPublications;
    }
    
    public Element prepareOutput() {
        Element statistics = new Element("generalStatistics");
        statistics.addContent(new Element("totalNumberOfPublications").setText(String.valueOf(numberOfPublications)));
        statistics.addContent(new Element("startYear").setText(String.valueOf(firstYear)));
        statistics.addContent(new Element("endYear").setText(String.valueOf(lastYear)));
        return statistics;
    }
    
    public Element preparePublicationList() {
    	Element pubList = new Element("mods:collection");
    	for (Element mods : modsList) {
    		if (mods.getChild("mods:extension") != null)
    			mods.removeChild("mods:extension");
    		pubList.addContent(mods);
    	}
    	return pubList;
    }
    
    public Hashtable<String,Integer> getPublicationsPerType() {
        return typeOfPublications;
    }
    
    public JSONArray getPublicationsPerYearAsJSON(){
        JSONArray data = new JSONArray();
        if (publicationsPerYear != null) {
        Enumeration<Integer> enumerator = publicationsPerYear.keys();
        while (enumerator.hasMoreElements()) {
        	JSONArray dataInd = new JSONArray();

            int year = enumerator.nextElement();
            int count = publicationsPerYear.get(year);
            dataInd.put(year).put(count);
            data.put(dataInd);
        }
        }
        return data;
    }

    public JSONArray getPublicationsPerTypeAsJSON(){
        JSONArray data = new JSONArray();
        if(typeOfPublications != null) {
        Enumeration<String> enumerator = typeOfPublications.keys();
        while (enumerator.hasMoreElements()) {
        	JSONObject typeJSON = new JSONObject();
            String type = enumerator.nextElement();
            int count = typeOfPublications.get(type);
            typeJSON.put("name",type).put("y", count);
            data.put(typeJSON);
        }
        }
        return data;
    }

}
