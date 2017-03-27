package unidue.ub.bibliometrics.knowledgebase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.httpclient.HttpException;
import org.apache.log4j.Logger;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.json.JSONArray;
import org.mycore.common.content.transformer.MCRXSL2XMLTransformer;
import org.xml.sax.SAXException;

import unidue.ub.api.connectors.GNDConnector;
import unidue.ub.api.connectors.LobidConnector;
import unidue.ub.api.connectors.OrcidConnector;
import unidue.ub.api.connectors.ScopusConnector;

public class AuthorBuilder {

    private final static String toBeChecked = "to be checked";
    
    private final static String DC_URL = "http://purl.org/dc/elements/1.1/";
    
    private final static Namespace DC_NS =  Namespace.getNamespace("", DC_URL);
    
    private static final Logger LOGGER = Logger.getLogger(AuthorBuilder.class);
    
    private PublicationAuthor author;
    
    private String typeOfID = "";
    
    /**
     * Initializes the <code>AuthorBuilder</code> with a incomplete author
     * @param author the author to be build
     */
    public AuthorBuilder(PublicationAuthor author) {
        this.author = author;
    }
    
    /**
     * Initializes the <code>AuthorBuilder</code> by creating a new author
     */
    public AuthorBuilder() {
        this.author = new PublicationAuthor();
    }



    /**
     * gets an author from the database by determining the type of the provided id. if no author is present it builds one from the id.
     * @param id the author identifier
     * @return the author retrieved from the database or build with the identifier
	 * @throws JDOMException thrown upon parsing the source response
	 * @throws IOException thrown upon reading profiles from disc
	 * @throws SAXException thrown when parsing the files from disc
	  */
    public PublicationAuthor retrieveAuthor(String id) throws JDOMException, IOException, SAXException {
        typeOfID = determineID(id);
        LOGGER.info("given ID: " + id + " is of type " + typeOfID);
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("publicationAuthors");
        EntityManager em = emf.createEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<PublicationAuthor> q = cb.createQuery(PublicationAuthor.class);
        Root<PublicationAuthor> c = q.from(PublicationAuthor.class);
        List<Predicate> predicates = new ArrayList<>();
        if (typeOfID.equals("surname")) {
            if (id.contains(",")) {
                predicates.add(cb.equal(c.get("surname"),id.substring(0,id.indexOf(","))));
                predicates.add(cb.equal(c.get("firstname"),id.substring(id.indexOf(",")+1)));
                LOGGER.info("retriving surname, firstname from database for " + id);
            } else if (id.contains(" ")) {
                predicates.add(cb.equal(c.get("firstname"),id.substring(0,id.indexOf(" "))));
                predicates.add(cb.equal(c.get("surname"),id.substring(id.indexOf(" ")+1)));
                LOGGER.info("retrieving firstname surname from database for " + id);
            } else {
                predicates.add(cb.equal(c.get("surname"), id));
                LOGGER.info("retrieving surname from database for " + id);
            }
        }
        predicates.add(cb.equal(c.get(typeOfID), id));
        q.select(c).where(cb.equal(c.get(typeOfID), id));
        TypedQuery<PublicationAuthor> query = em.createQuery(q);
        List<PublicationAuthor> authors = query.getResultList();
        em.close();
        if (authors.size() == 1) {
            LOGGER.info("found author in database");
            this.author = authors.get(0);
            return author;
        }
        LOGGER.info("no match in database");
        return buildAuthor(id);
    }
    
    /**
     * determines the type of the provided id
     * @param id the id to be determined
     * @return the type of id: "orcid", "surname", "scopusAuthorID", "lsfID
     */
    public static String determineID(String id) {
        Pattern p = Pattern.compile("\\d+");
        Matcher m = p.matcher(id);
        
        if (id.contains("-")) {
            id = id.replace("-", "");
            if (!m.matches())
                return "surname";
            else
                return "orcid";
        } else {
            if (id.contains(" ")) id = id.replace(" ", "");
            if (id.contains(".")) id = id.replace(".", "");
        }
        if (!m.find())
            return "surname";
        else if (id.length() == 16)
            return "orcid";
        else if (id.length() == 5)
            return "lsfID";
        else if (id.length() > 5 || id.length() < 16)
            return "scopusAuthorID";
        else
            return "";
    }
    
