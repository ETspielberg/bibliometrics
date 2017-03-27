package unidue.ub.bibliometrics.knowledgebase;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.annotation.WebServlet;

import org.apache.log4j.Logger;
import org.mycore.common.config.MCRConfiguration;
import org.mycore.frontend.servlets.MCRServletJob;

import unidue.ub.bibliometrics.BibliometricsServlet;

@WebServlet("/knowledgeBase/authorUpload")
public class AuthorImporterServlet extends BibliometricsServlet {

	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = Logger.getLogger(AuthorImporterServlet.class);

	private final static String inputDir;

	private int posSurname;

	private int posFirstname;

	private int posMiddlename;

	private int posOrcid;

	private int posScopusID;

	private int posResearcherID;

	private int posGnd;

	private int posInstitution;

	private int posDepartment;

	private int posAddress;

	private int posLSFID;

	private int posGoogleProfile;

	private int posResearchGate;

	private int posDatabases;

	private int posComments;

	private int posKeywords;

	private int posHomepage;

	static {
		MCRConfiguration config = MCRConfiguration.instance();
		inputDir = config.getString("ub.statistics.inputDir");
	}

	@Override
	public void doGetPost(MCRServletJob job) throws Exception {
		posSurname = -1;
		posMiddlename = -1;
		posFirstname = -1;
		posOrcid = -1;
		posScopusID = -1;
		posGnd = -1;
		posResearcherID = -1;
		posInstitution = -1;
		posDepartment = -1;
		posAddress = -1;
		posLSFID = -1;
		posGoogleProfile = -1;
		posResearchGate = -1;
		posComments = -1;
		posKeywords = -1;
		posDatabases = -1;
		posHomepage = -1;

		String listName = getParameter(job, "listName");
		List<String[]> data = readFromDisk(listName);
		List<PublicationAuthor> authors = buildAuthorList(data);
		PublicationAuthorDAO.persistAuthors(authors);
		job.getResponse().sendRedirect("/bibliometrics/upload");
	}

	private List<String[]> readFromDisk(String csvFilename) {
		List<String[]> data = new ArrayList<>();
		String filename = csvFilename + ".csv";
		File dataFile = new File(inputDir, filename);

		try {
			BufferedReader br = new BufferedReader(new FileReader(dataFile));
			String[] header = br.readLine().split(",");
			for (int i = 0; i < header.length; i++) {
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
				else if (header[i].toLowerCase().equals("lehrstuhl"))
					posInstitution = i;
				else if (header[i].toLowerCase().equals("scopus author id"))
					posScopusID = i;
				else if (header[i].toLowerCase().equals("researcher id"))
					posResearcherID = i;
				else if (header[i].toLowerCase().equals("department"))
					posDepartment = i;
				else if (header[i].toLowerCase().equals("google profil"))
					posGoogleProfile = i;
				else if (header[i].toLowerCase().equals("Addresse"))
					posAddress = i;
				else if (header[i].toLowerCase().equals("lsf id"))
					posLSFID = i;
				else if (header[i].toLowerCase().equals("researchgate"))
					posResearchGate = i;
				else if (header[i].toLowerCase().equals("Kommentare"))
					posComments = i;
				else if (header[i].toLowerCase().equals("keywords"))
					posKeywords = i;
				else if (header[i].toLowerCase().equals("datenbanken"))
					posDatabases = i;
				else if (header[i].toLowerCase().equals("homepage"))
					posHomepage = i;
			}
			String line = "";
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
			if (posSurname != -1)
				author.setSurname(datum[posSurname]);
			if (posFirstname != -1)
				author.setFirstname(datum[posFirstname]);
			if (posMiddlename != -1)
				author.setMiddlename(datum[posMiddlename]);
			if (posInstitution != -1)
				author.setInstitution(datum[posInstitution]);
			if (posOrcid != -1)
				author.setOrcid(datum[posOrcid]);
			if (posGnd != -1)
				author.setGnd(datum[posGnd]);
			if (posScopusID != -1)
				author.setScopusAuthorID(datum[posScopusID]);
			if (posResearcherID != -1)
				author.setScopusAuthorID(datum[posScopusID]);
			if (posDepartment != -1)
				author.setDepartment(datum[posDepartment]);
			if (posAddress != -1)
				author.setAddress(datum[posAddress]);
			if (posLSFID != -1)
				author.setLsfID(datum[posLSFID]);
			if (posGoogleProfile != -1)
				author.setLinkGoogleProfile(datum[posGoogleProfile]);
			if (posResearchGate != -1)
				author.setResearchGate(datum[posResearchGate].equals("X"));
			if (posComments != -1)
				author.setComment(datum[posComments]);
			if (posKeywords != -1)
				author.setKeywords(datum[posKeywords]);
			if (posDatabases != -1)
				author.setFoundInDatabase(datum[posDatabases]);
			if (posHomepage != -1)
				author.setFoundInDatabase(datum[posHomepage]);
			query.add(author);
		}
		return query;
	}
}
