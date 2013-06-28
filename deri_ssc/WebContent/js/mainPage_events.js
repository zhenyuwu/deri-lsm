$(document).ready(function(){
        	var ws = null;
            var refreshIntervalId,rabbitIntervalId;
            
            function setConnected(connected) {
                document.getElementById('connect').disabled = connected;
                document.getElementById('disconnect').disabled = !connected;
                document.getElementById('echo').disabled = !connected;
            }

            
            function make_table(data){
            	var table = document.createElement('table');
            	var tr = document.createElement('tr');
            	var tbody = document.createElement('tbody');            	
            	for(var field in data[0]){
                    var th = document.createElement('th');
                    if(field=='lat'){
                    	th.innerHTML = "Map";                    	
                    }
                    else if(field=='lng') 
                    	continue;
                    else
                    	th.innerHTML=field;
                    tr.appendChild(th);              					
            	}
            	tbody.appendChild(tr);
            	table.appendChild(tbody);
                table.id="sensorDisTbl";
                return table;
            }
            
            function add_To_table(data){
                var tbody = document.createElement('tbody');                
                for(var i=0; i<data.length; i++ ){
                        var tr = document.createElement('tr');
                        var lat=0,lng=0;
                        for(var field in data[i]){
                                var td = document.createElement('td');
                                if(data[i][field].type==="uri"){
                                	td.innerHTML='<a href="#" onclick="setSensorURL(this.innerHTML)">' + data[i][field].value +'</a>';
                                }
                                else{
                                	if(field=='lat'){
                                		lat = data[i][field].value;
                                		continue;
                                	}
                                	else if(field=='lng'){
                                		lng = data[i][field].value;
                                		td.lat = lat;
                                		td.lng = lng;
                                		var img = document.createElement('img');
                                		img.src = "images/32x32/location.png";
                                		img.title = "show on map";
                                		img.id = "sensorMap"+i;
                                		td.appendChild(img);
                                	}
                                	else{
                                		td.innerHTML = data[i][field].value;
                                	}
                                }
                                td.className=field;                                
                                tr.appendChild(td);                            
                        }
                        tbody.appendChild(tr);
                }
                return tbody;
            }
            
            function addRowHandlers() {
                var table = document.getElementById("sensorDisTbl");
                var rows = table.getElementsByTagName("tr");
                for (i = 1; i < rows.length; i++) {
                	var cNum = true;
                    var currentRow = table.rows[i];
                    var createClickHandler = 
                        function(row) 
                        {
                            return function() { 
                                                    var cell = row.getElementsByTagName("td")[1];
                                                    var name = row.getElementsByTagName("td")[2];
                                                    var city = row.getElementsByTagName("td")[3];
                                                    $("#mapContainer").data("lat",cell.lat).data("lng",cell.lng).data("city",name.innerText+","+city.innerText).dialog('open');  
                                             };
                        }
                    var cell = currentRow.getElementsByTagName("td")[1];
                    cell.onclick = createClickHandler(currentRow);
                }
            }
            
    	/* tabs events
         * 
         */
    
        $("#txtQuery").val(defaultQuery);	
        $("#sparqlvisual").click(function() {			 
			 openVisualization(selectedBlock);
		});
        
        $("#sscWidget").click(function() {			 
//			 window.open(SSCHost+'/SSCWidget/index.html', '_blank');
        	alert('This functionality will be coming soon');
		});
                
        $("#sscHome").click(function() {			 
			 window.open(SSCHost, "_self");
		});
        
        function openVisualization(id){            	
        	var popup = window.open(SSCHost+'VisualSparql.jsp', '_blank', 'toolbar=0,location=0,menubar=0');
        	    $(popup).attr("parentId");
        	    popup["parentId"]=id;           
        }
        
  
        $('#login').click(function(e) {
        	if($('#login').html()!='Login'){        		    			  			
    			$(this).html('Login');
    			$(this).attr("title","Click here to sign in");
    			user.username = null;
    			user.pass = null;
    			user.userId = null;
        	}else
        		$( "#login-form" ).dialog( "open" );
        });
        
        $('#signUpBtn').click(function(e) {
        	$("#login-form").dialog("close");
        	$( "#signup-form" ).dialog( "open" );
        });
        
        $( "#signup-form" ).dialog({
			autoOpen: false,
			modal:true,
			show: "blind",
			width: 400,
			hide: "explode",		
			open: function() {				
				$('#signup-form').find('input:first').focus();
		    },
			buttons: [{
                	id:"signUpCancel",            
                	text: "Cancel",
                	click: function() {
                        $(this).dialog("close");
                	}
				},{
					id:"signup",			
					text: "Sign up",
					click: function() {					
						var u = new Object();
						u.user = $("#signupName").val();
						u.pass =  $("#signupPassword").val();
						u.repass =  $("#signupConfirmPassword").val();
						u.email = $("#signupEmail").val();
						if(u.pass!=u.repass){
							alert('Signup failed!');
							return;	
						}									
						var message = JSON.stringify(u);
						$.getJSON(SSCHost+"signup.html",{user:message}, function(data) {
							var obj = data;        	
			        		if(obj.success==true){
			        			var text = "Signup successful!";
//			        			$("#notifiTxt").html("Signup successful!");
			        			$( "#notification" ).data("content",text).dialog("open");
			        			
			        			$("#login").html(u.user+"/logout");
			        			$("#login").attr("title","Click here to sign out");
			        			$("#signup-form").dialog("close");
			        			user.username = u.user;
			        			user.pass = u.pass;
			        			user.userId = obj.userId;
			        		}else alert('Signup failed!');
			            		                
						}, "json");
					}
				}]
		});
        
               
        $( "#login-form" ).dialog({
			autoOpen: false,
			modal:true,
			show: "blind",
			width: 350,
			hide: "explode",		
			open: function() {
				$('#logName').focus();
		    },
			buttons: [{
                	id:"loginCancel",            
                	text: "Cancel",
                	click: function() {
                        $(this).dialog("close");
                	}
				},{
					id:"signin",			
					text: "Login",
					click: function() {					
						var u = new Object();
						u.user = $("#logName").val();
						u.pass =  $("#logPass").val();
															
						var message = JSON.stringify(u);
						$.getJSON(SSCHost+"login.html",{user:message}, function(data) {
							var obj = data;        	
			        		if(obj.find==true){
			        			$("#login").html(u.user+"/Logout");
			        			$("#login").attr("title","Click here to sign out");
			        			$("#login-form").dialog("close");
			        			user.username = u.user;
			        			user.pass = u.pass;
			        			user.userId = obj.userId;
			        		}else alert('Login failed!');
			            		                
						}, "json");
					}
				}]
		});
        
        $('#logPass').keypress(function(e){
            if(e.which === 13)
               $('#signin').click();
        });
        
        
        $( "#discover" ).dialog({
			autoOpen: false,
			modal:true,
			show: "blind",
			width: 600,
			hide: "explode",		
			open: function(event, ui) {
				$('#discover').find("table:first").remove();	
				$("#disLat").val('');
				$("#disLong").val('');
				$("#disLocation").val('');
				$("#disRadius").val('');
		    },
			buttons: [{
                	id:"disCancel",            
                	text: "Close",
                	click: function() {
                        $(this).dialog("close");
                	}
				},{
					id:"disDiscover",			
					text: "Discover",
					click: function() {						
						$('#discover').find("table:first").remove();
						var findInfo = new Object();
						findInfo.lat = $("#disLat").val();
						findInfo.long =  $("#disLong").val();
						findInfo.location =  $("#disLocation").val();
						findInfo.radius =  $("#disRadius").val();
						findInfo.sensorType = $("#discover").attr('sensorType');									
						
						var message = JSON.stringify(findInfo);					
						$.getJSON(SSCHost+"discover.html",{info:message}, function(data) {							
							var obj = data;        	
			        		var table = make_table(obj.results.bindings);
			            	$("#dialogLsmDiscover").append(table);			            	
			        		table.appendChild(add_To_table(obj.results.bindings));	
			        		addRowHandlers();
						}, "json");
					}
				}]
		});
          
		

        $("#disLocation").keyup(function(e){
        	if (e.keyCode == 13) {
				var place = new Object();
				place.lat = $("#disLat").val();
				place.long =  $("#disLong").val();
				place.info =  $(this).val();
							
				var interval = loadProgressBar();
				$( "#loading-form" ).dialog('open');
				var message = JSON.stringify(place);						
					
				$.getJSON(SSCHost+"get_location.html",{info:message}, function(data) {
					clearInterval(interval);
					$( "#loading-form" ).dialog('close');
					$("#disLocation").val(data.address);
					$("#disLat").val(data.lat);
					$("#disLong").val(data.long);
	//				windowMeta.setPlace(data);
				}, "json");
        	}
		});
        
        $( "#sindiceDiscover" ).dialog({
			autoOpen: false,
			modal:true,
			show: "blind",
			width: 600,
			hide: "explode",		
			open: function(event, ui) {
				$('#sindiceDiscover').find("table:first").remove();				
		    },
			buttons: [{
                	id:"disClose",            
                	text: "Close",
                	click: function() {
                        $(this).dialog("close");
                	}
				},{
					id:"sindiceDiscoverBtn",			
					text: "Discover",
					click: function() {						
						$('#sindiceDiscover').find("table:first").remove();
						var findInfo = new Object();
						findInfo.keyword = $("#sindiceKeyword").val();
						
						var interval = loadProgressBar();
						$( "#loading-form" ).dialog('open');
						var message = JSON.stringify(findInfo);
						
						$.getJSON(SSCHost+"URLDiscover.html",{info:message}, function(data) {
							clearInterval(interval);
							$( "#loading-form" ).dialog('close');
							var obj = data;        	
			        		var table = make_URLDiscoveryResultTable(obj);
			            	$("#sindiceResult").append(table);			            	
			        		table.appendChild(add_To_URLDiscoveryResultTable(obj));			                
						}, "json");
					}
				}]
		});
        
        function loadProgressBar(){
	    	myProgressBar = new ProgressBar("loading-form",{
	    		borderRadius: 10,
	    		width: 300,
	    		height: 20,
	    		maxValue: 100,
	    		labelText: "Loaded in {value,0} %",
	    		orientation: ProgressBar.Orientation.Horizontal,
	    		direction: ProgressBar.Direction.LeftToRight,
	    		animationStyle: ProgressBar.AnimationStyle.LeftToRight1,
	    		animationSpeed: 1.5,
	    		imageUrl: 'images/progressbar.png',
	    		backgroundUrl: 'images/progressbar_backg.png',	    		
	    	});
	    	
	    	timerId = window.setInterval(function() {
	    		if (myProgressBar.value >= myProgressBar.maxValue)
	    			myProgressBar.setValue(0);
	    		else
	    			myProgressBar.setValue(myProgressBar.value+1);
	    		
	    	},
	    	100);
	    	return timerId;
	    }
        
        function make_URLDiscoveryResultTable(data){
        	var table = document.createElement('table');
        	var tr = document.createElement('tr');
        	var tbody = document.createElement('tbody');
        	for(var i=0;i<data.vars.length;i++){        		
        			var th = document.createElement('th');
        			th.innerHTML=data.vars[i];
        			tr.appendChild(th);
        	}
        	
        	tbody.appendChild(tr);        	
        	table.appendChild(tbody);
            table.id="table_" + Math.floor ( Math.random ( ) * 100 );
            return table;
        }
        
        function add_To_URLDiscoveryResultTable(data){
        	var tbody = document.createElement('tbody');
        	var entries = data.entries;
        	var vars = data.vars;
        	if (entries.length<=0) 
        		tbody.innterHTML +="Sorry! There is no sensor within this radius."
            for(var i=0; i<entries.length; i++ ){
                    var tr = document.createElement('tr');
                    for(var j=0;j<vars.length;j++){
                    	var field = vars[j].toLowerCase();
                    	if(field==='link') continue;
                        var td = document.createElement('td');
                        if(field==="title"){
                        	var titles = "";
                        	for(var t=0;t<entries[i][field].length;t++)
                        		if(t<entries[i][field].length-1)
                        			titles+=entries[i][field][t].value+",";
                        		else
                        			titles+=entries[i][field][t].value;
                        	td.innerHTML='<a href="#" onclick="setSensorURL(\''+entries[i]["link"]+'\')">' + titles +'</a>';
                        }else if(field==="formats"){
                        	var formats = "";
                        	for(var t=0;t<entries[i][field].length;t++)
                        		if(t<entries[i][field].length-1)
                        			formats+=entries[i][field][t]+",";
                        		else
                        			formats+=entries[i][field][t];
                        	td.innerHTML = formats;
                        }else
                        	td.innerHTML = entries[i][field];
                        td.className=field;
                        tr.appendChild(td);                            
                    }
                    tbody.appendChild(tr);
            }
            return tbody;
        }
        
        $("#discard").click(function() {	
        	var r=confirm("Delete all blocks?");
        	if (r==true){
        		 resetCanvas();
        	}			
		});
        
        $("#save").click(function() {			
        	if(user.userId==null){
        		alert("Please sign in!");
        	}else
        		$( "#userviewSave-form" ).dialog('open');
		});
        
        $( "#userviewSave-form" ).dialog({
			autoOpen: false,
			modal:true,
			show: "blind",
			width: 300,
			hide: "explode",		
			open: function(event, ui) {
				$('#saveViewName').focus();
		    },
			buttons: [{
                	id:"saveViewCancel",            
                	text: "Cancel",
                	click: function() {
                        $(this).dialog("close");
                	}
				},{
					id:"viewSubmit",			
					text: "Submit",
					click: function() {					
						var viewName = $('#saveViewName').val();
						var blockDom = new BlockDom(metaHash,connections,viewName);
			        	var json = blockDom.blockDomToJson();
			        	blockDom.saveBlocks(json);
			        	$("#userviewSave-form").dialog("close");
					}
				}]
		});
        
        $("#load").click(function() {		
        	if(user.userId==null){
        		alert("Please sign in!");
        	}else
        		$( "#userviewLoad-form" ).dialog('open');
		});
        
        $( "#userviewLoad-form" ).dialog({
			autoOpen: false,
			modal:true,
			show: "blind",
			width: 300,
			hide: "explode",		
			open: function(event, ui) {
				$('#loadViewName').focus();
				$.getJSON(SSCHost+"loadAllViewName.html",{userId:user.userId}, function(data) {	
					var viewsArr = new Array();
					for(var i=0;i<data.views.length;i++){
						viewsArr.push(data.views[i]);
					}
					
					$("#loadViewName").addClass("ui-widget ui-widget-content ui-corner-left").autocomplete({
					    source: viewsArr,
					    minLength: 0,		    
					    select: function( event, ui ) {
							ui.item.selected = true;
							var item= ui.item.value;							
						}
					});
					
					$("#loadViewName").click(function(){		
						if ($("#loadViewName").autocomplete("widget").is(":visible")) {
							$("#loadViewName").autocomplete( "close" );
			            	return;                         
						}                                           
				       $("#loadViewName").autocomplete("search", "" );                         
				       $("#loadViewName").focus();
					});
					
				}, "json");
		    },
			buttons: [{
                	id:"loadViewCancel",            
                	text: "Cancel",
                	click: function() {
                        $(this).dialog("close");                        
                	}
				},{
					id:"viewSubmit",			
					text: "Submit",
					click: function() {				
						resetCanvas();
						var viewName = $('#loadViewName').val();
						var blockDom = new BlockDom(null,null,viewName);
						blockDom.loadBlocks();
			        	$("#userviewLoad-form").dialog("close");			        	
					}
				}]
		});
        
        $( "#publishSocket-form" ).dialog({
			autoOpen: false,
			modal:true,
			show: "blind",
			width: 500,
			hide: "explode",		
			open: function(event, ui) {
				var socketId = $(this).data('socket');
				$("#socketURL").val("ws://"+socketHost+ "websocket/SSCMessage/"+socketId);
				$('#description').focus();
		    },
			buttons: [{
                	text: "Cancel",
                	click: function() {
                        $(this).dialog("close");
                        return null;
                	}
				},{							
					text: "Submit",
					click: function() {	
						var id = $(this).data('id');
						var socketId = $(this).data('socket');
						var blockMeta = metaHash.get(id).getBlockMeta();
						var obj = new Object();
						obj.socketId = socketId;
						obj.userId = user.userId;
						obj.socketTemplate = blockMeta;
						obj.description = $("#socketDes").val();
						var message = JSON.stringify(obj);
						$.getJSON(SSCHost+"usersocket_save.html",{info:message}, function(data) {								                
						}, "json");						
			        	$(this).dialog("close");	
			        	$("#socketURL").val("");
					}
				}]
		});
        
        function resetCanvas(){
        	$("#demo").empty();
			 $("#visualizationTab").empty();
			 $("#tableContent").empty();
			 $("#txtRawData").val('');
			 $("#txtQuery").val('');
			 connections.length = 0;
			 metaHash.clear();
			 eleNum = 0;
			 endpoints.length = 0;
        }

        $( "#notification" ).dialog({
			autoOpen: false,
			modal:true,
			show: "blind",
			width: 300,
			hide: "explode",		
			open: function(event,ui){
				var text = $(this).data('content');
				$("#notifiTxt").html(text);
			},
			buttons: [{                	           
                	text: "Ok",
                	id: "notificationOk",
                	click: function() {
                        $(this).dialog("close");
                	}
				}]
		});
        
        $( "#help" ).dialog({
			autoOpen: false,
			modal:true,
			show: "blind",
			width: 500,
			hide: "explode",		
			open: function(event,ui){
				var text = $(this).data('content');
				$("#helpContent").html(text);				
			},
			buttons: [{                	           
                	text: "Ok",                	
                	click: function() {
                        $(this).dialog("close");
                	}
				}]
		});
        
        $( "#loading-form" ).dialog({
			autoOpen: false,
			modal:true,
			show: "blind",
			width: 320,
			height:25,
			hide: "explode",		
			open: function(event,ui){
				$( "#loading-form" ).empty();
			},
			close: function(event,ui){
				$(this).dialog("close");
			}
		});
        
        $( "#mapContainer" ).dialog({
			autoOpen: false,
			modal:true,
			show: "blind",
			width: 500,
			height:500,
			hide: "explode",		
			open: function(event,ui){
				var lat = $(this).data('lat');
				var lng = $(this).data('lng');
				var city = $(this).data('city');
				$("#map").gMap({ zoom:14, markers: [{ latitude: lat,
                    longitude: lng,html: city,
                    }] });
			},
			buttons: [{                	           
                	text: "Ok",                	
                	click: function() {
                        $(this).dialog("close");
                	}
				}]
		});
        
        $("#discoverHelp").click(function() {		
        	var content = "This functionality is used to discover all the sensors around you within a specified radius. <br>" +
        			"Please follow these steps:<br>" +
        			"1. Input your location (ex: <b>London</b>) in the Location text box. Press <b>Enter</b> to finish.<br>" +
        			"2. Input the radius (km).<br>" +
        			"3. Click <b>Discover</b> button to start acquisition data process.<br>"+
        			"4. Click on the <b>your sensor URL</b> in the result list.<br>"+
        			"5. Click <b>Close</b> to finish.<br>"+
        			"(Note: The Train Station and Bike Hire sensors are available only for London city.The Traffic sensors are available for London and Ohio(USA).)";
        	$( "#help" ).data("content",content).dialog('open');        	
		});
        
        $("#sindiceHelp").click(function() {		
        	var content = "This functionality is used to discover all the RDF data sources based on the input keywords. <br>" +
        			"Please follow these steps:<br>" +
        			"1. Input your keywords (ex: <b>Buckingham palace</b>) in the <b>Keyword</b> text box.<br>" +        			
        			"2. Click <b>Discover</b> button to start acquisition data process.<br>"+
        			"3. Click on the <b>Data title</b> in the result list.<br>"+
        			"4. Click <b>Close</b> to finish.<br>";
        	$( "#help" ).data("content",content).dialog('open');
		});
        
       
 });
        
