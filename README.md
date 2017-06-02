# bibliometrics
A web application to perform bibliometric analyses

Provided with an author’s name or identifier (e.g. ORCID, Scopus author ID) the tool collects the author’s publications from a MyCoRe Repository. This mechanism allows for the curation of the data within the local system and the inclusion of all media of output (presentation, poster etc). Subsequently, the collected publications are enriched with additional information gathered from a citation database. 
Three analysis modules can be applied to the enriched publication data:
1.	General analysis (number and type of publications, publications per year etc.)
2.	Citation analysis (average citations per publication, h-index, uncitedness)
3.	Affiliation analysis (home and partner institutions)

The resulting XML-file is rendered into a web page. If approved by the bibliometrician the XML file is saved and a formal report is produced as a LaTeX-generated pdf file. An email with access links is sent to the bibliometrician. By forwarding these links to the initiator of the analysis access to the final report can be granted in both formats.
