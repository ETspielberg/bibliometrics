jQuery(document).ready(function() {
  var dtItems = {
    "bPaginate" : true,
    "sPaginationType" : "full_numbers",
    "bLengthChange" : true,
    "sDom" : 'ifplt',
    "bJQueryUI" : true,
    "aaSorting" : [ [ 1, 'asc' ] ],
    "bAutoWidth" : false,
    "aoColumns" : [ {
      "sWidth" : "45px"
    }, {
      "sWidth" : "150px"
    }, {
      "sWidth" : "80px"
    }, {
      "sWidth" : "90px"
    }, {
      "sWidth" : "290px"
    } ]
  };
  var dtEvents = {
    "bPaginate" : true,
    "sPaginationType" : "full_numbers",
    "bLengthChange" : true,
    "sDom" : 'ifplt',
    "bJQueryUI" : true,
    "bAutoWidth" : false,
    "aoColumns" : [ {
      "sWidth" : "130px"
    }, {
      "sWidth" : "130px",
      "bSearchable" : false
    }, {
      "sWidth" : "60px",
      "bSearchable" : false
    }, {
      "sWidth" : "100px"
    }, {
      "sWidth" : "100px"
    }, {
      "sWidth" : "50px"
    }, {
      "sWidth" : "150px"
    } ]
  };
  var dtDeletions = {
		    "bPaginate" : true,
		    "sPaginationType" : "full_numbers",
		    "bLengthChange" : true,
		    "sDom" : 'ifplt',
		    "bJQueryUI" : true,
		    "bAutoWidth" : false,
		    "aoColumns" : [ {
		      "sWidth" : "130px"
		    }, {
		      "sWidth" : "130px",
		      "bSearchable" : false
		    }, {
		      "sWidth" : "130px",
		      "bSearchable" : false
		    }, {
		      "sWidth" : "130px",
		      "bSearchable" : false
		    }, {
		      "sWidth" : "130px",
		      "bSearchable" : false
		    }, {
		      "sWidth" : "130px",
		      "bSearchable" : false
		    }, {
		      "sWidth" : "130px",
		      "bSearchable" : false
		    }, {
		      "sWidth" : "130px",
		      "bSearchable" : false
		    }, {
		      "sWidth" : "130px",
		      "bSearchable" : false
		    }, {
		      "sWidth" : "130px",
		      "bSearchable" : false
		    } ]
		  };
  jQuery('.datatable.items').dataTable(dtItems);
  jQuery('.datatable.events').dataTable(dtEvents);
  jQuery('.datatable.deletions').dataTable(dtDeletions);
  $('.datatable.deltionTable').DataTable();
});
