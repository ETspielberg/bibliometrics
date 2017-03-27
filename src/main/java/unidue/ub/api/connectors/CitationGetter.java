package unidue.ub.api.connectors;

import java.io.IOException;

import org.apache.commons.httpclient.HttpException;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.xml.sax.SAXException;

public interface CitationGetter {

    public int getCitationCount(String id) throws HttpException, IOException, JDOMException, SAXException;
    
    public Element getCitationInformation(String id) throws HttpException, IOException, JDOMException, SAXException;
}
