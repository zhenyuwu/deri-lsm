<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
	<script type='text/javascript' src='http://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.js'></script>
	<script type='text/javascript' src='http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.13/jquery-ui.min.js'></script>
	
    <title>Apache Tomcat WebSocket Examples: Echo</title>
    <style type="text/css">
        #connect-container {
            float: left;
            width: 400px
        }

        #connect-container div {
            padding: 5px;
        }

        #console-container {
            float: left;
            margin-left: 15px;
            width: 400px;
        }

        #console {
            border: 1px solid #CCCCCC;
            border-right-color: #999999;
            border-bottom-color: #999999;
            height: 350px;
            overflow-y: scroll;
            padding: 5px;
            width: 100%;
        }

        #console p {
            padding: 0;
            margin: 0;
        }
    </style>
    <script type="text/javascript">
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
//                 var message = document.getElementById('message').value;
//                 log('Sent: ' + message);
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
    </script>
</head>
<body>
<noscript><h2 style="color: #ff0000">Seems your browser doesn't support Javascript! Websockets rely on Javascript being enabled. Please enable
    Javascript and reload this page!</h2></noscript>
<div>
<h2>Test your SSC Socket</h2>
    <div id="connect-container">
        <div>        	
            <span>Please input your SSC socket URL:</span>
            <!-- echo example using streams on the server side -->
<!--             <input id="radio1" type="radio" name="group1" value="" -->
<!--                    onclick="updateTarget(this.value);"> <label for="radio1">streams</label>             -->
        </div>
        <div>
            <input id="target" type="text" size="40" style="width: 350px"/>
        </div>
        <div>
            <button id="connect" onclick="connect();">Connect</button>
            <button id="disconnect" disabled="disabled" onclick="disconnect();">Disconnect</button>
        </div>
<!--         <div>  ws://localhost:8080/SuperStreamCollider/websocket/SSCMessage/64167875522217266 -->
<!--             <textarea id="message" style="width: 350px">Here is a message!</textarea> -->
<!--         </div> -->
        <div>
            <button id="echo" onclick="echo();" disabled="disabled">Submit</button>
        </div>
    </div>
    <div id="console-container">
        <div id="console"></div>
    </div>
</div>
</body>
</html>