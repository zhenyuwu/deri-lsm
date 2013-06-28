<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
<head>
<script type="text/javascript" src="js/VisualSP/jquery.min.js"></script>
<script type="text/javascript" src="js/VisualSP/d3/d3.js"></script>
<script type="text/javascript" src="js/VisualSP/d3/d3.layout.js"></script>
<script type="text/javascript" src="js/VisualSP/d3/d3.geom.js"></script>
<script type="text/javascript" src="js/VisualSP/namespace.js"></script>
<script type="text/javascript" src="js/VisualSP/SparqlParser.js"></script>
<script type="text/javascript" src="js/VisualSP/sparqlApi.js"></script>
<!-- <script type="text/javascript" src="js/jquery.bpopup-0.7.0.min.js"></script> -->

<link href='css/VisualSP/style.css' rel='stylesheet' type='text/css' />
<link href='css/VisualSP/dialog.css' rel='stylesheet' type='text/css' />
<link href='css/VisualSP/components.css' rel='stylesheet' type='text/css' />
<link href='css/VisualSP/addNodeStyles.css' rel='stylesheet' type='text/css' />

<title>Visual SPARQL</title>
</head>
<body>
<h1 align="center">LSM Visual SPARQL</h1>
<div style="height:420px" class='canvas'>
<div>
	<div style="background-color:#F7F4E5;width:500px;float: left;border-width: 1px; border-style: solid;
	border: 1px solid #346789;border-radius: 0.5em 0.5em 0.5em 0.5em;z-index:20; 
	-o-box-shadow: 0px 0px 17px #044;-webkit-box-shadow: 0px 0px 17px #044; -moz-box-shadow: 0px 0px 17px #044;box-shadow: 0px 0px 17px #044;
	" class='gallery' id='chart'></div>
	<div style="float:left;color:red" id="msg"></div>

	<textarea  cols="60" rows="20" style="float:left;width:450px" id="query" class="sparql">
	PREFIX foaf: <http://xmlns.com/foaf/0.1/>
	SELECT ?person ?email WHERE {
		?person a foaf:Person;
	    foaf:mbox ?email .
	}
	</textarea>

	<br/>
	
</div>
<div id="dialog-overlay"></div>
<div id="dialog-box">
    <div class="dialog-content">
        <div id="dialog-message"></div>
        <a href="#" class="button">Close</a>
    </div>
</div>
<div id="panel" style="display:none">
<input type="text" id="nodename" value="?x"/><br/>
  <button id="submitNode" class="button">Add</button>
</div>
<div id="predDialog" style="display:none">
  <input type="text" id="predname" value="foaf:based_near"/><br/>
  <button id="submitPred">Add</button>
</div>
</div>

<script src="js/VisualSP/popup.js"></script>

<div id="modal" class="modal">
	<div id="heading">
		Please input node
	</div>
	<div class="content">
		<div>
			<input type="text" id="txtContent" class="txtContent"/>
		</div>
		<br/>
		<a id="Ok" href="#" class="buttonPopup green close"><img src="images/tick.png">Ok</a>
		<a id="cancel" href="#" class="buttonPopup red close"><img src="images/cross.png">Cancel</a>
	</div>
</div>

<div id="preModal" class="modal">
	<div id="heading">
		Please input predicate
	</div>
	<div class="content">
		<div>
			<input type="text" id="txtPreContent" class="txtContent"/>
		</div>
		<br/>
		<a id="preOk" href="#" class="buttonPopup green close"><img src="images/tick.png">Ok</a>
		<a id="preCancel" href="#" class="buttonPopup red close"><img src="images/cross.png">Cancel</a>
	</div>
</div>

<div style="display: inline;">
<ul id="menu-css">  
    <li><a id="addNode" href="#">Add node</a></li>  
    <li><a id="addPre" href="#">Add predicate</a></li>  
<!--     <li><a id="delNode" href="#">Delete node</a></li>   -->
<!--     <li><a id="clear" href="#">Clear all</a></li>   -->
    <li><a id="close" href="#">Close</a></li>
    <button id="redraw" class="button">Redraw</button>
</ul>
<!-- <table style="border: 1px; border-style:solid"> -->
<!--   <tr><th>Action</th><th>Event</th></tr> -->
<!--   <tr><td><strong>Double-click on screen</strong></td><td>Create a new node</td></tr> -->
<!--   <tr><td><strong>Double-click on node</strong></td><td>Create a new link between nodes</td></tr> -->
<!--   <tr><td><strong>Single-click on node</strong></td><td>Select node</td></tr> -->
<!-- </table> -->
</div>
<script type="text/javascript" src='js/VisualSP/main.js'></script>
<script type="text/javascript" src="js/VisualSP/events.js"></script>
</body>
</html>