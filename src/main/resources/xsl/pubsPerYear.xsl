<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="pubsPerYear">
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
			data:<xsl:value-of select="." />
			}]

			}
	</xsl:template>
</xsl:stylesheet>