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
			<xsl:apply-templates select="author-retrieval-response/author-profile" />
		</person>
	</xsl:template>

	<xsl:template match="author-profile">
			<surname>
				<xsl:value-of select="preferred-name/surname/." />
			</surname>
			<given-name>
				<xsl:value-of select="preferred-name/given-name/." />
			</given-name>
			<scopusAuthorID>
				<xsl:value-of select="substring(../coredata/dc:identifier/.,11)" />
			</scopusAuthorID>
			<xsl:if test="orcid">
				<orcid>
					<xsl:value-of select="../coredata/orcid/." />
				</orcid>
			</xsl:if>
			<affiliation>
				<xsl:value-of select="affiliation-current/affiliation /ip-doc/preferred-name/." />
			</affiliation>
	</xsl:template>
	
</xsl:stylesheet>