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
		<person>
		<xsl:apply-templates select="orcid-message" />
		</person>
	</xsl:template>
	
	<xsl:template match="orcid-message" >
		<xsl:apply-templates select="orcid-identifier" />
		<xsl:apply-templates select="orcid-bio" />
		<xsl:if test="orcid-acivities">
			<xsl:apply-templates select="orcid-activities" />
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="orcid-identifier">
		<orcid>
			<xsl:value-of select="path/." />
		</orcid>
	</xsl:template>

	<xsl:template match="orcid-bio">
			<surname>
				<xsl:value-of select="personal-details/family-name/." />
			</surname>
			<given-name>
				<xsl:value-of select="personal-details/given-names/." />
			</given-name>
			<xsl:if test="external-identifiers">
				<xsl:apply-templates select="external-identifiers/external-identifier" />
			</xsl:if>
	</xsl:template>
	
	<xsl:template match="external-identifier" >
		<xsl:choose>
			<xsl:when test="external-id-common-name/. = 'ResearcherID'">
				<researcherID>
					<xsl:value-of select="external-id-reference" />
				</researcherID>
			</xsl:when>
			<xsl:when test="external-id-common-name/. = 'Scopus Author ID'">
				<scopusAuthorID>
					<xsl:value-of select="external-id-reference" />
				</scopusAuthorID>
			</xsl:when>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template match="orcid-activities" >
		<xsl:if test="affiliations">
			<xsl:apply-templates select="affiliation " />
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="affiliation">
	<xsl:if test="not(end-date)">
		<affiliation>
			<xsl:value-of select="organization/name/." />
		</affiliation>
	</xsl:if>
	</xsl:template>
	
</xsl:stylesheet>