    private PublicationAuthor buildAuthor(String id) throws JDOMException, IOException, SAXException {
        if (typeOfID.equals("scopusAuthorID")) {
            ScopusConnector scopusConnection = new ScopusConnector();
            Element authorInfo = (new MCRXSL2XMLTransformer("xsl/scopusAuthor2PublicationAuthor.xsl")).transform(scopusConnection.getAuthorProfile(id)).asXML().detachRootElement().clone();
            buildFromAuthorInfo(authorInfo);
            retrieveGND();
        } else if (typeOfID.equals("orcid")) {
            OrcidConnector connection = new OrcidConnector();
            Element authorInfo = (new MCRXSL2XMLTransformer("xsl/orcidAuthor2PublicationAuthor.xsl")).transform(connection.getPublicationsByOrcid(id)).asXML().detachRootElement().clone();
            buildFromAuthorInfo(authorInfo);
        } else if (typeOfID.equals("gnd")) {
            GNDConnector connection = new GNDConnector();
            Element authorInfo = (new MCRXSL2XMLTransformer("xsl/gnd2PublicationAuthor.xsl")).transform(connection.getGND(author)).asXML().detachRootElement().clone();
            buildFromAuthorInfo(authorInfo);
        } else if (typeOfID.equals("surname")) {           
                if (id.contains(",")) {
                author.setSurname(id.substring(0,id.indexOf(",")).trim());
                author.setFirstname(id.substring(id.indexOf(",")+1).trim());
                } else if (id.contains(" ")) {
                    author.setFirstname(id.substring(0,id.indexOf(" ")).trim());
                    author.setSurname(id.substring(id.indexOf(" ")+1).trim());
                } else author.setSurname(id);
            retrieveScopusAuthorID();
            retrieveGND();
            if (!author.getScopusAuthorID().equals(toBeChecked)) {
                ScopusConnector scopusConnection = new ScopusConnector();
                Element authorInfo = (new MCRXSL2XMLTransformer("xsl/scopusAuthor2PublicationAuthor.xsl")).transform(scopusConnection.getAuthorProfile(author.getScopusAuthorID())).asXML().detachRootElement().clone();
                buildFromAuthorInfo(authorInfo);
                retrieveGND();
            } /*else if (!author.getGnd().equals(toBeChecked)){
                GNDConnector connection = new GNDConnector();
                Element authorInfo = (new MCRXSL2XMLTransformer("xsl/gnd2PublicationAuthor.xsl")).transform(connection.getGND(author)).asXML().detachRootElement().clone();
                buildFromAuthorInfo(authorInfo);
            }*/
           
            
        }
        PublicationAuthorDAO.persistAuthor(author);
        return author;
    }
    
    /**
     * retrieves the ScopusAuthorID for an author and puts it into the <code>PublicationAuthor</code> object
     * @throws HttpException thrown upon connecting to the source
	 * @throws JDOMException thrown upon parsing the source response
	 * @throws IOException thrown upon reading profiles from disc
	 * @throws SAXException thrown when parsing the files from disc
	 */
    public void retrieveScopusAuthorID() throws HttpException, JDOMException, IOException, SAXException {
        ScopusConnector connection = new ScopusConnector();
        Element result = connection.retrieveScopusAuthorID(author).asXML().detachRootElement().clone();
        
        List<String> allIDs = new ArrayList<>();
        for (Element child : result.getChildren()) {
            if (result.getName().equals("error")) continue;
            if (child.getName().equals("entry")) {
                Element identifier = child.getChild("identifier",DC_NS);
                String value = identifier.getValue().replace("AUTHOR_ID:", "");
                allIDs.add(value);
            }
        }
        if (allIDs.size() == 1) {
            author.setScopusAuthorID(allIDs.get(0));
            LOGGER.info("found ScopusID: " + author.getScopusAuthorID());
        } else
            author.setScopusAuthorID(toBeChecked);
    }

    /**
     * retrieves the h-Index for an author and puts it into the <code>PublicationAuthor</code> object
     * @throws HttpException thrown upon connecting to the source
	 * @throws JDOMException thrown upon parsing the source response
	 * @throws IOException thrown upon reading profiles from disc
	 * @throws SAXException thrown when parsing the files from disc
	 */
    public void retrieveScopusHIndex() throws HttpException, JDOMException, IOException, SAXException {
        ScopusConnector connection = new ScopusConnector();
        if (!author.getScopusAuthorID().equals(toBeChecked)) {
            Element authorProfile = connection.getAuthorProfile(author.getScopusAuthorID()).asXML().detachRootElement().clone();
            try {
            author.sethIndex(authorProfile.getChild("h-index").getValue());
            } catch (Exception e) {
            }
        } else {
            author.sethIndex(toBeChecked);
        }
    }
    
    private PublicationAuthor buildFromAuthorInfo(Element authorInfo) {
        author.setFirstname(authorInfo.getChildText("given-name"));
        author.setSurname(authorInfo.getChildText("surname"));
        if (authorInfo.getChildText("scopusAuthorID") != null) author.setScopusAuthorID(authorInfo.getChildText("scopusAuthorID"));
        if (authorInfo.getChildText("affiliation") != null) author.setInstitution(authorInfo.getChildText("affiliation"));
        if (authorInfo.getChildText("orcid") != null) author.setOrcid(authorInfo.getChildText("orcid"));
        if (authorInfo.getChildText("researcherID") != null) author.setResearcherID(authorInfo.getChildText("researcherID"));
        if (authorInfo.getChildText("gnd") != null) author.setGnd(authorInfo.getChildText("gnd"));
        return author;
    }

    public String createCSVLine() {
        return author.getSurname() + "," + author.getFirstname() + "," + author.getInstitution() + "," + author.getScopusAuthorID() + "," + author.gethIndex() + "\n";
    }

    public static String createCSVHeader() {
        return "name,surname,insitution,scopusAuthorID,hIndex \n";
    }
    
    /**
     * retrieves the GND-ID for an author and puts it into the <code>PublicationAuthor</code> object. if no ID or more than one are returned, it is set to "to be checked".
     * @throws HttpException thrown upon connecting to the source
	 * @throws JDOMException thrown upon parsing the source response
	 * @throws IOException thrown upon reading profiles from disc
	 * @throws SAXException thrown when parsing the files from disc
	 */
    public void retrieveGND() throws HttpException, JDOMException, IOException, SAXException {
        LobidConnector connection = new LobidConnector();
        String answer = connection.retrieveGND(author).asString();
        JSONArray result = new JSONArray(answer);
        if (result.length() == 1) {
            author.setGnd(result.getJSONObject(0).getString("value").replace("http://d-nb.info/gnd/", ""));
            LOGGER.info("found GND: " + author.getGnd());
        } else
            author.setGnd(toBeChecked);
    }
}
