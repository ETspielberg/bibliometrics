<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:mods="http://www.loc.gov/mods/v3"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xalan="http://xml.apache.org/xalan"
	xmlns:ce="http://www.elsevier.com/xml/ani/common" xmlns:scopus="http://www.elsevier.com/xml/svapi/abstract/dtd"
	xmlns:cto="http://www.elsevier.com/xml/cto/dtd" xmlns:dc="http://purl.org/dc/elements/1.1/"
	xmlns:prism="http://prismstandard.org/namespaces/basic/2.0/"
	exclude-result-prefixes="xsl xalan mods cto ce dc scopus prism">

	<xsl:output method="xml" encoding="UTF-8" indent="yes"
		xalan:indent-amount="2" />

	<xsl:template match="/">
		<scopus>
			<publication>
				<xsl:apply-templates select="author-retrieval-response/coredata" />
				<xsl:apply-templates select="author-retrieval-response/author-profile/publication-range" />
			</publication>
				<xsl:apply-templates select="author-retrieval-response/author-profile/affiliation-history" />
		</scopus>
	</xsl:template>

	<xsl:template match="coredata">
		<citedTotal>
			<xsl:value-of select="citation-count/." />
		</citedTotal>
		<scopus>
			<xsl:value-of select="document-count/." />
		</scopus>
		<averageCitationPerPublication>
			<xsl:value-of select="citation-count/. div document-count/." />
		</averageCitationPerPublication>
		<hIndex>
		<xsl:value-of select="../h-index/." />
		</hIndex>
	</xsl:template>

	<xsl:template match="publication-range">
		<start>
			<xsl:value-of select="@start" />
		</start>
		<end>
			<xsl:value-of select="@end" />
		</end>
	</xsl:template>

	<xsl:template match="author-retrieval-response/author-profile/affiliation-history">
	<home-institutions>
	<xsl:apply-templates select="affiliations" />
		<xsl:apply-templates select="affiliation" />
		</home-institutions>
	</xsl:template>
	
	<xsl:template match="affiliations">
		<xsl:apply-templates select="affiliation" />
	</xsl:template>
	
	<xsl:template match="affiliation[ip-doc/@type = 'parent']">
		<scopus-affilID>
		<xsl:value-of select="@affiliation-id" />
		</scopus-affilID>
	</xsl:template>
	
	<xsl:template match="affiliation">
		<scopus-affilID>
		<xsl:value-of select="@parent" />
		</scopus-affilID>
	</xsl:template>
	
</xsl:stylesheet>