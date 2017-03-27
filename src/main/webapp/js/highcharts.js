jQuery(document).ready(
    function() {
      Highcharts
          .setOptions({
            lang : {
              months : [ 'Januar', 'Februar', 'März', 'April', 'Mai', 'Juni',
                  'Juli', 'August', 'September', 'Oktober', 'November',
                  'Dezember' ],
              weekdays : [ 'Sonntag', 'Montag', 'Dienstag', 'Mittwoch',
                  'Donnerstag', 'Freitag', 'Samstag' ],
              resetZoom : 'Zoom zurücksetzen',
              printChart : 'Diagramm drucken',
              downloadPNG : 'Als PNG herunterladen',
              downloadJPEG : 'Als JPG herunterladen',
              downloadPDF : 'Als PDF herunterladen',
              downloadSVG : 'Als SVG herunterladen'
            },
            exporting : {
              width : jQuery(window).width() - 100
            }
          });

      jQuery("div.highchart").each(
          function(index, value) {
            var chartDiv = value;
            var docNumber = jQuery(chartDiv).data("docnumber");
            jQuery.getJSON('series?docNumber=' + docNumber, function(data) {
              chart = new Highcharts.Chart({
                chart : {
                  renderTo : chartDiv,
                  defaultSeriesType : 'area',
                  zoomType : 'xy'
                },
                title : {
                  text : data.edition + ". Aufl. " + data.shelfmark
                },
                subtitle : {
                  text : '' + data.numItems + ' Exemplare'
                },
                xAxis : {
                  type : 'datetime',
                  maxZoom : 7 * 24 * 3600000,
                  dateTimeLabelFormats : {
                    month : '%b %y'
                  }
                },
                yAxis : {
                  title : {
                    text : 'Anzahl'
                  },
                  min : 0,
                  allowDecimals : false
                },
                tooltip : {
                  formatter : function() {
                    return Highcharts.dateFormat('%e. %b %y', this.x)
                        + '<br/>' + this.y + ' ' + this.series.name;
                  }
                },
                plotOptions : {
                  area : {
                    marker : {
                      enabled : false,
                      symbol : 'circle',
                      radius : 1,
                      states : {
                        hover : {
                          enabled : true
                        }
                      }
                    }
                  }
                },
                colors : [ '#4572A7', '#AA4643', '#89A54E', '#80699B',
                    '#3D96AE', '#DB843D', '#92A8CD', '#A47D7C', '#B5CA92' ],
                series : data.series
              });
            });
          });

    });
