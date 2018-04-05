  var myRequest = new XMLHttpRequest();
  myRequest.open('GET', 'ajaxresult.html');
  myRequest.onreadystatechange = function () {
    if (myRequest.readyState === 4) {
      document.getElementById('ajax-content').innerHTML = myRequest.responseText;
      console.log(myRequest.responseText);
    }
  };

  function sendTheAJAX() {
    myRequest.send();
    document.getElementById('reveal').style.display = 'none';
  }