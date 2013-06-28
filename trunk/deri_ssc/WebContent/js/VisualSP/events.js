
var creatingLink = false;
var sourceNode, targetNode = undefined;
var sourceCircle, targetCircle = undefined;

function changeSelects(d){
  if(d.type != "var"){
  	errorMsg("Only variables can be selected");
  }else{  	  	
  	var newSelects;  
  	var query = d3.select("#query").text();
  	var selects = query.match(/SELECT (.*)WHERE/);
  	if(selects[1].indexOf(d.name+" ") < 0){
  	  newSelects = "SELECT "+selects[1]+d.name+' WHERE';
  	  query = query.replace(/SELECT(.*)WHERE/, newSelects);
  	  d3.select("#query").text(query); 	
  	  return true;	  
  	}else if(selects[1].indexOf(d.name+" ") >= 0){
  	  var aux = selects[1].replace(d.name+" ", "");
  	  newSelects = "SELECT "+aux+"WHERE";
  	  query = query.replace(/SELECT(.*)WHERE/, newSelects);
  	  d3.select("#query").text(query);
  	  return true;
  	}
  	return false;
  }
}

$("circle.node").live("dblclick", function(d){ 
	console.log("double click on circle");
	$("#panel").css("display", "none");
	if(creatingLink == false){
	  sourceCircle = d3.select(this);
	  sourceCircle.style("fill", selectColor);
	  creatingLink = true;
	  sourceNode= sourceCircle.attr("id");
	}else{
//	  $("#predDialog").css("display", "inline");
	  if(targetCircle != undefined){
	  	targetCircle.style("fill", normalColor);
	  }
	  targetCircle = d3.select(this);
	  targetCircle.style("fill", selectColor);
	  targetNode = targetCircle.attr("id");
	}
	return false;
})


d3.selectAll("circle.node").on("click", function(d){
	changed = changeSelects(d);
	if(changed){
	  if(d3.select(this).style("fill") == normalColor){
	  	d3.select(this).style("fill", varColor); 
	  }else{
	  	d3.select(this).style("fill", normalColor);
	  }
	}
});
d3.selectAll("text.link").on("click", function(d){
	changed = changeSelects(d);
	if(changed){
	  if(d3.select(this).style("stroke") == normalLink){
	  	d3.select(this).style("fill", "black"); 
	  	d3.select(this).style("stroke", varLink); 
	  }else{
	  	d3.select(this).style("fill", "white");
	  	d3.select(this).style("stroke", normalLink);
	  }
	}
});

$("#msg").change(function(d){
	setTimeout(function() {  
		$('#msg').fadeOut('fast');  
	}, 1000);
});

vis.on("dblclick", function(d){
	
	//Since it is not possible to stop event propagation
	//on live events, I need to check if the click was done
	//on a circle by calculating the distance between
	//the mouse pointer and each circle
	var mouseCoords = d3.svg.mouse(this);
	var mouseX = parseFloat(mouseCoords[0]);
	var mouseY = parseFloat(mouseCoords[1]);
	var inCircle = false;
	for(var i=0; i<nodes.length; i++){
	  var circleX = parseFloat(nodes[i].px);
	  var circleY = parseFloat(nodes[i].py);
	  var distance = Math.sqrt(
	  	Math.pow((circleX - mouseX), 2) +
	  	Math.pow((circleY - mouseY), 2)
	  	);
	  if(distance < 11){
	  console.log(distance);
	  	inCircle = true;
	  }
	}
	if(!inCircle){
//		alert('add node');
	  console.log("double click svg");
	  $("#panel").css("display", "inline");
	}
})


//Check that sparql query syntax is OK after typing
var typingTimer;                //timer identifier
var doneTypingInterval = 1200;  //time in ms, 5 second for example

$('#query').keyup(function(){
    typingTimer = setTimeout(doneTyping, doneTypingInterval);
});

$('#query').keydown(function(){
    clearTimeout(typingTimer);
});

$("#submitPred").on("click", function(){
    drawPre($("#predname").val());
});

