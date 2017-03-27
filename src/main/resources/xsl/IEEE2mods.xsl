<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:mods="http://www.loc.gov/mods/v3"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xalan="http://xml.apache.org/xalan"
	exclude-result-prefixes="xsl xalan xsi ">

	<xsl:output method="xml" encoding="UTF-8" indent="yes"
		xalan:indent-amount="2" />


	<xsl:template match="/">
	<mods:modsCollection xsi:schemaLocation="http://www.loc.gov/mods/v3 http://www.loc.gov/standards/mods/v3/mods-3-4.xsd">
      <xsl:apply-templates select="root"/>
    </mods:modsCollection>
    </xsl:template>
    
    <xsl:template match="root">
		<xsl:apply-templates select="document" />
	</xsl:template>

	<xsl:template match="root/document">
	<mods:mods>
		<xsl:apply-templates select="title" />
		<xsl:apply-templates select="pubtype" />
		<xsl:apply-templates select="authors" />
		<xsl:apply-templates select="doi" />
		<mods:extension>
				<sourcetext type="crossRef">
					<xsl:copy-of select="root" />
				</sourcetext>
			</mods:extension>
		</mods:mods>
	</xsl:template>

	<xsl:template match="title">
		<mods:titleInfo>
			<mods:title>
				<xsl:value-of select="." />
			</mods:title>
		</mods:titleInfo>
	</xsl:template>

	<xsl:template match="pubtype">
		<xsl:choose>
			<xsl:when test=".='Conference Publications'">
				<mods:relatedItem type="host">
					<mods:genre usage="primary"
						valueURI="http://www.mycore.org/classifications/mir_genres#proceeding"
						authorityURI="http://www.mycore.org/classifications/mir_genres"
						type="intern" />
					<mods:titleInfo>
						<xsl:apply-templates select="../pubtitle" />
					</mods:titleInfo>
					<mods:part>
						<xsl:apply-templates select="../arnumber" />
					</mods:part>
					<xsl:apply-templates select="../isbn" />
				</mods:relatedItem>
			</xsl:when>
			<xsl:otherwise>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="pubtitle">
		<mods:titleInfo type="simple">
			<xsl:value-of select="." />
		</mods:titleInfo>
	</xsl:template>

	<xsl:template match="arnumber">
		<mods:extent unit="pages">
			<xsl:choose>
				<xsl:when test="not(contains(.,'-'))">
					<mods:start>
						<xsl:value-of select="." />
					</mods:start>
				</xsl:when>
				<xsl:when
					test="string-length(substring-before(.,'-')) &lt;= string-length(substring-after(.,'-'))">
					<mods:start>
						<xsl:value-of select="substring-before(.,'-')" />
					</mods:start>
					<mods:end>
						<xsl:value-of select="substring-after(.,'-')" />
					</mods:end>
				</xsl:when>
				<xsl:otherwise>
					<mods:start>
						<xsl:value-of select="." />
					</mods:start>
				</xsl:otherwise>
			</xsl:choose>
		</mods:extent>
	</xsl:template>

	<xsl:template match="isbn">
		<mods:identifier type="isbn">
			<xsl:value-of select="." />
		</mods:identifier>
	</xsl:template>

	<xsl:template match="doi">
		<mods:identifier type="doi" invalid="yes">
			<xsl:value-of select="." />
		</mods:identifier>
	</xsl:template>

	<xsl:template match="authors">
		<mods:name usage="primary" type="personal">
			<mods:displayForm>
				<xsl:value-of select="." />
			</mods:displayForm>
			<mods:role>
				<mods:roleTerm type="code" authority="marcrelator">aut
				</mods:roleTerm>
			</mods:role>
		</mods:name>
	</xsl:template>

	<xsl:template match="*|text()" />

</xsl:stylesheet>