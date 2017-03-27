<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:mods="http://www.loc.gov/mods/v3"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xalan="http://xml.apache.org/xalan"
	xmlns:ce="http://www.elsevier.com/xml/ani/common" xmlns:scopus="http://www.elsevier.com/xml/svapi/abstract/dtd"
	xmlns:cto="http://www.elsevier.com/xml/cto/dtd" xmlns:dc="http://purl.org/dc/elements/1.1/"
	xmlns:prism="http://prismstandard.org/namespaces/basic/2.0/"
	exclude-result-prefixes="xsl xalan cto ce dc scopus prism">

	<xsl:output method="xml" encoding="UTF-8" indent="yes"
		xalan:indent-amount="2" />

	<xsl:template match="/">
	<scopus-export>
	<xsl:choose>
	<xsl:when test="mycoreobject/metadata/def.modsContainer/modsContainer/mods:mods/mods:extension">
	<xsl:apply-templates select="mycoreobject/metadata/def.modsContainer/modsContainer/mods:mods/mods:extension/sourcetext" />
	</xsl:when>
	<xsl:otherwise>
	<error>no entry</error>
	</xsl:otherwise>
	</xsl:choose>
	</scopus-export>
	</xsl:template>

	<xsl:template match="sourcetext[@type='scopus']">
		<xsl:copy-of select="abstract-document" />" />
	</xsl:template>
	
	<xsl:template match="sourcetext">
	</xsl:template>
	
</xsl:stylesheet>