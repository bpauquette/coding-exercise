$( getAppointments() );

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
	            loadTable('outputTable', jsonData);
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

function zeroPad(number) {
	if (number<10) {
		return '0' + number;
	} else {
		return ''  + number;
	}
}


function populateHours() {
var select = '';
for (i=1;i<=12;i++){
    select += '<option val=' + zeroPad(i) + '>' + zeroPad(i) + '</option>';
}
$('#hour').html(select);
}
function populateMinutes() {
	var select = '';
	for (i=0;i<=59;i++){
	    select += '<option val=' + zeroPad(i) + '>' + zeroPad(i) + '</option>';
	}
	$('#minute').html(select);
}
function populateSeconds() {
	var select = '';
	for (i=0;i<=59;i++){
	    select += '<option val=' + zeroPad(i) + '>' + zeroPad(i) + '</option>';
	}
	$('#second').html(select);
}


function loadTable(tableId, data) {
    var rows = '';
    var appointment;
    rows+="<tr><th>Date</th><th>Time</th><th>Description</th><th>Duration</th></tr>";
    for(i =0;i<data.length;i++){
    	
    	rows+="<tr>";
    	appointment=data[i];
    	var appointment_id=appointment.appointment_id;
    	/*rows +="<td>";
    	rows += appointment_id;
    	rows +="</td>";  */
     	
    	rows +="<td>";
    	rows += appointment.when.date.year;
    	rows += '/';
    	rows += zeroPad(appointment.when.date.month);
    	rows += '/';
    	rows += zeroPad(appointment.when.date.day);
    	rows += "</td>";
        rows += "<td>";
    	rows += zeroPad(appointment.when.time.hour);
    	rows += ":";
    	rows += zeroPad(appointment.when.time.minute);
    	rows += ":";
    	rows += zeroPad(appointment.when.time.second);   	
    	rows +="</td>";
    	rows +="<td>";
     	rows += appointment.description;
    	rows +="</td>";
    	rows += "<td>"
    	rows += appointment.duration_in_minutes + " minutes" 
    	rows += "</td>"    
    	rows +="</tr>";
    }
    $('#' + tableId).html(rows);
}


