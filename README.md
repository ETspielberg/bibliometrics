# bibliometrics #
A web application to perform bibliometric analyses based on a given set of MODS-formatted bibliographic records.

Provided with an author’s name or identifier (e.g. ORCID, Scopus author ID) the tool collects the author’s publications from a repository (up to now only [MyCoRe](https://github.com/MyCoRe-Org "MyCoRe")-based ones). This mechanism allows for the curation of the data within our local system and the inclusion of all media of output (presentation, poster etc). 

Subsequently, the collected publications are enriched with additional information gathered from a citation database (Scopus). Three analysis modules can be applied to the enriched publication data:

   -  General analysis (number and type of publications, publications per year etc.)
-     Citation analysis (average citations per publication, h-index, uncitedness)
-     Affiliation analysis (home and partner institutions)

The resulting XML-file is rendered into a web page. If approved by the bibliometrician the XML file is saved and a formal report is produced as a LaTeX-generated pdf file. An email with access links is sent to the bibliometrician. By forwarding these links to the initiator of the analysis access to the final report can be granted in both formats.

## Adaption to other systems ##
The MODS-entries are collected by classes implementing the Connector-Interface (unidue.ub.api.connectors.Connector). These can be adopted to support other types of sources for bibliographic records. Other sources for citation data need to implement the CitationGetter-interface (unidue.ub.api.connectors.CitationGetter)