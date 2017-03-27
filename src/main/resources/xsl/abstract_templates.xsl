<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:mods="http://www.loc.gov/mods/v3"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xalan="http://xml.apache.org/xalan"
                xmlns:ce="http://www.elsevier.com/xml/ani/common" xmlns:scopus="http://www.elsevier.com/xml/svapi/abstract/dtd"
                xmlns:cto="http://www.elsevier.com/xml/cto/dtd" xmlns:dc="http://purl.org/dc/elements/1.1/"
                xmlns:prism="http://prismstandard.org/namespaces/basic/2.0/"
                exclude-result-prefixes="xsl xalan xsi cto ce dc scopus prism">

    <xsl:param name="MCR.api.ScopusAffilID" />

    <xsl:template match="bibrecord">
        <xsl:apply-templates select="item-info/itemidlist/ce:doi" />
        <xsl:apply-templates select="head" />
    </xsl:template>

    <xsl:template match="item-info/itemidlist/ce:doi">
        <mods:identifier type="doi" invalid="yes">
            <xsl:value-of select="." />
        </mods:identifier>
    </xsl:template>

    <xsl:template match="head">
        <mods:genre usage="primary"
                    valueURI="http://www.mycore.org/classifications/mir_genres#article"
                    authorityURI="http://www.mycore.org/classifications/mir_genres" type="intern" />
        <xsl:apply-templates select="citation-title/titletext" />
        <xsl:apply-templates select="source" />
        <xsl:apply-templates select="author-group" />
        <xsl:apply-templates select="abstracts/abstract" />
    </xsl:template>

    <xsl:template match="citation-title/titletext">
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
        <mods:titleInfo xml:lang="de" supplied="yes" usage="primary">
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


    <xsl:template match="source">
        <mods:relatedItem type="host">
            <mods:accessCondition type="restriction on access" />
            <mods:genre type="intern"
                        authorityURI="http://www.mycore.org/classifications/mir_genres"
                        valueURI="http://www.mycore.org/classifications/mir_genres#journal"
                        usage="primary" />

            <mods:titleInfo xml:lang="de" supplied="yes" usage="primary">
                <xsl:apply-templates select="sourcetitle" />
            </mods:titleInfo>
            <mods:part>
                <xsl:apply-templates select="volisspag/voliss" />
                <xsl:apply-templates select="article-number" />
                <xsl:apply-templates select="publicationdate/year" />
            </mods:part>
            <xsl:apply-templates select="issn" />
            <mods:language usage="primary">
                <mods:languageTerm authority="rfc4646" type="code">de
                </mods:languageTerm>
            </mods:language>
            <mods:classification authority="sdnb"
                                 displayLabel="sdnb" usage="primary">000</mods:classification>
        </mods:relatedItem>
    </xsl:template>


    <xsl:template match="sourcetitle">
        <mods:title>
            <xsl:value-of select="." />
        </mods:title>
    </xsl:template>


    <xsl:template match="volisspag/voliss">
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
        
        


    <xsl:template match="publicationdate/year">
        <mods:date keyDate="yes" encoding="w3cdtf">
            <xsl:value-of select="." />
        </mods:date>
    </xsl:template>


    <xsl:template match="issn[@type='electronic']">
        <mods:identifier type="issn" invalid="yes">
            <xsl:value-of select="substring(.,1,4)" />
            <xsl:text>-</xsl:text>
            <xsl:value-of select="substring(.,5)" />
        </mods:identifier>
    </xsl:template>


    <xsl:template match="issn">
        <mods:identifier type="issn" invalid="yes">
            <xsl:value-of select="substring(.,1,4)" />
            <xsl:text>-</xsl:text>
            <xsl:value-of select="substring(.,5)" />
        </mods:identifier>
    </xsl:template>
        
    <xsl:template match="author-group">
        <xsl:apply-templates select="author">
            <xsl:sort select="@seq" data-type="number" />
        </xsl:apply-templates>
    </xsl:template>


    <xsl:template match="author-group/author">
        <xsl:variable name="authorGroupID">
            <xsl:value-of select="../affiliation/@afid" />
        </xsl:variable>
        <xsl:choose>
            <xsl:when test="$authorGroupID=$MCR.api.ScopusAffilID">
                <mods:name usage="primary" type="personal"
                           authorityURI="http://www.lsf.uni-due.de" valueURI="https://www.uni-due.de/zim/services/suchdienste/mitarbeiter.php?id=0">
                    <xsl:apply-templates select="ce:indexed-name" />
                    <mods:role>
                        <mods:roleTerm type="code" authority="marcrelator">aut
                        </mods:roleTerm>
                    </mods:role>
                </mods:name>
            </xsl:when>
            <xsl:otherwise>
                <mods:name usage="primary" type="personal">
                    <xsl:apply-templates select="ce:indexed-name" />
                    <mods:role>
                        <mods:roleTerm type="code" authority="marcrelator">aut
                        </mods:roleTerm>
                    </mods:role>
                </mods:name>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>


    <xsl:template match="author-group/author/ce:indexed-name">
        <mods:displayForm>
            <xsl:value-of select="." />
        </mods:displayForm>
    </xsl:template>
        
        
    <xsl:template match="abstracts/abstract[@xml:lang='eng']">
        <mods:abstract xml:lang="eng" shareable="no">
            <xsl:apply-templates select="ce:para" />
        </mods:abstract>
    </xsl:template>


    <xsl:template match="abstracts/abstract/ce:para">
        <xsl:value-of select="." />
    </xsl:template>

</xsl:stylesheet>