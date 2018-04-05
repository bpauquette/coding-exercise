$( getAppointments );

function show(elementID) {
	document.getElementById(elementID).style.display="block";
}

function hide(elementID) {
	document.getElementById(elementID).style.display="none";
}

function toggle(showCreate) {
    if (showCreate) {
      show("hideShowCreate");
      hide("hideShowNew");
    } else {
      hide("hideShowCreate");
      show("hideShowNew");
    }
}

function processAjax() {
	  console.log("processAjax runs");
	  var xhttp = new XMLHttpRequest();
	  xhttp.onreadystatechange = function() {
	    if (this.readyState == 4 && this.status == 200) {
	    	    console.log(this.responseText);
	    	    var jsonData=JSON.parse(this.responseText);  
	            loadTable('outputTable', ['description', 'when'], jsonData);
	    }
	  };
	  searchForText=document.getElementById("searchInput").value;
	  xhttp.open("GET", "/cgi-bin/processor.pl?search=" + searchForText, true);
	  xhttp.send();
	  console.log("processAjax ends");
}

function getAppointments() {
	processAjax();
}


function loadTable(tableId, fields, data) {
    //$('#' + tableId).empty(); //not really necessary
    var rows = '';
    $.each(data, function(index, item) {
        var row = '<tr>';
        $.each(fields, function(index, field) {
            row += '<td>' + item[field+''] + '</td>';
        });
        rows += row + '<tr>';
    });
    $('#' + tableId).html(rows);
}


