package unidue.ub.bibliometrics;

import java.util.Set;

import javax.servlet.annotation.WebServlet;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.mycore.common.MCRConstants;
import org.mycore.common.MCRSessionMgr;
import org.mycore.frontend.servlets.MCRServletJob;

import unidue.ub.api.connectors.Connector;
import unidue.ub.api.connectors.MyCoReConnector;
import unidue.ub.bibliometrics.knowledgebase.AuthorBuilder;
import unidue.ub.bibliometrics.knowledgebase.PublicationAuthor;

@WebServlet("/analysis/personalBibliometricReport")
public class BibliometricReportServlet extends BibliometricsServlet {

	/**
	 * takes the author and publication info from the session and prepares the
	 * final bibliometric report.
	 */
	private static final long serialVersionUID = 1L;

	private final static Namespace modsNS = MCRConstants.MODS_NAMESPACE;

	protected void doGetPost(MCRServletJob job) throws Exception {
		Element output = prepareOutput(job, "bibliometricAnalysis");

		// read in the parameters from the request
		String id = getParameter(job, "queryID");
		output.setAttribute("queryID", id);

		String type = getParameter(job, "citationSource");
		output.setAttribute("citationSource", type);
		
		boolean affiliationAnalysis = getParameter(job, "citationSource").equals("true");
		output.setAttribute("affiliationAnalysis", String.valueOf(affiliationAnalysis));

		if (!id.isEmpty()) {

			// prepare the author
			AuthorBuilder authorBuilder = new AuthorBuilder();
			PublicationAuthor author = authorBuilder.retrieveAuthor(id);

			// retrieve the reference data from the unibib
			Connector mycoreConnection = new MyCoReConnector();
			
			// collect publications from the mycore repository.
			Set<Element> modsEntries = mycoreConnection.getPublicationsForAuthor(author);

			if (modsEntries.size() > 0) {
			// perform basic statistics and add to output
			GeneralStatistics generalStatistics = new GeneralStatistics(modsEntries);
			author.setNumberOfPublicationsUniBib(generalStatistics.getNumberOfPublications());
			
			Element statistics = generalStatistics.prepareOutput();
			Element jsonData = new Element("json");
			jsonData.addContent(
					new Element("pubsPerYear").setText(generalStatistics.getPublicationsPerYearAsJSON().toString()));
			jsonData.addContent(
					new Element("pubsPerType").setText(generalStatistics.getPublicationsPerTypeAsJSON().toString()));
			output.addContent(statistics);

			// if not switched off, perform citation and affiliation statistics
			// with the chosen source
			if (!type.equals("none")) {
				// citation data are added depending on the citation source
				CitationStatistics citationStatistics = new CitationStatistics(modsEntries, type);
				Element citationElement = citationStatistics.prepareOutput();
				output.addContent(citationElement);
				author.sethIndex(String.valueOf(citationStatistics.getHIndex()));

				if (affiliationAnalysis) {
				// data about affiliations are added depending on the source
				AffiliationsStatistics affiliationStatistics = new AffiliationsStatistics(modsEntries, author, type);
				jsonData.addContent(affiliationStatistics.getActualHomeInstitutionsElement());
				jsonData.addContent(affiliationStatistics.getHomeInstitutionsElement());
				jsonData.addContent(affiliationStatistics.getPartnerInstitutionsElement());
				}
			}

			

			// add publications as mods collection
			Element publications = new Element("collection", modsNS);
			for (Element mods : modsEntries) {
				if (mods.getChild("extension", modsNS) != null) {
					mods.removeChild("extension", modsNS);
				}
				publications.addContent(mods.clone());
			}
			output.addContent(publications);

			// add the json data for graphs to output
			output.addContent(jsonData);
			}
			
			// add author to output
			author.addToOutput(output);

			// save a clone in the Session for the generation of latex code
			MCRSessionMgr.getCurrentSession().put("bibliometricReport", new Document(output.clone()));
			MCRSessionMgr.getCurrentSession().put("authorName", author.getAuthorName());
		}
		// display the analysis
		sendOutput(job, output);
	}
}
