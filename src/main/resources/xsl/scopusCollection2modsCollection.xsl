<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:mods="http://www.loc.gov/mods/v3"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xalan="http://xml.apache.org/xalan"
	xmlns:ce="http://www.elsevier.com/xml/ani/common" xmlns:scopus="http://www.elsevier.com/xml/svapi/abstract/dtd"
	xmlns:cto="http://www.elsevier.com/xml/cto/dtd" xmlns:dc="http://purl.org/dc/elements/1.1/"
	xmlns:xoe="http://www.elsevier.com/xml/xoe/dtd" xmlns:xocs="http://www.elsevier.com/xml/xocs/dtd"
	xmlns:prism="http://prismstandard.org/namespaces/basic/2.0/"
	xmlns:ns1="http://webservices.elsevier.com/schemas/search/fast/types/v4"
	xmlns:ait="http://www.elsevier.com/xml/ani/ait"
	exclude-result-prefixes="xsl xalan xsi cto ce dc scopus prism">

	<xsl:output method="xml" encoding="UTF-8" indent="yes"
		xalan:indent-amount="2" />
		
	<xsl:include href="abstract-document-templates.xsl" />

	<xsl:template match="/">
		<mods:modsCollection
			xsi:schemaLocation="http://www.loc.gov/mods/v3 http://www.loc.gov/standards/mods/v3/mods-3-4.xsd">
			<xsl:apply-templates
				select="affiliation-retrieval-response/documents/abstract-document" />
		</mods:modsCollection>
	</xsl:template>

</xsl:stylesheet>