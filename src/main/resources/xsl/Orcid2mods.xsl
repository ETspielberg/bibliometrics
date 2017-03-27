<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:mods="http://www.loc.gov/mods/v3"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xalan="http://xml.apache.org/xalan"
	xmlns:orcid="http://www.orcid.org/ns/orcid"
	exclude-result-prefixes="xsl xalan xsi ">

	<xsl:output method="xml" encoding="UTF-8" indent="yes"
		xalan:indent-amount="2" />


	<xsl:template match="/">
		<mods:mods>
			<xsl:apply-templates select="orcid:orcid-message/orcid-profile/orcid-activities/orcid-works" />
			<mods:extension>
				<sourcetext type="orcid">
					<xsl:copy-of select="orcid:orcid-message" />
				</sourcetext>
			</mods:extension>
		</mods:mods>
	</xsl:template>

	<xsl:template match="orcid:orcid-message/orcid-profile/orcid-activities/orcid-works">
		<xsl:apply-templates select="work-title/title" />
	</xsl:template>

	<xsl:template match="work-title/title">
		<xsl:variable name="title">
			<xsl:choose>
				<xsl:when test="substring(text(),string-length(text()))='.'">
					<xsl:value-of select="substring(text(),1,string-length(text())-1)" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="title/text()" />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<mods:titleInfo>
			<mods:title>
				<xsl:choose>
					<xsl:when test="contains($title,': ')">
						<xsl:value-of select="substring-before($title,': ')" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$title" />
					</xsl:otherwise>
				</xsl:choose>
			</mods:title>
			<xsl:if test="contains($title,': ')">
				<mods:subTitle>
					<xsl:value-of select="substring-after($title,': ')" />
				</mods:subTitle>
			</xsl:if>
		</mods:titleInfo>
	</xsl:template>


	<xsl:template match="*|text()" />

</xsl:stylesheet>