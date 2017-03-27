<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:mods="http://www.loc.gov/mods/v3"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xalan="http://xml.apache.org/xalan"
	exclude-result-prefixes="xsl xalan xsi ">

	<xsl:output method="xml" encoding="UTF-8" indent="yes"
		xalan:indent-amount="2" />


	<xsl:template match="/">
		<mods:mods>
			<xsl:apply-templates select="content/message" />
			<mods:extension>
				<sourcetext type="crossRef">
					<xsl:copy-of select="content" />
				</sourcetext>
			</mods:extension>
		</mods:mods>
	</xsl:template>

	<xsl:template match="content/message">
		<xsl:apply-templates select="title" />
		<xsl:apply-templates select="type" />
		<xsl:apply-templates select="author" />
		<xsl:apply-templates select="DOI" />
	</xsl:template>

	<xsl:template match="title">
		<xsl:variable name="title">
			<xsl:choose>
				<xsl:when test="substring(text(),string-length(text()))='.'">
					<xsl:value-of select="substring(text(),1,string-length(text())-1)" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="text()" />
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

	<xsl:template match="type">
		<xsl:choose>
			<xsl:when test=".='journal-article'">
				<mods:relatedItem type="host">
					<mods:genre usage="primary"
						valueURI="http://www.mycore.org/classifications/mir_genres#journal"
						authorityURI="http://www.mycore.org/classifications/mir_genres"
						type="intern" />
					<mods:titleInfo>
						<xsl:apply-templates select="container-title" />
					</mods:titleInfo>
					<mods:part>
						<xsl:apply-templates select="volume" />
						<xsl:apply-templates select="issue" />
						<xsl:apply-templates select="article-number" />
					</mods:part>
					<xsl:apply-templates select="ISSN" />
				</mods:relatedItem>
			</xsl:when>
			<xsl:otherwise>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="container-title">
		<mods:titleInfo type="simple">
			<xsl:value-of select="." />
		</mods:titleInfo>
	</xsl:template>

	<xsl:template match="volume">
		<mods:detail type="volume">
			<mods:number>
				<xsl:value-of select="." />
			</mods:number>
		</mods:detail>
	</xsl:template>


	<xsl:template match="issue">
		<mods:detail type="issue">
			<mods:number>
				<xsl:value-of select="." />
			</mods:number>
		</mods:detail>
	</xsl:template>

	<xsl:template match="article-number">
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

	<xsl:template match="issn">
		<mods:identifier type="issn">
			<xsl:value-of select="substring(.,1,4)" />
			<xsl:text>-</xsl:text>
			<xsl:value-of select="substring(.,5)" />
		</mods:identifier>
	</xsl:template>

	<xsl:template match="DOI">
		<mods:identifier type="doi" invalid="yes">
			<xsl:value-of select="." />
		</mods:identifier>
	</xsl:template>

	<xsl:template match="author">
		<mods:name usage="primary" type="personal">
			<mods:displayForm>
				<xsl:value-of select="family/." />
				<xsl:text>, </xsl:text>
				<xsl:value-of select="given/." />
			</mods:displayForm>
			<mods:role>
				<mods:roleTerm type="code" authority="marcrelator">aut
				</mods:roleTerm>
			</mods:role>
		</mods:name>
	</xsl:template>

	<xsl:template match="*|text()" />

</xsl:stylesheet>