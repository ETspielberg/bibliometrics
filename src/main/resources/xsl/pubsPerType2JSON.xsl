<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xalan="http://xml.apache.org/xalan"
	xmlns:i18n="xalan://org.mycore.services.i18n.MCRTranslation"
	xmlns:mabxml="http://www.ddb.de/professionell/mabxml/mabxml-1.xsd"
								exclude-result-prefixes="xsl xalan i18n mabxml">

	<xsl:output method="text" omit-xml-declaration="yes"
		encoding="UTF-8" />
		
	<xsl:template match="/" >
	<xsl:text>
	{
			chart: {
            plotBackgroundColor: null,
            plotBorderWidth: null,
            plotShadow: false,
            type: 'pie'
        	},
        		title: {
            		text: 'Type of publications'
        	},
        	tooltip: {
            	pointFormat: '{series.name}: &lt;b&gt;{point.percentage:.1f}%&lt;/b&gt;'
        	},
        	plotOptions: {
            	pie: {
                	allowPointSelect: true,
                	cursor: 'pointer',
                	dataLabels: {
                    	enabled: false
                	},
                	showInLegend: true
            	}
        	},
        	series: [{
            	name: 'Type',
            	colorByPoint: true,
            	data:</xsl:text>
            	<xsl:value-of select="bibliometricAnalysis/json/pubsPerType/." />
            	<xsl:text>
            }]
			}
			</xsl:text>
	</xsl:template>

</xsl:stylesheet>