<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
	xmlns:xalan="http://xml.apache.org/xalan"
	xmlns:mods="http://www.loc.gov/mods/v3"
	exclude-result-prefixes="xsl xalan">

	<xsl:output method="xml" encoding="UTF-8" indent="yes"
		xalan:indent-amount="2" />

	<xsl:output method="xml" encoding="UTF-8" indent="yes"
		xalan:indent-amount="2" />

	<xsl:template match="/">
	<response>
	<number>
	<xsl:value-of select="response/result/@numFound" />
	</number>
	<list>
	<xsl:apply-templates select="response/result/doc"/>
	</list>
	</response>
	</xsl:template>
	
	<xsl:template match="doc">
	<mcrID><xsl:apply-templates select="str" /></mcrID>
	</xsl:template>
	
	<xsl:template match="str[@name = 'id']">
	<xsl:variable name="mcrID">
		<xsl:value-of select="." />
	</xsl:variable>
	<xsl:apply-templates select="document(concat('http://localhost:8080/mir/api/v1/objects/',$mcrID))" />
	</xsl:template>
	
	<xsl:template match="str">
	</xsl:template>
	
	<xsl:template match="mods:mods">
		<xsl:copy>
            <xsl:apply-templates select="@*|node()" />
        </xsl:copy>
	</xsl:template>
	</xsl:stylesheet>