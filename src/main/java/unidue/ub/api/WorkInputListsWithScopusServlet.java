package unidue.ub.api;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.jdom2.Element;
import org.mycore.common.config.MCRConfiguration;
import org.mycore.common.content.MCRJDOMContent;
import org.mycore.frontend.servlets.MCRServlet;
import org.mycore.frontend.servlets.MCRServletJob;

import unidue.ub.api.connectors.ScopusConnector;
import unidue.ub.bibliometrics.BibliometricsServlet;
import unidue.ub.bibliometrics.knowledgebase.AuthorBuilder;
import unidue.ub.bibliometrics.knowledgebase.PublicationAuthor;



@WebServlet("/getScopusAuthorData")
public class WorkInputListsWithScopusServlet extends BibliometricsServlet {

    private static final long serialVersionUID = 1L;
    
    private static final Logger LOGGER = Logger.getLogger(WorkInputListsWithScopusServlet.class);

    private final static String resultsDir;
    
    private final static String inputDir;
    
    private int posSurname;
    
    private int posFirstname;
    
    private int posMiddlename;
    
    private int posOrcid;
    
    private int posScopusID;
    
    private int posGnd;
    
    private int posInstitution;
    
    static {
        MCRConfiguration config = MCRConfiguration.instance();
        resultsDir = config.getString("ub.bibliometrics.resultsDir");
        inputDir = config.getString("ub.statistics.inputDir");
    }

    @Override
    public void doGetPost(MCRServletJob job) throws Exception {
        //prepare request
        HttpServletRequest req = job.getRequest();
        
        posSurname = -1;
        posMiddlename = -1;
        posFirstname = -1;
        posOrcid = -1;
        posScopusID = -1;
        posGnd = -1;
        posInstitution = -1;

        //read in service parameter
        String listName = getParameter(job, "listName");

        List<String[]> data = readFromDisk(listName);
        List<PublicationAuthor> authors = buildAuthorList(data);
        
        String csvOutput = AuthorBuilder.createCSVHeader();
        Element output = new Element("authorRetrieval");
        ScopusConnector connection = new ScopusConnector();
        for (PublicationAuthor author : authors) {
            AuthorBuilder builder = new AuthorBuilder(author);
            LOGGER.info("retrieving data for " + author.toString());
            if (author.getScopusAuthorID().equals(""))
            builder.retrieveScopusAuthorID();
            builder.retrieveScopusHIndex();
            csvOutput = csvOutput + builder.createCSVLine();
            author.addToOutput(output);
        }
        connection.close();
        output.addContent((new Element("csv")).addContent(csvOutput));
        (new MCRJDOMContent(output.clone())).sendTo(new File(resultsDir, "results.csv"));
        getLayoutService().doLayout(job.getRequest(), job.getResponse(), new MCRJDOMContent(output));
        LOGGER.info("Finished output.");
    }
    
    private List<String[]> readFromDisk(String csvFilename) {
        List<String[]> data = new ArrayList<>();
        String filename = csvFilename + ".csv";
        File dataFile = new File(inputDir, filename);
        
        try {
            BufferedReader br = new BufferedReader(new FileReader(dataFile));
            String[] header = br.readLine().split(",");
            for (int i = 0; i < header.length; i++ ) {
                if (header[i].toLowerCase().equals("name"))
                    posSurname = i;
                else if (header[i].toLowerCase().equals("vorname"))
                    posFirstname = i;
                else if (header[i].toLowerCase().equals("weitere"))
                    posMiddlename = i;
                else if (header[i].toLowerCase().equals("orcid"))
                    posOrcid = i;
                else if (header[i].toLowerCase().equals("gnd"))
                    posGnd = i;
                else if (header[i].toLowerCase().equals("institution"))
                    posInstitution = i;
                else if (header[i].toLowerCase().equals("scopusauthorid"))
                    posScopusID = i;
            }
            String line;
            while ((line = br.readLine()) != null) {
                String[] lines = line.split(",");
                data.add(lines);
            }
            br.close();
        } catch (Exception e) {
            LOGGER.info("csv file '" + filename + "' not found");
        }
        return data;
    }
    
    private List<PublicationAuthor> buildAuthorList(List<String[]> data) {
        List<PublicationAuthor> query = new ArrayList<>();
        for (String[] datum : data) {
            PublicationAuthor author = new PublicationAuthor();
            if (posSurname != -1) author.setSurname(datum[posSurname]);
            if (posFirstname != -1) author.setFirstname(datum[posFirstname]);
            if (posMiddlename != -1) author.setMiddlename(datum[posMiddlename]);
            if (posInstitution != -1) author.setInstitution(datum[posInstitution]);
            if (posOrcid != -1) author.setOrcid(datum[posOrcid]);
            if (posGnd != -1) author.setGnd(datum[posGnd]);
            if (posScopusID != -1) author.setScopusAuthorID(datum[posScopusID]);
            query.add(author);
        }
        return query;
    }
}
