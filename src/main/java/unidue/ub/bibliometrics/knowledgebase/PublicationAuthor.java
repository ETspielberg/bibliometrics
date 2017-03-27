package unidue.ub.bibliometrics.knowledgebase;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.jdom2.Element;

@Entity
@XmlRootElement(name="publicationAuthor")
public class PublicationAuthor {

    @Id
    @GeneratedValue (strategy = GenerationType.AUTO)
    private Long id;

    @XmlAttribute(name="surname")
    private String surname;

    @XmlAttribute(name="firstname")
    private String firstname;

    @XmlAttribute(name="middlename")
    private String middlename;

    @XmlAttribute(name="institution")
    private String institution;

    @XmlAttribute(name="department")
    private String department;

    @XmlAttribute(name="address")
    private String address;

    @XmlAttribute(name="scopusAuthorID")
    private String scopusAuthorID;

    @XmlAttribute(name="hIndex")
    private String hIndex;

    @XmlAttribute(name="orcid")
    private String orcid;

    @XmlAttribute(name="lsfID")
    private String lsfID;

    @XmlAttribute(name="researcherID")
    private String researcherID;

    @XmlAttribute(name="gnd")
    private String gnd;

    @XmlAttribute(name="researchGate")
    private boolean researchGate;

    @XmlAttribute(name="numberOfPublicationsUniBib")
    private int numberOfPublicationsUniBib;

    @XmlAttribute(name="numberOfPublicationsScopus")
    private int numberOfPublicationsScopus;

    @XmlAttribute(name="numberOfPublicationsWoS")
    private int numberOfPublicationsWoS;

    @XmlAttribute(name="linkGoogleProfile")
    private String linkGoogleProfile;

    @XmlAttribute(name="linkHomepage")
    private String linkHomepage;

    @Lob
    @XmlAttribute(name="foundInDatabase")
    private String foundInDatabase;

    @Lob
    @XmlAttribute(name="comment")
    private String comment;

    @Lob
    @XmlAttribute(name="keywords")
    private String keywords;

    public PublicationAuthor() {
        gnd = "";
        orcid = "";
        researcherID = "";
        scopusAuthorID = "";
        surname = "";
        firstname = "";
        institution = "";
        department = "";
        address = "";
        middlename = "";
        hIndex = "0";
        researchGate = false;
        numberOfPublicationsUniBib = 0;
        numberOfPublicationsScopus = 0;
        numberOfPublicationsWoS = 0;
        linkGoogleProfile = "";
        linkHomepage = "";
        foundInDatabase = "";
        comment = "";
        keywords = "";
        lsfID = "";
    }

    /**
     * @return the department
     */
    public String getDepartment() {
        return department;
    }

    /**
     * @return the address
     */
    public String getAddress() {
        return address;
    }

    /**
     * @return the researchGate
     */
    public boolean isResearchGate() {
        return researchGate;
    }

    /**
     * @return the numberOfPublicationsUniBib
     */
    public int getNumberOfPublicationsUniBib() {
        return numberOfPublicationsUniBib;
    }

    /**
     * @return the numberOfPublicationsScopus
     */
    public int getNumberOfPublicationsScopus() {
        return numberOfPublicationsScopus;
    }

    /**
     * @return the numberOfPublicationsWoS
     */
    public int getNumberOfPublicationsWoS() {
        return numberOfPublicationsWoS;
    }

    /**
     * @return the linkGoogleProfile
     */
    public String getLinkGoogleProfile() {
        return linkGoogleProfile;
    }

    /**
     * @return the linkHomepage
     */
    public String getLinkHomepage() {
        return linkHomepage;
    }

    /**
     * @return the foundInDatabase
     */
    public String getFoundInDatabase() {
        return foundInDatabase;
    }

    /**
     * @return the comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * @return the keywords
     */
    public String getKeywords() {
        return keywords;
    }

    /**
     * @param department the department to set
     */
    public void setDepartment(String department) {
        this.department = department;
    }

    /**
     * @param address the address to set
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * @param researchGate the researchGate to set
     */
    public void setResearchGate(boolean researchGate) {
        this.researchGate = researchGate;
    }

    /**
     * @param numberOfPublicationsUniBib the numberOfPublicationsUniBib to set
     */
    public void setNumberOfPublicationsUniBib(int numberOfPublicationsUniBib) {
        this.numberOfPublicationsUniBib = numberOfPublicationsUniBib;
    }

    /**
     * @param numberOfPublicationsScopus the numberOfPublicationsScopus to set
     */
    public void setNumberOfPublicationsScopus(int numberOfPublicationsScopus) {
        this.numberOfPublicationsScopus = numberOfPublicationsScopus;
    }

