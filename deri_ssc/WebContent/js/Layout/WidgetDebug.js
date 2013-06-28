$(document).ready(function(){
	var ws = null;
	var socketId;
    function setConnected(connected) {
        document.getElementById('connect').disabled = connected;
        document.getElementById('disconnect').disabled = !connected;
        document.getElementById('echo').disabled = !connected;
    }

    function connect() {
        var target = document.getElementById('target').value;
        var n=target.lastIndexOf("/");
        socketId = target.substring(n+1);
        target = target.substring(0,n);
        
        if (target == '') {
            alert('Please select server side connection implementation.');
            return;
        }
        if ('WebSocket' in window) {
            ws = new WebSocket(target);
        } else if ('MozWebSocket' in window) {
            ws = new MozWebSocket(target);
        } else {
            alert('WebSocket is not supported by this browser.');
            return;
        }
        ws.onopen = function () {
            setConnected(true);
            log('Info: WebSocket connection opened.');
        };
        ws.onmessage = function (event) {
        	var obj = $.parseJSON(event.data); 
            log('Received: ' + obj.ntriples);
        };
        ws.onclose = function () {
            setConnected(false);
            log('Info: WebSocket connection closed.');
        };
    }

    function disconnect() {
        if (ws != null) {
            ws.close();
            ws = null;
        }
        setConnected(false);
    }

    function echo() {
        if (ws != null) {
            ws.send(socketId);
        } else {
            alert('WebSocket connection not established, please connect.');
        }
    }

    function updateTarget(target) {
        document.getElementById('target').value = 'ws://' + window.location.host + target;
    }

    function log(message) {
        var console = document.getElementById('console');
        var p = document.createElement('p');
        p.style.wordWrap = 'break-word';
        p.appendChild(document.createTextNode(message));
        console.appendChild(p);
        while (console.childNodes.length > 25) {
            console.removeChild(console.firstChild);
        }
        console.scrollTop = console.scrollHeight;
    }
});