$("#newnode").on("click", function(){
//	$("#panel").css("display", "inline");
});

$("#submitNode").on("click", function(){	
	drawNode();
});


$("#addNode").on("click", function(){
	$('#modal').reveal({ // The item which will be opened with reveal
	  	animation: 'fade',                   // fade, fadeAndPop, none
		animationspeed: 600,                       // how fast animtions are
		closeonbackgroundclick: true,              // if you click background will modal close?
		dismissmodalclass: 'close'    // the class of a button or element that will close an open modal
	});
//	 $("#panel").css("display", "inline");
	 return false;
});

$("#addPre").on("click", function(){
	$('#preModal').reveal({ // The item which will be opened with reveal
	  	animation: 'fade',                   // fade, fadeAndPop, none
		animationspeed: 600,                       // how fast animtions are
		closeonbackgroundclick: true,              // if you click background will modal close?
		dismissmodalclass: 'close'    // the class of a button or element that will close an open modal
	});
//	 $("#panel").css("display", "inline");
	 return false;
});

$("#close").on("click", function(){
	$("#txtQuery",opener.document).val($('#query').val());
//	alert($(this).parent().parent().parent().parent().attr("id"));
//	var c = window.parentId;
//	opener.metaHash.get(c).setSparql($('#query').val());
	window.close();	
});

function drawNode(){
	newNode = $("#nodename").val();
	var nodeListSize = nodes.length;
	var addedNode = sp.createNode(newNode);
	if(nodeListSize > addedNode){
	  alert("Node \""+newNode+"\" already exists!");
	}else{
	  var json = {
	  	nodes: sp.getNodes(),
	  	links: sp.getLinks()
	  };
	  init(json);
	  $("#nodename").val("");
	  $("#panel").css("display", "none");	  
	}
}

function drawPre(preName){
	creatingLink = false;
    $("#predDialog").css("display", "none");
    var addedLink = sp.createLink(sourceNode, targetNode, preName);    
    targetCircle, sourceCircle = undefined;
    redrawGraph(); 
    rewriteQuery();
    return false;
}

function doneTyping () {
  q = document.getElementById("query").value;
  
  if(sp.init(q)){
	sp.getPatterns();
	projections = sp.getProjection();
	redrawGraph();
  }
}

function addPrefix(curie, prefixes){ 
  aux = curie.split(":");
  if(prefixes.indexOf(aux[0]) <0){
  	prefixes.push(aux[0]);
  }
}

function rewriteQuery(){
  var usedPrefixes = new Array();
  var q = "";
  for(var i=0; i<links.length; i++){
  	if(links[i].source.type == "curie"){
  	  addPrefix(links[i].source.name, usedPrefixes);
  	}
  	if(links[i].type == "curie"){
  	  addPrefix(links[i].name, usedPrefixes);
  	}
  	if(links[i].target.type == "curie"){
  	  addPrefix(links[i].target.name, usedPrefixes);
  	}
  }
  for(var i=0; i< usedPrefixes.length; i++){
  	q += "PREFIX "+usedPrefixes[i]+": <"+ns.get(usedPrefixes[i])+">\n";
  }
  q += "SELECT";
  var p = sp.getProjection();
  for(var i=0; i<p.length; i++){
  	q += " "+p[i];
  }
  q+= " WHERE{\n";
  for(var i=0; i<links.length; i++){
  	q+= "  "+links[i].source.name+" "+links[i].name+" "+links[i].target.name+" .\n";
  }
  q += "}";
  document.getElementById("query").value = q;
}

(function($) {

	/*---------------------------
	 Defaults for Reveal
	----------------------------*/
		 
	/*---------------------------
	 Listener for data-reveal-id attributes
	----------------------------*/

		$('a[data-reveal-id]').live('click', function(e) {
			e.preventDefault();
			var modalLocation = $(this).attr('data-reveal-id');
			$('#'+modalLocation).reveal($(this).data());
		});
		
		$('#preOk').live('click', function(e) {
			var name = $('#txtPreContent').val();		
//			$('#nodename').val(name);
			drawPre(name);
		});


	})(jQuery);