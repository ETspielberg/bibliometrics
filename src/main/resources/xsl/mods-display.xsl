<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet version="1.0" 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
  xmlns:mods="http://www.loc.gov/mods/v3"
  xmlns:xalan="http://xml.apache.org/xalan" 
  xmlns:encoder="xalan://java.net.URLEncoder" 
  xmlns:xlink="http://www.w3.org/1999/xlink">
  
 
  <!-- ========== Tab Details (Tabellenlayout) ========== -->
  <xsl:template match="mods:mods">
    <tr>
    	<td>
			<xsl:apply-templates select="mods:name" />
			<xsl:choose>
			<xsl:when test="mods:identifier[@type='doi']">
        	<xsl:apply-templates select="mods:identifier[@type='doi']"/>
        	</xsl:when>
        	<xsl:otherwise>
        	<xsl:apply-templates select="mods:titleInfo"/>
        	</xsl:otherwise>
        	</xsl:choose>
			<xsl:apply-templates select="mods:relatedItem" />
			<xsl:apply-templates select="mods:originInfo" />
		</td>
		<td>
		<xsl:apply-templates select="mods:originInfo" mode="plain" />
		</td>
		<td>
		<xsl:apply-templates select="citationInformation" />
		</td>
    </tr>
  </xsl:template>
  
  <xsl:template match="citationInformation">
  <xsl:choose>
		<xsl:when test="citationLink">
		<xsl:element name="a">
		<xsl:attribute name="href">
		<xsl:value-of select="citationLink/." />
		</xsl:attribute>
		<xsl:attribute name="target">popup</xsl:attribute>
		<xsl:value-of select="count/."/>
		</xsl:element>
		</xsl:when>
		<xsl:otherwise>
		<xsl:value-of select="count/."></xsl:value-of>
		</xsl:otherwise>
		</xsl:choose>
  </xsl:template>
  
    <!-- ========== DOI ========== -->
  <xsl:template match="mods:identifier[@type='doi']">
    <a href="http://dx.doi.org/{text()}" target="popup">
      <xsl:apply-templates select="../mods:titleInfo" />
    </a>
  </xsl:template>
  
  
  <!-- ========== ISSN ========== -->
  <xsl:template match="mods:identifier[@type='issn']">
    <xsl:variable name="parameters" select="concat('genre=journal&amp;sid=bib:ughe&amp;pid=bibid%3DUGHE&amp;issn=',text())" />
   <a href="http://www.uni-due.de/ub/ghbsys/jop?{$parameters}" title="JOP" target="popup">
    <xsl:value-of select="../mods:titleInfo/." />
      </a>
  </xsl:template>
  
   <!-- ========== Titel ========== -->
  <xsl:template match="mods:titleInfo">
    <xsl:apply-templates select="mods:nonSort" />
    <xsl:apply-templates select="mods:title" />
    <xsl:apply-templates select="mods:subTitle" />
	<xsl:text>;</xsl:text>
  </xsl:template>

  
<!-- ========== Führende Artikel: Der, Die, Das ========== -->
  <xsl:template match="mods:nonSort">
    <xsl:value-of select="text()" />
    <xsl:text> </xsl:text>
  </xsl:template>

  <!-- ========== Haupttitel ========== -->
  <xsl:template match="mods:title">
    <xsl:value-of select="." />
  </xsl:template>
  


  <!-- ========== Untertitel ========== -->
  <xsl:template match="mods:subTitle">
    <xsl:variable name="lastCharOfTitle" select="substring(../mods:title,string-length(../mods:title))" />
    <!-- Falls Titel nicht mit Satzzeichen endet, trenne Untertitel durch : -->
    <xsl:if test="translate($lastCharOfTitle,'?!.:,-;','.......') != '.'">
      <xsl:text> :</xsl:text>
    </xsl:if>
    <xsl:text> </xsl:text>
    <xsl:value-of select="text()" />
  </xsl:template>

<!-- ========== Personenname ========== -->
  <xsl:template match="mods:name">
    <xsl:value-of select="mods:displayForm/." />
    <xsl:choose>
    <xsl:when test="position() != last()">
    <xsl:text>; </xsl:text>
    </xsl:when>
    <xsl:otherwise>
    <xsl:text>: </xsl:text>
    </xsl:otherwise>
    </xsl:choose>  
  </xsl:template>
  
   <!-- ========== Auflage, Ort : Verlag, Jahr ========== -->
  <xsl:template match="mods:originInfo" mode="plain">
    <xsl:value-of select="substring(mods:dateIssued/.,1,4)" />
  </xsl:template>
  
  <xsl:template match="mods:originInfo">
  <b> <xsl:text> (</xsl:text>
    <xsl:value-of select="substring(mods:dateIssued/.,1,4)" />
	<xsl:text>)</xsl:text>
	</b>
	<xsl:text>.</xsl:text>
  </xsl:template>
  
  <xsl:template match="mods:relatedItem[@type='host']">
  <xsl:choose>
  <xsl:when test="mods:identifier[@type='issn']">
  <xsl:apply-templates select="mods:identifier[@type='issn']"/>
  </xsl:when>
  <xsl:otherwise>
  <xsl:value-of select="mods:titleInfo"/>
  </xsl:otherwise>
  </xsl:choose>
  <xsl:apply-templates select="mods:part" />
  </xsl:template>

  <!-- ========== Band/Jahrgang, Heftnummer, Seitenangaben ========== -->
  <xsl:template match="mods:part">
    <xsl:apply-templates select="mods:detail[@type='volume']" />
    <xsl:apply-templates select="mods:detail[@type='issue']" />
    <xsl:apply-templates select="mods:detail[@type='page']" />
    <xsl:apply-templates select="mods:extent[@unit='pages']" />
  </xsl:template>
  
  <!-- ========== Band/Jahrgang ========== -->
  <xsl:template match="mods:detail[@type='volume']">
    <xsl:text>, Vol. </xsl:text>
    <xsl:value-of select="mods:number/." />
  </xsl:template>

  <!-- ========== Heftnummer ========== -->
  <xsl:template match="mods:detail[@type='issue']">
    <xsl:if test="../mods:detail[@type='volume']">, </xsl:if>
    
    <xsl:text>Iss. </xsl:text>
	<i>
    <xsl:value-of select="mods:number" />
	</i>
  </xsl:template>
  
  <!-- ========== Einzelne Seite ========== -->
  <xsl:template match="mods:detail[@type='page']">
    <xsl:if test="../mods:detail[not(@type='page')]">
      <xsl:text>, </xsl:text>
    </xsl:if>
    <xsl:text> </xsl:text>
    <xsl:value-of select="mods:number" />
  </xsl:template>

  <!-- ========== Seiten von-bis ========== -->
  <xsl:template match="mods:extent[@unit='pages']">
    <xsl:apply-templates select="mods:start|mods:end" />
  </xsl:template>

  <xsl:template match="mods:start">
    <xsl:text>, S. </xsl:text>
    <xsl:value-of select="text()" />
  </xsl:template>
  
  <xsl:template match="mods:end">
    <xsl:text> - </xsl:text>
    <xsl:value-of select="text()" />
  </xsl:template>

</xsl:stylesheet>