    /**
     * @param numberOfPublicationsWoS the numberOfPublicationsWoS to set
     */
    public void setNumberOfPublicationsWoS(int numberOfPublicationsWoS) {
        this.numberOfPublicationsWoS = numberOfPublicationsWoS;
    }

    /**
     * @param linkGoogleProfile the linkGoogleProfile to set
     */
    public void setLinkGoogleProfile(String linkGoogleProfile) {
        this.linkGoogleProfile = linkGoogleProfile;
    }

    /**
     * @param linkHomepage the linkHomepage to set
     */
    public void setLinkHomepage(String linkHomepage) {
        this.linkHomepage = linkHomepage;
    }

    /**
     * @param foundInDatabase the foundInDatabase to set
     */
    public void setFoundInDatabase(String foundInDatabase) {
        this.foundInDatabase = foundInDatabase;
    }

    /**
     * @param comment the comment to set
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * @param keywords the keywords to set
     */
    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getName() {
        return surname + ", " + firstname;
    }

    public String getSurname() {
        return surname;
    }

    public String getGnd() {
        return gnd;
    }

    public PublicationAuthor setGnd(String gnd) {
        this.gnd = gnd;
        return this;
    }

    public PublicationAuthor setSurname(String surname) {
        this.surname = surname;
        return this;
    }

    public String getFirstname() {
        return firstname;
    }

    public PublicationAuthor setFirstname(String firstname) {
        this.firstname = firstname;
        return this;
    }

    public String getMiddlename() {
        return middlename;
    }

    public PublicationAuthor setMiddlename(String middlename) {
        this.middlename = middlename;
        return this;
    }

    public String getInstitution() {
        return institution;
    }

    public PublicationAuthor setInstitution(String institution) {
        this.institution = institution;
        return this;
    }

    public String getScopusAuthorID() {
        return scopusAuthorID;
    }

    public void setScopusAuthorID(String scopusAuthorID) {
        this.scopusAuthorID = scopusAuthorID;
    }

    public String gethIndex() {
        return hIndex;
    }

    public void sethIndex(String hIndex) {
        this.hIndex = hIndex;
    }

    public String getOrcid() {
        return orcid;
    }

    public void setOrcid(String orcid) {
        this.orcid = orcid;
    }

    public String getLsfID() {
        return lsfID;
    }

    public void setLsfID(String lsfID) {
        this.lsfID = lsfID;
    }

    public String getResearcherID() {
        return researcherID;
    }

    public void setResearcherID(String researcherID) {
        this.researcherID = researcherID;
    }

    public PublicationAuthor(String surname, String firstname, String institution) {
        this.surname = surname;
        this.firstname = firstname;
        this.institution = institution;
    }
    
    public String getAuthorName() {
    	String name = (middlename.isEmpty()) ? firstname + " " + surname : firstname + " " + middlename + " " + surname;
    	return name;
    }

    public void addToOutput(Element output) {
        Element authorElement = new Element("author");
        authorElement.addContent((new Element("surname")).addContent(surname));
        authorElement.addContent((new Element("middlename")).addContent(middlename));
        authorElement.addContent((new Element("firstname")).addContent(firstname));
        
        String name = (middlename.isEmpty()) ? firstname + " " + surname : firstname + " " + middlename + " " + surname;
        authorElement.setAttribute("name", name);
        
        authorElement.addContent((new Element("institution")).addContent(institution));
        authorElement.addContent((new Element("department")).addContent(department));
        authorElement.addContent((new Element("address")).addContent(address));
        authorElement.addContent((new Element("scopusAuthorID")).addContent(scopusAuthorID));
        authorElement.addContent((new Element("ResearcherID")).addContent(researcherID));
        authorElement.addContent((new Element("orcid")).addContent(orcid));
        authorElement.addContent((new Element("gnd")).addContent(gnd));
        authorElement.addContent((new Element("lsfID")).addContent(lsfID));
        authorElement.addContent((new Element("hIndex")).addContent(hIndex));
        authorElement.addContent((new Element("researchGate")).addContent(String.valueOf(researchGate)));
        authorElement.addContent((new Element("numberOfPublicationsUniBib")).addContent(String.valueOf(numberOfPublicationsUniBib)));
        authorElement.addContent((new Element("numberOfPublicationsScopus")).addContent(String.valueOf(numberOfPublicationsScopus)));
        authorElement.addContent((new Element("numberOfPublicationsWoS")).addContent(String.valueOf(numberOfPublicationsWoS)));
        authorElement.addContent((new Element("linkGoogleProfile")).addContent(String.valueOf(linkGoogleProfile)));
        authorElement.addContent((new Element("linkHomepage")).addContent(String.valueOf(linkHomepage)));
        authorElement.addContent((new Element("foundInDatabase")).addContent(String.valueOf(foundInDatabase)));
        output.addContent(authorElement);
    }

}
