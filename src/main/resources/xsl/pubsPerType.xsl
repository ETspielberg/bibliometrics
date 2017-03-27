<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="pubsPerType">
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
            	<xsl:value-of select="." />
            	<xsl:text>
            }]
			});
			}
			</xsl:text>
	</xsl:template>
</xsl:stylesheet>