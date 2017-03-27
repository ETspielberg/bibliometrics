<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:mods="http://www.loc.gov/mods/v3"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xalan="http://xml.apache.org/xalan"
	xmlns:ce="http://www.elsevier.com/xml/ani/common" xmlns:scopus="http://www.elsevier.com/xml/svapi/abstract/dtd"
	xmlns:cto="http://www.elsevier.com/xml/cto/dtd" xmlns:dc="http://purl.org/dc/elements/1.1/"
	xmlns:prism="http://prismstandard.org/namespaces/basic/2.0/"
	exclude-result-prefixes="xsl xalan xsi cto ce dc scopus prism">

	<xsl:template match="abstract-document">
		<mods:mods xmlns:mods="http://www.loc.gov/mods/v3">
		<mods:typeOfResource>text</mods:typeOfResource>
			<mods:genre
				valueURI="http://www.mycore.org/classifications/mir_genres#article"
				authorityURI="http://www.mycore.org/classifications/mir_genres"
				type="intern" />
			<xsl:apply-templates select="dc:title" />
			<xsl:apply-templates select="prism:doi" />
			<xsl:apply-templates select="prism:coverDate" />
			<mods:accessCondition type="use and reproduction" >rights_reserved</mods:accessCondition>
			<mods:relatedItem type="host">
				<xsl:apply-templates select="prism:publicationName" />
				<xsl:apply-templates select="prism:issn" />
				<mods:part>
					<xsl:apply-templates select="prism:pageRange" />
					<xsl:apply-templates select="prism:volume" />
					<xsl:apply-templates select="prism:issueIdentifier" />
				</mods:part>
			</mods:relatedItem>
			<xsl:apply-templates select="authors" />
			<mods:extension>
				<sourcetext type="scopus">
					<xsl:copy-of select="." />
				</sourcetext>
			</mods:extension>
		</mods:mods>
	</xsl:template>

	<xsl:template match="dc:title">
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


	<xsl:template match="prism:publicationName">
		<mods:titleInfo>
			<mods:title>
				<xsl:value-of select="." />
			</mods:title>
		</mods:titleInfo>
	</xsl:template>

	<xsl:template match="prism:volume">
		<mods:detail type="volume">
			<mods:number>
				<xsl:value-of select="." />
			</mods:number>
		</mods:detail>
	</xsl:template>
	
	<xsl:template match="prism:coverDate">
	<mods:originInfo eventType="publication">
		<mods:dateIssued encoding="w3cdtf" >
				<xsl:value-of select="." />
		</mods:dateIssued>
		</mods:originInfo>
	</xsl:template>


	<xsl:template match="prism:issueIdentifier">
		<mods:detail type="issue">
			<mods:number>
				<xsl:value-of select="." />
			</mods:number>
		</mods:detail>
	</xsl:template>


	<xsl:template match="prism:pageRange">
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

	<xsl:template match="prism:issn">
		<mods:identifier type="issn">
			<xsl:value-of select="substring(.,1,4)" />
			<xsl:text>-</xsl:text>
			<xsl:value-of select="substring(.,5)" />
		</mods:identifier>
	</xsl:template>
	
	<xsl:template match="prism:doi">
		<mods:identifier type="doi">
			<xsl:value-of select="." />
		</mods:identifier>
	</xsl:template>


	<xsl:template match="authors">
		<xsl:apply-templates select="author">
			<xsl:sort select="authseq" data-type="number" />
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="author">
		<mods:name type="personal">
		<xsl:apply-templates select="authname" />
			<mods:role>
				<mods:roleTerm type="code" authority="marcrelator">aut
				</mods:roleTerm>
			</mods:role>
			<xsl:apply-templates select="authid" />
			</mods:name>
	</xsl:template>

	<xsl:template match="authname">
		<mods:displayForm>
			<xsl:value-of select="." />
		</mods:displayForm>
	</xsl:template>

	<xsl:template match="*|text()" />

	<xsl:template match="authid">
		<mods:nameIdentifier type="scopusAuthorID">
			<xsl:value-of select="." />
		</mods:nameIdentifier>
	</xsl:template>

</xsl:stylesheet>