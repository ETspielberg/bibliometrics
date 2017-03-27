<?xml version="1.0" encoding="UTF-8"?>



<xsl:stylesheet version="1.0"

	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:mods="http://www.loc.gov/mods/v3"

	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xalan="http://xml.apache.org/xalan"

	xmlns:ce="http://www.elsevier.com/xml/ani/common"
	
	xmlns:scopus="http://www.elsevier.com/xml/svapi/abstract/dtd"

	xmlns:cto="http://www.elsevier.com/xml/cto/dtd" xmlns:dc="http://purl.org/dc/elements/1.1/"

	exclude-result-prefixes="xsl xalan xsi cto ce dc scopus">

	<xsl:output method="xml" encoding="UTF-8" indent="yes"
		xalan:indent-amount="2" />

	<!-- ============ Start at top root element ============ -->


	<xsl:template match="/scopus:abstracts-retrieval-response">

		<mods:mods>
			
			<xsl:apply-templates select="/scopus:abstracts-retrieval-response/scopus:item/scopus:bibrecord" />
			
			<mods:extension>
			
			<mods:sourcetext type="scopus">
			
			<xsl:copy-of select="/scopus:abstracts-retrieval-response"></xsl:copy-of>
			
			</mods:sourcetext>
			
			</mods:extension>

		</mods:mods>
		

	</xsl:template>



	<xsl:template match="/scopus:abstracts-retrieval-response/scopus:item/scopus:bibrecord">
		
		<xsl:apply-templates select="scopus:head" />

	</xsl:template>



	<xsl:template match="scopus:head">

		<xsl:apply-templates select="scopus:citation-title/scopus:titletext" />

		<xsl:apply-templates select="scopus:source" />

		<xsl:apply-templates select="scopus:author-group" />

		<xsl:apply-templates select="scopus:abstracts/scopus:abstract" />
		
	</xsl:template>




	<xsl:template match="scopus:citation-title/scopus:titletext">

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


	<xsl:template match="scopus:source">

		<mods:relatedItem type="host">

			<mods:genre usage="primary"
				valueURI="http://www.mycore.org/classifications/mir_genres#journal"
				authorityURI="http://www.mycore.org/classifications/mir_genres"
				type="intern" />

			<mods:titleInfo>

				<xsl:apply-templates select="scopus:sourcetitle" />

			</mods:titleInfo>


			<mods:part>

				<xsl:apply-templates select="scopus:volisspag/scopus:voliss" />

				<xsl:apply-templates select="scopus:article-number" />

			</mods:part>

			<xsl:apply-templates select="scopus:issn" />

		</mods:relatedItem>

	</xsl:template>


	<xsl:template match="scopus:sourcetitle">

		<mods:titleInfo type="simple">

			<xsl:value-of select="." />

		</mods:titleInfo>

	</xsl:template>


	<xsl:template match="scopus:volisspag/scopus:voliss">

		<mods:detail type="volume">

			<mods:number>

				<xsl:value-of select="@volume" />

			</mods:number>

		</mods:detail>

		<mods:detail type="issue">

			<mods:number>

				<xsl:value-of select="@issue" />

			</mods:number>

		</mods:detail>

	</xsl:template>


	<xsl:template match="scopus:article-number">

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


	<xsl:template match="scopus:issn[@type='electronic']">

		<mods:identifier type="issn">

			<xsl:value-of select="substring(.,1,4)" />

			<xsl:text>-</xsl:text>

			<xsl:value-of select="substring(.,5)" />

		</mods:identifier>

	</xsl:template>


	<xsl:template match="scopus:abstracts/scopus:abstract[@xml:lang='eng']">

		<mods:abstract xml:lang="eng" shareable="no">

			<xsl:apply-templates select="ce:para" />

		</mods:abstract>

	</xsl:template>


	<xsl:template match="scopus:abstracts/scopus:abstract/ce:para">

		<xsl:value-of select="." />

	</xsl:template>


	<xsl:template match="scopus:author-group">

		<xsl:apply-templates select="scopus:author">

			<xsl:sort select="@seq" data-type="number" />

		</xsl:apply-templates>

	</xsl:template>


	<xsl:template match="scopus:author-group/scopus:author">

		<mods:name usage="primary" type="personal">

			<xsl:apply-templates select="ce:indexed-name" />

			<mods:role>

				<mods:roleTerm type="code" authority="marcrelator">aut
				</mods:roleTerm>

			</mods:role>

		</mods:name>

	</xsl:template>


	<xsl:template match="scopus:author-group/scopus:author/ce:indexed-name">

		<mods:displayForm>

			<xsl:value-of select="." />

		</mods:displayForm>

	</xsl:template>


	<xsl:template match="text()" />
	

</xsl:stylesheet>