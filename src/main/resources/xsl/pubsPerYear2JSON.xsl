<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xalan="http://xml.apache.org/xalan"
	xmlns:i18n="xalan://org.mycore.services.i18n.MCRTranslation"
	xmlns:mabxml="http://www.ddb.de/professionell/mabxml/mabxml-1.xsd"
	 exclude-result-prefixes="xsl xalan i18n mabxml">

	<xsl:output method="text" omit-xml-declaration="yes"
		encoding="UTF-8" />
	
	<xsl:template match="/" >
	{
			chart: {
			zoomType: 'xy'
			},
			title: {
			text: 'Publications'
			},
			xAxis: {
			type: 'linear',
			crosshair: true
			},
			yAxis: [{
			labels: {
			style: {
			color: Highcharts.getOptions().colors[0]
			}
			},
			min: 0,
			title: {
			text: 'Publications per year',
			style: {
			color:
			Highcharts.getOptions().colors[0]
			}
			}
			}],
			tooltip: {
			shared: true
			},
			legend: {
			layout: 'vertical',
			align: 'left',
			x: 120,
			verticalAlign: 'top',
			y: 100,
			floating: true,
			backgroundColor:(Highcharts.theme &amp;&amp; Highcharts.theme.legendBackgroundColor) || '#FFFFFF'
			},
			series: [{
			type: 'column',
			name: 'Publications',
			yAxis: 0,
			data:
		<xsl:value-of select="bibliometricAnalysis/json/pubsPerYear/." />
		}]

			}
	</xsl:template>
	
	
</xsl:stylesheet>