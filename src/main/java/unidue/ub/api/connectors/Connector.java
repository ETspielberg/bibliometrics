package unidue.ub.api.connectors;

import java.io.IOException;
import java.util.Set;

import org.apache.commons.httpclient.HttpException;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.xml.sax.SAXException;

import unidue.ub.bibliometrics.knowledgebase.PublicationAuthor;

public interface Connector {
    
    Element getPublicationByID(String ID) throws IOException, JDOMException, SAXException;
    
    int getNumberOfPublications(PublicationAuthor author) throws JDOMException, IOException, SAXException;
    
    Set<Element> getPublicationsForAuthor(PublicationAuthor author) throws IOException, JDOMException, SAXException;

}
