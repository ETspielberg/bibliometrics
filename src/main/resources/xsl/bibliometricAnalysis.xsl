<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xalan="http://xml.apache.org/xalan"
	xmlns:i18n="xalan://org.mycore.services.i18n.MCRTranslation"
	xmlns:mabxml="http://www.ddb.de/professionell/mabxml/mabxml-1.xsd"
	xmlns:mods="http://www.loc.gov/mods/v3" exclude-result-prefixes="xsl xalan i18n mabxml">

	<xsl:output encoding="UTF-8" method="html" media-type="text/html"
		indent="yes" xalan:indent-amount="2" />

	<xsl:include href="mods-display.xsl" />
	<xsl:include href="pubsPerYear.xsl" />
	<xsl:include href="pubsPerType.xsl" />

	<!-- ============ Parameter von MyCoRe LayoutService ============ -->

	<xsl:param name="WebApplicationBaseURL" />
	<xsl:param name="ServletsBaseURL" />
	<xsl:param name="RequestURL" />
	<xsl:param name="CurrentLang" />
	<xsl:param name="DefaultLang" />

	<!-- ======== HTML Seitenlayout ======== -->


	<xsl:variable name="authorName">
		<xsl:value-of select="/bibliometricAnalysis/author/@name" />
	</xsl:variable>

	<xsl:variable name="citationSource">
		<xsl:value-of select="/bibliometricAnalysis/@citationSource" />
	</xsl:variable>
	
	<xsl:variable name="affiliationAnalysis">
		<xsl:value-of select="/bibliometricAnalysis/@affiliationAnalysis" />
	</xsl:variable>

	<xsl:variable name="queryID">
		<xsl:value-of select="/bibliometricAnalysis/@queryID" />
	</xsl:variable>

	<xsl:template match="/">
		<html>
			<head>
				<meta charset="utf-8" />
				<meta http-equiv="X-UA-Compatible" content="IE=edge" />
				<meta name="viewport" content="width=device-width, initial-scale=1" />
				<!-- The above 3 meta tags *must* come first in the head; any other head 
					content must come *after* these tags -->
				<meta name="description" content="" />
				<meta name="author" content="" />
				<link rel="icon" href="img/favicon.ico">
				</link>

				<title>DuEPublicA :: Personal Analysis</title>

				<script type="text/javascript"
					src="{$WebApplicationBaseURL}webjars/jquery/${version.jquery}/jquery.min.js" />
				<script type="text/javascript"> jQuery.noConflict(); </script>
				<script type="text/javascript"
					src="{$WebApplicationBaseURL}webjars/jquery-ui/${version.jquery}/jquery-ui.min.js" />
				<script type="text/javascript"
					src="{$WebApplicationBaseURL}webjars/datatables/${version.datatables}/js/jquery.dataTables.min.js"
					language="javascript" />
				<script type="text/javascript"
					src="{$WebApplicationBaseURL}webjars/highcharts/${version.highcharts}/highcharts.src.js"/>
				<script type="text/javascript"
					src="{$WebApplicationBaseURL}webjars/highcharts/${version.highcharts}/modules/exporting.src.js"/>
				<script type="text/javascript"
					src="{$WebApplicationBaseURL}webjars/highcharts/${version.highcharts}/themes/grid.js"/>
				<script src="{$WebApplicationBaseURL}js/leaflet.js"/>

				<link rel="stylesheet" href="{$WebApplicationBaseURL}css/leaflet.css" />
				<link href="{$WebApplicationBaseURL}css/bootstrap.css" rel="stylesheet" />
				<link href="{$WebApplicationBaseURL}css/ie10-viewport-bug-workaround.css"
					rel="stylesheet" />
				<link href="{$WebApplicationBaseURL}css/dashboard.css" rel="stylesheet" />
				<link href="{$WebApplicationBaseURL}css/description_box.css"
					rel="stylesheet" />
				<link href="{$WebApplicationBaseURL}css/layout.css" rel="stylesheet" />
				<link href="{$WebApplicationBaseURL}css/dataTables.bootstrap.min.css"
					rel="stylesheet" />

				<link rel="icon" href="{$WebApplicationBaseURL}img/favicon.ico" />
			</head>
			<body>
				<xsl:choose>
					<xsl:when test="bibliometricAnalysis/navbar/qualifiedAs/.='analyst'">
						<nav class="navbar navbar-inverse navbar-fixed-top">
							<div class="container-fluid">
								<div class="navbar-header">
									<button type="button" class="navbar-toggle collapsed"
										data-toggle="collapse" data-target="#navbar" aria-expanded="false"
										aria-controls="navbar">
										<span class="sr-only">Toggle navigation</span>
										<span class="icon-bar"/>
										<span class="icon-bar"/>
										<span class="icon-bar"/>
									</button>
									<a class="navbar-brand" href="{$WebApplicationBaseURL}index.html">DuEPublicA :: Personal Analysis</a>
								</div>
								<div id="navbar" class="navbar-collapse collapse">
									<ul class="nav navbar-nav navbar-right">
										<li>
											<a href="{$WebApplicationBaseURL}index.html">Home</a>
										</li>
										<li>
											<a href="{$WebApplicationBaseURL}logout">Logout</a>
										</li>
									</ul>
								</div><!--/.navbar-collapse -->
							</div>
						</nav>
						<xsl:call-template name="form" />
					</xsl:when>
					<xsl:otherwise>
						<nav class="navbar navbar-inverse navbar-fixed-top">
							<div class="container-fluid">
								<div class="navbar-header">
									<button type="button" class="navbar-toggle collapsed"
										data-toggle="collapse" data-target="#navbar" aria-expanded="false"
										aria-controls="navbar">
										<span class="sr-only">Toggle navigation</span>
										<span class="icon-bar"/>
										<span class="icon-bar"/>
										<span class="icon-bar"/>
									</button>
									<a class="navbar-brand" href="#">DuEPublicA :: Analyse</a>
								</div>
							</div>
						</nav>
					</xsl:otherwise>
				</xsl:choose>
				<xsl:apply-templates select="bibliometricAnalysis" />
				<script src="{$WebApplicationBaseURL}js/bootstrap.min.js" />
			</body>
		</html>
	</xsl:template>

	<xsl:template name="form">
		<div class="col-md-10 col-md-offset-1 main">
			<form class="form-inline">
				Citation data from
				<div class="form-group">
					<select id="citationSource" class="form-control" name="citationSource"
						value="{$citationSource}">
						<option value="scopus">Scopus</option>
						<option value="wos">Web of Science</option>
						<option value="ieee">IEEE</option>
						<option value="none">None</option>
					</select>
				</div>
				for
				<div class="form-group">
					<input class="form-control" id="queryID" type="text" name="queryID"
						size="40" value="{$queryID}" />
				</div>
				Affiliation analysis with 
				<div class="form-group">
					<select id="affiliationAnalysis" class="form-control" name="affiliationAnalysis"
						value="{$affiliationAnalysis}">
						<option value="false">None</option>
						<option value="scopus">Scopus</option>
					</select>
				</div>
				<button type="submit" class="btn btn-default">Run</button>
			</form>
		</div>
	</xsl:template>

	<xsl:template match="bibliometricAnalysis">
		<xsl:choose>
			<xsl:when test="author">
				<xsl:if test="navbar/qualifiedAs/.='analyst'">
					<div class="col-md-10 col-md-offset-1 main">
						<form class="form-inline" action="exportReport">
							<a class="btn btn-sm btn-primary" href="{$WebApplicationBaseURL}/analysis/exportReport"
								role="button">Save and create pdf</a>
						</form>
					</div>
				</xsl:if>
				<xsl:apply-templates select="author" />
				<xsl:apply-templates select="generalStatistics" />
				<xsl:apply-templates select="citationStatistics" />
				<xsl:call-template name="temporalEvolution"/>
				<xsl:apply-templates select="mods:collection" />
				<xsl:apply-templates select="json" />
			</xsl:when>
			<xsl:otherwise>
				<div class="col-md-10 col-md-offset-1 main">
					<p>No entries were found. Please provide a valid name or
						identifier.</p>
				</div>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="author">
		<div class="col-md-10 col-md-offset-1 main">
			<h1>
				Bibliometric analysis for
				<xsl:value-of select="@name" />
			</h1>
		</div>
		
		
		<xsl:choose>
			<xsl:when test="/bibliometricAnalysis/citationStatistics">
			<!-- <div class="col-md-4"> <h3>Kontaktdaten</h3> Hier sollten Kontaktangaben 
				aus dem LSF eingeblendet werden </div> -->
			<div class="col-md-10 col-md-offset-1 main">
				<h3>
					Places
					<xsl:value-of select="@name" />
					has been publishing:
				</h3>
				<div id="mapid" />
			</div>
			</xsl:when>
			<xsl:otherwise>
			</xsl:otherwise>
		</xsl:choose>

		<div class="col-md-10 col-md-offset-1 main">
			<h3>Data source</h3>
			<p>
				This bibliometric analysis of
				<xsl:value-of select="/bibliometricAnalysis/author/@name" />'s
				publications is based on the
				publications which are listed in the Bibliography of the University
				Duisburg-Essen.
			</p>
			<xsl:choose>
				<xsl:when test="/bibliometricAnalysis/citationStatistics">
					<xsl:choose>
						<xsl:when
							test="/bibliometricAnalysis/citationStatistics/@source = 'scopus'">
							<p>
								In general, the impact of a publications is correlated to the number of other publications citing this document.
								Citation data for articles in scientific journals, conference
								proceedings or book chapters were taken from the citation
								database
								<a href="http://www.scopus.com/">Scopus</a>.
							</p>
							<p>
								The database
								<a href="http://www.scopus.com/">Scopus</a>
								contains records of several selected publication forms (for example peer-reviewed journal
								articles, conference proceedings and book chapters).
								Publications as monographs are not included in the citation analysis.
								For a comprehensive evaluation this report should should be accompanied
								by peer-reviews and alternative metrics. Please refer also to the 10 priciples of of research evaluation as
								outlined in the
								<a href="http://www.leidenmanifesto.org">Leiden Manifesto for Research Metrics</a>
							</p>
						</xsl:when>
						<xsl:otherwise>
							Citation data were taken from another source.
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:otherwise>
					<p>
						No citation analysis has been performed.
					</p>
				</xsl:otherwise>
			</xsl:choose>
		</div>
	</xsl:template>

	<xsl:template match="generalStatistics">
		<div class="col-md-10 col-md-offset-1 main">
			<h3>Basic metrics</h3>
			<div class="col-md-4">
				<p>
					<xsl:value-of select="/bibliometricAnalysis/author/@name" />
					has published
					<xsl:value-of select="totalNumberOfPublications/." />
					publications between
					<xsl:value-of select="startYear/." />
					and
					<xsl:value-of select="endYear/." />
					which are listed in the University Bibliography.
				</p>
				<p>
				The diagramme depicts the distribution over the available communication media (journal article, book chapter etc.)  
				</p>
			</div>
			<div class="col-md-6">
				<div id="media" style="width: 250px; height: 200px; margin: 0 auto" />
			</div>
		</div>
	</xsl:template>

	<xsl:template match="citationStatistics">
	<xsl:variable name="fractionWithCitationData">
	<xsl:value-of select="numberOfDocumentsWithCitationData/. div number(../generalStatistics/totalNumberOfPublications/.)" />
	</xsl:variable>
	
	<xsl:choose>
	<xsl:when test="$fractionWithCitationData &lt; 0.8 and $fractionWithCitationData &gt; 0.6">
	<div class="col-md-10 col-md-offset-1 main">
		<div class="alert alert-warning">
		<h4>Warning!</h4>
		<p>Only <xsl:value-of select='format-number($fractionWithCitationData,"0.0%")' /> of the publications in the University Bibliography are covered by the citation database. 
		All citation analysis should be handled carefully as they do not represent the author's whole scientific output!
		</p>
		</div>
	</div>
	</xsl:when>
	<xsl:when test="$fractionWithCitationData &lt; 0.6">
	<div class="col-md-10 col-md-offset-1 main">
		<div class="alert alert-danger">
		<h4>Caution!</h4>
		<p>
		Only <xsl:value-of select='format-number($fractionWithCitationData,"0.0%")' /> of the publications in the University Bibliography are covered by the citation database. 
		The results of the citation analysis are not representive for the author's scientific work!
		</p>
		<p>
		In addition, this might indicates that a considerable part of scientific communication within the author's subject communitytakes place in media not covered by the citation database.
		The listed citation counts might be much smaller, as even the publications listed in the citation database might have a large impact on other communication media.
		</p>
		</div>
	</div>
	</xsl:when>
	<xsl:otherwise>
	</xsl:otherwise>
	</xsl:choose>

		<div class="col-md-10 col-md-offset-1 main">
			<h3 class="sub-header">Citation metrics</h3>
			<div class="table-responsive">
				<table class="table table-hover">
					<tr>
						<xsl:choose>
							<xsl:when test="$citationSource = 'scopus'">
								<td>
									N(<a href="http://www.scopus.com/">Scopus</a>):
								</td>
							</xsl:when>
							<xsl:otherwise>
								<td>
									N:
								</td>
							</xsl:otherwise>
						</xsl:choose>

						<td>
							N<sub>P</sub> =
						</td>
						<td>
							<xsl:value-of select="numberOfDocumentsWithCitationData/." />
						</td>
					</tr>
					<tr>
						<td>
							<xsl:choose>
								<xsl:when test="$citationSource = 'scopus'">

									Total number of citations in
									<a href="http://www.scopus.com/">Scopus</a>:
								</xsl:when>
								<xsl:otherwise>
									Total number of citations:
								</xsl:otherwise>
							</xsl:choose>
						</td>
						<td>
							N<sub>Z</sub>=
						</td>
						<td>
							<xsl:value-of select="totalCitations/." />
						</td>
					</tr>
					<tr>
						<td>Average citations per Publication</td>
						<td> CpP = </td>
						<td>
							<xsl:value-of
								select='format-number(totalCitations/. div number(numberOfDocumentsWithCitationData/.),"#0.0")' />
						</td>
					</tr>
					<xsl:choose>
						<xsl:when test="uncited">
							<tr>
								<td>Fraction of non-cited publications:</td>
								<td>
									P<sub>uncited</sub>=
								</td>
								<td>
									<xsl:value-of
										select='format-number(uncited/. div number(numberOfDocumentsWithCitationData/.),"0.0%")' />
								</td>
							</tr>
						</xsl:when>
					</xsl:choose>
					<xsl:choose>
						<xsl:when test="hIndex">
							<tr>
								<td>Hirsch-Index</td>
								<td>h =</td>
								<td>
									<xsl:value-of select="hIndex" />
								</td>
							</tr>
						</xsl:when>
					</xsl:choose>
				</table>
			</div>
		</div>
		<div class="col-md-10 col-md-offset-1 main">
			<h3>In particular:</h3>
			<p>
				<h4>Average Citation per Publication</h4>
				The average
				<em>citation per publication (CpP)</em>
				represents a measure for the impact of the publications published by a scientise, a group of scientists or an instituttion.
				Calculated as the ratio between the total number of citation (C) and and the total number of publications (P), the CCP allows for a relative comparison of the scientific attention.
				As an arithmetric average it is sensitive against outliers and does <em>not</em> consider the variance nor the the distribution of the citation counts.
				The data source as well as the number and nature of the publications influence the citation rate.
			</p>
			<p>
				The University Bibliography lists  
				<xsl:value-of select="numberOfDocumentsWithCitationData/." />
				publications between
				<xsl:value-of select="/bibliometricAnalysis/generalStatistics/startYear/." />
				and
				<xsl:value-of select="/bibliometricAnalysis/generalStatistics/endYear/." />
				for 
				<xsl:value-of select="/bibliometricAnalysis/author/@name" />.
				For these publications
				<xsl:value-of select="totalCitations/." />
				citations are listed in the citation database yielding
				<xsl:value-of
					select='format-number(totalCitations/. div number(numberOfDocumentsWithCitationData/.),"#0.0")' />
				citations per publication .
			</p>
			<xsl:choose>
				<xsl:when test="uncited">
					<h4>Fraction of non-cited publications</h4>
					<p>
						To take Publications into account that some publications included in the ciatation analysis have not been cited at all, the metric
						<em>
							Uncitedness (P<sub>uncited</sub>)
						</em>
						is included as well.
						In contrast to the citation rate this metric represents the lack of attention to certain publications.
						However, several reasons account for a low attention. 
						Very new Publications have not been able to raise citations yet, the publication might be hard to find or might be ignored due to other reasons.
						In addition, even very popular publications might have none citation, if due to his or her reputation, the author is cited by his or her name and not the publication itself. 
						The metric is calculated as the fraction of publications without citations (N) and the total number of publications listed in the citation database (P).
						</p>
						<p>
						From <xsl:value-of select="/bibliometricAnalysis/author/@name" />'s publications listed in the citation database,
						<xsl:value-of
							select='format-number(uncited/. div number(numberOfDocumentsWithCitationData/.),"0.0%")' />
							have not been cited (yet).
					</p>
				</xsl:when>
			</xsl:choose>
			<xsl:choose>
				<xsl:when test="hIndex">
					<p>
						<h4>h-Index</h4>
						The 
						<em>h-index</em>
						(Hirsch-index, h) is commonly seen as a metric for the scientific impact of an author, a group of authors or an Institution. 
						If the publications are ordered from the most cited item to the lowest, the h-index corresponds to number of publications <em>n</em> who have been cited at least <em>n</em> times. 
						A high value means that the author has published a high number of publications, which have been highly cited.
						Therefore it is usally seen as a sign of great influence.
						However, it has to be kept in mind that the average h-index varies strongly across subject areas and can not be used for an interdisciplinary comparison.
						In addition, it depends strongly on the age of the scientist and increases with time, even if no further contributions are made.
					</p>
					<p>
						For <xsl:value-of select="/bibliometricAnalysis/author/@name" />
						a
						h-Iindex of
						<xsl:value-of select="hIndex/." />
						can be calculated meaning that
						<xsl:value-of select="hIndex/." />
						of the author's publications have been 
						cited at least 
						<xsl:value-of select="hIndex/." />
						times.
					</p>
				</xsl:when>
			</xsl:choose>
		</div>
	</xsl:template>
	
	<xsl:template name="temporalEvolution" >
	<div class="col-md-10 col-md-offset-1 main">
			<h3>Temporal evolution</h3>
			<div id="highchart" style="min-width: 310px; height: 400px; margin: 0 auto" />
		</div>
	</xsl:template>

	<xsl:template match="json">
		<script>
		<xsl:if test="/bibliometricAnalysis/citationStatistics">
			function onEachFeature(feature, layer) {
			if (feature.properties
			&amp;&amp; feature.properties.popupContent) {
			layer.bindPopup(feature.properties.popupContent);
			}
			}

			var
			geojsonMarkerOptions = {
			radius: 8,
			fillColor: "#ff7800",
			color:
			"#000",
			weight: 1,
			opacity: 1,
			fillOpacity: 0.8
			};

			var geojsonMarkerOptionsColl =
			{
			radius: 4,
			fillColor: "#0000ff",
			color:
			"#000",
			weight: 1,
			opacity: 1,
			fillOpacity: 0.8
			};

			var geojsonObject =
			<xsl:value-of select="homeInstitutions" />
			;
			var collaboratingInstitutions =
			<xsl:value-of select="partnerInstitutions" />
			;
			var mymap = L.map('mapid').setView([51.46, 7.01], 1);

			L.tileLayer('https://api.tiles.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token={accessToken}',{
			attribution: 'Map data:	<a href="http://openstreetmap.org">OpenStreetMap</a> contributors: <a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, Imagery <a href="http://mapbox.com">Mapbox</a>', 
			maxZoom: 18,	
			id: 'mapbox.streets', 
			accessToken: 'pk.eyJ1IjoiZWlrZSIsImEiOiJjaW1vbjAzNTAwMDFqdm9rZ256enRkYml0In0.W3PaqDVLrAmbynTI6mNviQ'
			}).addTo(mymap);

			var markersColl =
			L.geoJson(collaboratingInstitutions, {
			onEachFeature: onEachFeature,pointToLayer: function(feature, latlng) {return L.circleMarker(latlng, geojsonMarkerOptionsColl);
			}});
			markersColl.addTo(mymap);

			var markers = L.geoJson(geojsonObject, {
			onEachFeature: onEachFeature,
			pointToLayer: function(feature, latlng)
			{
			return L.circleMarker(latlng, geojsonMarkerOptions);
			}});
			markers.addTo(mymap);

			var overlayMaps = {
			"Heim-Institution": markers,
			"Kooperationspartner": markersColl
			};

			mymap.fitBounds(markersColl.getBounds().pad(0.1))

			L.control.layers(null,overlayMaps).addTo(mymap);
			</xsl:if>
			
			//-----------------------------------------------------------------------------------------
			jQuery(document).ready(function () {
			jQuery('#highchart').highcharts(
			<xsl:apply-templates select="pubsPerYear" />
			);
			
			//-----------------------------------------------------------------------------------------
			jQuery('#sortableTable').DataTable({
			"order": [ 2, 'desc' ],
			"searching": false
			});
			
			//-----------------------------------------------------------------------------------------
			jQuery('#media').highcharts(
			<xsl:apply-templates select="pubsPerType" />
			);
		</script>
	</xsl:template>

	<xsl:template match="mods:collection">
		<div class="col-md-10 col-md-offset-1 main">
			<h3> List of publications </h3>
			<p>The following table depicts the list of publications forming the basis of this report. Clicking on the title leads to the article, on the journal name to the entry in the "Elektronische Zeitschriftendatenbank" (EZB).</p>
				<xsl:if test="$citationSource = 'scopus'">
					The citation data in the last column are taken from the citation database
					<a href="http://www.scopus.com/">Scopus</a>.
					By clicking on the citation count the list of citing journals can be seen in <a href="http://www.scopus.com/">Scopus</a>.
				</xsl:if>
			<div class="table-responsive">
				<table id="sortableTable" class="table table-hover">
					<thead>
						<tr>
							<th>Publication </th>
							<th>Year </th>
							<th>Citation count</th>
						</tr>
					</thead>
					<tbody>
						<xsl:apply-templates select="mods:mods" />
					</tbody>
				</table>
			</div>
		</div>
	</xsl:template>

</xsl:stylesheet>