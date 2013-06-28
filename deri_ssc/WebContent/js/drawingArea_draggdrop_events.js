;(function() {

	var _initialised = false;
	jsPlumbDemo.setOperator = function(s) {
//		alert(s.sourceId +","+s.targetId);
		alert(s.source.id);
	};
	
	jsPlumbDemo.addOutputWindow = function(x,y,category,type){			
		var div = document.createElement("div");
		div.style.left = x + "px";
		div.style.top = y + "px";
		div.style.zIndex = 100;
		div.className = "LSMWindow window";
		div.innerHTML = " Output data <br/>";
		div.setAttribute("id", "result");				
		
		var childDiv = document.createElement("div");
		
		var tag = '<div id="tblContent" style="position:absolute; width:425px; height: 550px; overflow:auto">' +
				  '<textarea id="txtContent" rows="20" cols="50" style="position: bottom;"></textarea></div>';						  
		childDiv.innerHTML+=tag;
		childDiv.setAttribute("id", "tblContent");
		div.appendChild(childDiv);
				
		$("#demo").append(div);	
		
		jsPlumb.addEndpoint(div, { anchor:"LeftMiddle" }, exampleEndpoint3);
		var windowMeta = new BlockMeta(div.id);	
		
		var connectDiv = document.createElement("div");
		connectDiv.style.left = (x-50) + "px";
		connectDiv.style.top = y + "px";
		connectDiv.style.zIndex = 101;
		connectDiv.className = "MashupWindow";
		connectDiv.innerHTML = '<a href="#mashup" class="cmdMashup" style="color:#470202;">Mashup</a>';
		connectDiv.setAttribute("id", "mashupId");
		
		tag = '</br><a href="#detach" class="cmdLink detach" rel="'+connectDiv.id+'">Detach</a>'+
		  		'<a href="#remove" class="cmdLink remove" rel="'+connectDiv.id+'">Remove</a>';				  
		connectDiv.innerHTML+=tag;
		
		$("#demo").append(connectDiv);
		var e1 = jsPlumb.addEndpoint(connectDiv, { anchor:"LeftMiddle" }, inputDataEndpoint);
		e1.isInputData=true;
		jsPlumb.addEndpoint(connectDiv, { anchor:"RightMiddle" }, exampleEndpoint3);
		
		
		var divsWithWindowClass = jsPlumb.CurrentLibrary.getSelector(".window");
		jsPlumb.draggable(divsWithWindowClass);
		jsPlumb.draggable(connectDiv);
	}
	
	jsPlumbDemo.addMergedWindow = function(x,y,category,type){			
		var mergedDiv = document.createElement("div");
		mergedDiv.style.left = (x-50) + "px";
		mergedDiv.style.top = y + "px";
		mergedDiv.style.width = "11em";
		mergedDiv.style.height = "3.5em";
		mergedDiv.style.zIndex = 101;
		mergedDiv.className = "operators";
		mergedDiv.setAttribute("id", "div-" + (eleNum ++));
		
		var image = document.createElement("img");		
		image.style = "position: bottom";
		image.setAttribute("id", "text-" + (eleNum ));
		image.src = lsmIcon.get(type);
		mergedDiv.appendChild(image);
		mergedDiv.innerHTML += "Merge <br/>";
		
		var childDiv = document.createElement("div");
		childDiv.className = "context-menu-sub box menu-1";		
		var tag = '<a href="#detach" class="cmdLink detach" rel="'+mergedDiv.id+'">Detach</a>'+
				  '<a href="#remove" class="cmdLink remove" rel="'+mergedDiv.id+'">Remove</a>' +						  
				  '<a href="#submit" class="cmdLink submit" rel="'+mergedDiv.id+'">Submit</a>';						  
		childDiv.innerHTML+=tag;
		childDiv.setAttribute("id", "childDiv-" + (eleNum ));
		
		mergedDiv.appendChild(childDiv);			
		$("#demo").append(mergedDiv);
		var e1 = jsPlumb.addEndpoint(mergedDiv, { anchor:"LeftMiddle" }, mergedDataEndpoint);
		e1.id = mergedDiv.id + "ep_0";
		endpoints.push(e1);
		var e2 = jsPlumb.addEndpoint(mergedDiv, { anchor:"RightMiddle" }, outputDataEndpoint);	
		e2.id = mergedDiv.id + "ep_1";
		endpoints.push(e2);
		
		var divsWithWindowClass = jsPlumb.CurrentLibrary.getSelector(".operators");
		jsPlumb.draggable(divsWithWindowClass);
		jsPlumb.draggable(mergedDiv);
		
		var windowMeta = new BlockMeta(mergedDiv.id);	
		windowMeta.setType(type);
		windowMeta.setCategory(category);
		windowMeta.setX(x);
		windowMeta.setY(y);
		mergedDiv.setAttribute("meta",windowMeta);
		metaHash.put(mergedDiv.id,windowMeta);
		
		$(mergedDiv).click(function(){		
			var lastSelected_meta = metaHash.get(selectedBlock);
	        if((lastSelected_meta!=null)&&(lastSelected_meta.id!=mergedDiv.id)){
	        	lastSelected_meta.setSparql($("#txtQuery").val());
	        	selectedBlock = this.id;				
	        	sparqlReload(windowMeta);
	        	$("#txtQuery").attr('readonly',true);
	        }
		});
		
	}
    
    
    jsPlumbDemo.addLSMSensorWindow = function(x,y,category,type){			
		var div = document.createElement("div");
		div.style.left = x + "px";
		div.style.top = y + "px";
		div.style.zIndex = 100;
		div.className = "LSMWindow window";
		div.setAttribute("id", "div-" + (eleNum ++));		
		
		var image = document.createElement("img");		
		image.style = "position: bottom";
		image.setAttribute("id", "text-" + (eleNum ));
		image.src = lsmIcon.get(type);
		div.appendChild(image);
		div.innerHTML += type + " Sensor <br/>";
		
		var input = document.createElement("input");
		input.type = "text";
		input.style = "position: bottom";
		input.setAttribute("id", "url"+div.id);
		input.className = "URLInput";
		
		var childDiv = document.createElement("div");
		childDiv.className = "context-menu-sub box menu-1";
		childDiv.appendChild(input);
		var tag = '<a href="#detach" class="cmdLink discover" rel="'+div.id+'">Discover</a>'+
				  '<a href="#detach" class="cmdLink detach" rel="'+div.id+'">Detach</a>'+
				  '<a href="#remove" class="cmdLink remove" rel="'+div.id+'">Remove</a>' +						  
				  '<a href="#submit" class="cmdLink submit" rel="'+div.id+'">Submit</a>';						  
		childDiv.innerHTML+=tag;
		childDiv.setAttribute("id", "childDiv-" + (eleNum ));
		div.appendChild(childDiv);							
		$("#demo").append(div);			
		
		var divsWithWindowClass = jsPlumb.CurrentLibrary.getSelector(".window");
		jsPlumb.draggable(divsWithWindowClass);
		var p = jsPlumb.addEndpoint(div, { anchor:"BottomCenter" }, filterEndpoint);
		var t = jsPlumb.addEndpoint(div, { anchor:"RightMiddle" }, outputDataEndpoint);
		p.id = div.id + "ep_0";
		t.id = div.id + "ep_1";
		endpoints.push(p);
		endpoints.push(t);
				
		$("#"+input.id).hover(function() {
			 $("#"+input.id).attr('title', $("#"+input.id).val());
		});
		
		var windowMeta = new BlockMeta(div.id);		
		windowMeta.setSocketURL('ws://' + lsmSensorSocket);
		windowMeta.setType(type);
		windowMeta.setCategory(category);
		windowMeta.setSparql(defaultQuery);
		windowMeta.setX(x);
		windowMeta.setY(y);
		div.setAttribute("meta",windowMeta);
		metaHash.put(div.id,windowMeta);
		if(selectedBlock==null ) selectedBlock = div.id;
		
		$(div).click(function(){		
			var lastSelected_meta = metaHash.get(selectedBlock);
	        if((lastSelected_meta!=null)&&(lastSelected_meta.id!=div.id)){
	        	lastSelected_meta.setSparql($("#txtQuery").val());		   				
	        	selectedBlock = this.id;
	        	sparqlReload(windowMeta);
	        }
		});

	}
    
    jsPlumbDemo.addURLWindow = function(x,y,category,type){			
		var div = document.createElement("div");
		div.style.left = x + "px";
		div.style.top = y + "px";
		div.style.zIndex = 100;
		div.style.width = "14em";
		div.className = "sources";		
		div.setAttribute("id", "div-" + (eleNum ++));			
		var image = document.createElement("img");		
		image.style = "position: bottom";
		image.setAttribute("id", "text-" + (eleNum ));
		image.src = lsmIcon.get(type);
		div.appendChild(image);
		div.innerHTML += "URL <br/>";
		
		var input = document.createElement("input");
		input.type = "text";
		input.style = "position: bottom";
		input.setAttribute("id", "url"+div.id);
		input.className = "URLInput";
		
		var childDiv = document.createElement("div");
		childDiv.className = "context-menu-sub box menu-1";
		childDiv.appendChild(input);
		var tag = '<a href="#" class="cmdLink URLdiscover" rel="'+div.id+'">Discover</a>'+
				  '<a href="#detach" class="cmdLink detach" rel="'+div.id+'">Detach</a>'+
				  '<a href="#remove" class="cmdLink remove" rel="'+div.id+'">Remove</a>' +						  
				  '<a href="#submit" class="cmdLink submit" rel="'+div.id+'">Submit</a>';						  
		childDiv.innerHTML+=tag;
		childDiv.setAttribute("id", "childDiv-" + (eleNum ));
		div.appendChild(childDiv);							
		$("#demo").append(div);			
		
		var divsWithWindowClass = jsPlumb.CurrentLibrary.getSelector(".sources");
		jsPlumb.draggable(divsWithWindowClass);
		var p = jsPlumb.addEndpoint(div, { anchor:"BottomCenter" }, filterEndpoint);
		var t = jsPlumb.addEndpoint(div, { anchor:"RightMiddle" }, outputDataEndpoint);
		p.id = div.id + "ep_0";
		t.id = div.id + "ep_1";
		endpoints.push(p);
		endpoints.push(t);
		
		$("#"+input.id).hover(function() {
			 $("#"+input.id).attr('title', $("#"+input.id).val());
		});
		
		var windowMeta = new BlockMeta(div.id);		
		windowMeta.setSocketURL('ws://' + lsmSensorSocket);
		windowMeta.setType(type);
		windowMeta.setCategory(category);
		windowMeta.setX(x);
		windowMeta.setY(y);
		
		div.setAttribute("meta",windowMeta);
		metaHash.put(div.id,windowMeta);
		if(selectedBlock==null ) selectedBlock = div.id;
		
		$(div).click(function(){		
			var lastSelected_meta = metaHash.get(selectedBlock);
	        if((lastSelected_meta!=null)&&(lastSelected_meta.id!=div.id)){
	        	lastSelected_meta.setSparql($("#txtQuery").val());		   				
	        	selectedBlock = this.id;
	        	sparqlReload(windowMeta);
	        }
		});

	}
    
    jsPlumbDemo.addTimepicker = function(x,y,category,type){
    	var div = document.createElement("div");
		div.style.left = x + "px";
		div.style.top = y + "px";
		div.style.zIndex = 100;		
		div.style.width = "15em";
		div.style.height = "5.3em";
		div.className = "operators";
		div.setAttribute("id", "div-" + (eleNum ++));
		
		var image = document.createElement("img");		
		image.style = "position: bottom";
		image.setAttribute("id", "text-" + (eleNum ));
		image.src = lsmIcon.get(type);
		div.appendChild(image);
		div.innerHTML += "Timer <br/>";
		
		var operInput = document.createElement("select");
		operInput.style.width='22%';
		operInput.setAttribute("id", div.id+"timerOper");
		var operArray = ["=", "<=", "<", ">=",">"];
		var arLen=operArray.length;
		for(var j=0; j<arLen; j++){
		    operInput.options[j]=new Option(operArray[j], operArray[j]);
		}
//		operInput.onchange= function(){dbrOptionChange()};
		div.appendChild(operInput);
		
    	var input = document.createElement("input");
		input.type = "text";
		input.style = "position: bottom";
		input.style.width = "60%";
		input.setAttribute("id", div.id+"timer");			
		input.value="";			
		$(input).datetimepicker();
		div.appendChild(input);
		
		var childDiv = document.createElement("div");
		childDiv.className = "context-menu-sub box menu-1";			
		var tag = '<a href="#detach" class="cmdLink detach" rel="'+div.id+'">Detach</a>'+
				  '<a href="#remove" class="cmdLink remove" rel="'+div.id+'">Remove</a>';
//				  '<a href="#set" class="set" rel="'+div.id+'">Set</a>';
		childDiv.innerHTML+=tag;
		childDiv.setAttribute("id", "childDiv-" + (eleNum ));
		div.appendChild(childDiv);	
		$("#demo").append(div);
		
		var divsWithWindowClass = jsPlumb.CurrentLibrary.getSelector(".operators");
		jsPlumb.draggable(divsWithWindowClass);
		var p = jsPlumb.addEndpoint(div, { anchor:anchors }, filterEndpointSource);	
		p.id = div.id + "ep_0";
		endpoints.push(p);
		var windowMeta = new BlockMeta(div.id);
		div.setAttribute("meta",windowMeta);
		metaHash.put(div.id,windowMeta);
		windowMeta.setType(type);
		windowMeta.setCategory(category);
		windowMeta.setX(x);
		windowMeta.setY(y);
		
		$(div).focusout(function(){ 	
//			$(input).blur();
			setFilter(this.id);
		});
		
		operInput.onchange=function(){
			setFilter(div.id);
		}
    }
    
    jsPlumbDemo.addLocationWindow = function(x,y,category,type){			
		var div = document.createElement("div");
		div.style.left = x + "px";
		div.style.top = y + "px";
		div.style.zIndex = 100;
		div.className = "operators";
		div.setAttribute("id", "div-" + (eleNum ++));		
		
		var image = document.createElement("img");		
		image.style = "position: bottom";
		image.setAttribute("id", "text-" + (eleNum ));
		image.src = lsmIcon.get(type);
		div.appendChild(image);
		div.innerHTML += "Location <br/>";
		
		var input = document.createElement("input");
		input.type = "text";
		input.style = "position: bottom";
		input.setAttribute("id", div.id+"location");
		
		var lat = document.createElement("input");
		lat.type = "text";			
		lat.setAttribute("id", div.id+"lat");
		lat.style.width="62px";
		
		var lng = document.createElement("input");
		lng.type = "text";		
		lng.style.width="62px";
		lng.setAttribute("id", div.id+"long");
		
		var childDiv = document.createElement("div");
		childDiv.className = "context-menu-sub box menu-1";
		div.appendChild(input);
		childDiv.innerHTML+="</br";
		childDiv.innerHTML+="  Lat  ";
		childDiv.appendChild(lat);
		childDiv.innerHTML+="  Long  ";
		childDiv.appendChild(lng);
		var tag = '</br><a href="#detach" class="cmdLink detach" rel="'+div.id+'">Detach</a>'+
				  '<a href="#remove" class="cmdLink remove" rel="'+div.id+'">Remove</a>';
		childDiv.innerHTML+=tag;
		childDiv.setAttribute("id", "childDiv-" + (eleNum ));
		div.appendChild(childDiv);
		
		$("#demo").append(div);		

		var divsWithWindowClass = jsPlumb.CurrentLibrary.getSelector(".operators");
		jsPlumb.draggable(divsWithWindowClass);
		var p = jsPlumb.addEndpoint(div, { anchor:anchors }, locationEndpoint);		
		p.id = div.id + "ep_0";
		endpoints.push(p);

		var windowMeta = new BlockMeta(div.id);	
		windowMeta.setType(type);
		windowMeta.setCategory(category);
		windowMeta.setX(x);
		windowMeta.setY(y);
		
		div.setAttribute("meta",windowMeta);
		metaHash.put(div.id,windowMeta);
		
		$(div).focusout(function(){ 		
			setFilter(div);
		});
		
		$("#"+input.id).onEnter(function(){
			var place = new Object();
			place.lat = $("#"+div.id+"lat").val();
			place.long =  $("#"+div.id+"long").val();
			place.info =  $(this).val();
						
			var message = JSON.stringify(place);
			$.getJSON(SSCHost+"get_location.html",{info:message}, function(data) {
				$("#"+input.id).val(data.address);
				$("#"+div.id+"lat").val(data.lat);
				$("#"+div.id+"long").val(data.long);
				windowMeta.setPlace(data);
			}, "json");
		});
	}
    
    jsPlumbDemo.addToStreamWindow = function(x,y,category,type){			
		var div = document.createElement("div");
		div.style.left = x + "px";
		div.style.top = y + "px";
		div.style.zIndex = 103;
		div.style.width = "130px";
		div.style.height = "70px";
		div.className = "operators";
		div.setAttribute("id", "div-" + (eleNum ++));				
		
		var image = document.createElement("img");		
		image.style = "position: bottom";
		image.setAttribute("id", "text-" + (eleNum ));
		image.src = lsmIcon.get(type);
		div.appendChild(image);
		div.innerHTML += " To RDF Stream <br/>";
		
		var input = document.createElement("input");
		input.type = "text";
//		input.style = "position: bottom";
		input.style.width="70px";
		input.style.margin="3px";
		input.setAttribute("id", div.id+"timeIterInput");
		
		var unit = document.createElement("input");
		unit.type = "text";			
		unit.setAttribute("id", div.id+"iterUnit");
		unit.style.width="42px";
				
		div.appendChild(input);	
		div.appendChild(unit);

		var childDiv = document.createElement("div");
		childDiv.className = "context-menu-sub box menu-1";		
		var tag = '<a href="#detach" class="cmdLink detach" rel="'+div.id+'">Detach</a>'+
				  '<a href="#remove" class="cmdLink remove" rel="'+div.id+'">Remove</a>' +						  
				  '<a href="#submit" class="cmdLink submit" rel="'+div.id+'">Submit</a>';						  
		childDiv.innerHTML+=tag;
		childDiv.setAttribute("id", "childDiv-" + (eleNum ));
		div.appendChild(childDiv);
				
		$("#demo").append(div);		

		var divsWithWindowClass = jsPlumb.CurrentLibrary.getSelector(".operators");
		jsPlumb.draggable(divsWithWindowClass);
		var o = jsPlumb.addEndpoint(div, { anchor:"RightMiddle" }, outputDataEndpoint);
		var p = jsPlumb.addEndpoint(div, { anchor:"BottomCenter" }, cqelsEndpoint);
		var in_e = jsPlumb.addEndpoint(div, { anchor:anchors }, inputDataEndpoint);		
		p.id = div.id + "ep_0";
		o.id = div.id + "ep_1";
		in_e.id = div.id + "ep_2";

		endpoints.push(p);
		endpoints.push(in_e);
		endpoints.push(o);
		
		var windowMeta = new BlockMeta(div.id);	
		windowMeta.setType(type);
		windowMeta.setSocketURL('ws://' + lsmCqelsSocket);
		windowMeta.setCategory(category);
		windowMeta.setX(x);
		windowMeta.setY(y);
		
		div.setAttribute("meta",windowMeta);
		metaHash.put(div.id,windowMeta);
		
		$("#"+div.id+"iterUnit").addClass("ui-widget ui-widget-content ui-corner-left").autocomplete({
		    source: timeUnits,
		    minLength: 0,		    
		    select: function( event, ui ) {
				ui.item.selected = true;
				var item= ui.item.value;
				windowMeta.setURL(item);
				for (var j = 0; j < connections.length; j++) {
					if((connections[j].scope==="filter dot")&&
							(connections[j].targetId===div.id||connections[j].sourceId===div.id)){	
						var t = connections[j].targetId===div.id?connections[j].sourceId:connections[j].targetId;
						if(metaHash.get(t).type==='Location'){
							windowMeta.setPlace(metaHash.get(t).place)
							$("#txtQuery").val(getPlaceSparqlQuery(windowMeta.place));				
							windowMeta.setSparql(getPlaceSparqlQuery(windowMeta.place));
						}											
					}				
				}
			}
		});
		
		$("#"+div.id+"iterUnit").click(function(){		
			if ($("#"+div.id+"iterUnit").autocomplete("widget").is(":visible")) {$("#"+div.id+"iterUnit").autocomplete( "close" );
            	return;                         
			}                                           
	       $("#"+div.id+"iterUnit").autocomplete("search", "" );                         
	       $("#"+div.id+"iterUnit").focus();
		});
		
		$(div).click(function(){		
			var lastSelected_meta = metaHash.get(selectedBlock);
	        if((lastSelected_meta!=null)&&(lastSelected_meta.id!=div.id)){
	        	lastSelected_meta.setSparql($("#txtQuery").val());
	        	selectedBlock = this.id;				
	        	sparqlReload(windowMeta);
	        	$("#txtQuery").attr('readonly',true);
	        }
		});
	}
    function setFilter(targetId){					
		var meta = metaHash.get(targetId);
		var filterArr;		
		var content,operator;
		var url;
		switch(meta.type.toLowerCase()){
			case "timer":
				content = $("#"+targetId).find("input:first");
				operator = $("#"+targetId+"timerOper").val();
				filterArr = new Array(meta.type.toLowerCase(),operator,content.val());	
				url = content.val();
				break;
			case "location":
				var address = $("#"+targetId).find("#"+"#"+targetId+"location").val();
				var lat = $("#"+targetId).find("#"+"#"+targetId+"lat").val();
				var long = $("#"+targetId).find("#"+"#"+targetId+"long").val();
				var place = new Array(lat,long);					
				filterArr = new Array(meta.type.toLowerCase(),"=",place);	
				url = address;				
				break;
		}					
		meta.setFilter(filterArr);
		meta.setURL(url);
//		alert(meta.filterArr);
	 }
    
    
    jsPlumbDemo.addLSMStreamWindow = function(x,y,category,type){			
		var div = document.createElement("div");
		div.style.left = x + "px";
		div.style.top = y + "px";
		div.style.zIndex = 100;
		div.style.height = "4em";
		div.className = "sources";
		
		div.setAttribute("id", "div-" + (eleNum ++));				
		var image = document.createElement("img");		
		image.style = "position: bottom";
		image.setAttribute("id", "text-" + (eleNum ));
		image.src = lsmIcon.get(type);
		div.appendChild(image);
		div.innerHTML += "LSM Stream";
		
		var childDiv = document.createElement("div");
		childDiv.className = "context-menu-sub box menu-1";		
		var tag = '<a href="#detach" class="cmdLink detach" rel="'+div.id+'">Detach</a>'+
				  '<a href="#remove" class="cmdLink remove" rel="'+div.id+'">Remove</a>' +						  
				  '<a href="#submit" class="cmdLink submit" rel="'+div.id+'">Submit</a>';						  
		childDiv.innerHTML+=tag;
		childDiv.setAttribute("id", "childDiv-" + (eleNum ));
		div.appendChild(childDiv);							
		$("#demo").append(div);			
		
		var divsWithWindowClass = jsPlumb.CurrentLibrary.getSelector(".sources");
		jsPlumb.draggable(divsWithWindowClass);
		var p = jsPlumb.addEndpoint(div, { anchor:"BottomCenter" }, cqelsEndpoint);
		var t = jsPlumb.addEndpoint(div, { anchor:"RightMiddle" }, outputDataEndpoint);
		p.id = div.id + "ep_0";
		t.id = div.id + "ep_1";
		endpoints.push(p);
		endpoints.push(t);
		
		var windowMeta = new BlockMeta(div.id);		
		windowMeta.setSocketURL('ws://' + lsmStreamSocket);
		windowMeta.setType(type);
		windowMeta.setCategory(category);
		windowMeta.setX(x);
		windowMeta.setY(y);

		div.setAttribute("meta",windowMeta);
		metaHash.put(div.id,windowMeta);
		if(selectedBlock==null ) selectedBlock = div.id;
		
//		jsPlumb.makeTarget(div,cqelsEndpoint);
			
		$(div).click(function(){		
			var lastSelected_meta = metaHash.get(selectedBlock);
	        if((lastSelected_meta!=null)&&(lastSelected_meta.id!=div.id))
	        	lastSelected_meta.setSparql($("#txtQuery").val());
			selectedBlock = this.id;				
			sparqlReload(windowMeta);
			if((windowMeta.sparql==null)||(windowMeta.sparql===""))
				$("#txtQuery").attr('readonly',true);			
		});

	}
    
    jsPlumbDemo.addTwitterStreamWindow = function(x,y,category,type){			
		var div = document.createElement("div");
		div.style.left = x + "px";
		div.style.top = y + "px";
		div.style.zIndex = 100;
		div.className = "sources";
		div.setAttribute("id", "div-" + (eleNum ++));				
		var image = document.createElement("img");		
		image.style = "position: bottom";
		image.setAttribute("id", "text-" + (eleNum ));
		image.src = lsmIcon.get(type);
		div.appendChild(image);
		
		var label = document.createElement("label");
		label.innerHTML='tags ';
		
		var input = document.createElement("input");
		input.className = "URLInput";
		input.type = "text";
		input.style = "position: bottom";
		input.setAttribute("id", "text-" + (eleNum ));
		
		var childDiv = document.createElement("div");
		childDiv.className = "context-menu-sub box menu-1";
		childDiv.appendChild(label);
		childDiv.appendChild(input);
		var tag = '<a href="#detach" class="cmdLink detach" rel="'+div.id+'">Detach</a>'+
				  '<a href="#remove" class="cmdLink remove" rel="'+div.id+'">Remove</a>' +						  
				  '<a href="#submit" class="cmdLink submit" rel="'+div.id+'">Submit</a>';						  
		childDiv.innerHTML+=tag;
		childDiv.setAttribute("id", "childDiv-" + (eleNum ));
		div.appendChild(childDiv);							
		$("#demo").append(div);			
		
		var divsWithWindowClass = jsPlumb.CurrentLibrary.getSelector(".sources");
		jsPlumb.draggable(divsWithWindowClass);
		var p = jsPlumb.addEndpoint(div, { anchor:[0.35, 1, 0, 1] }, cqelsEndpoint);
		var o = jsPlumb.addEndpoint(div, { anchor:[0.7, 1, 0, 1] }, filterEndpoint);
		var t = jsPlumb.addEndpoint(div, { anchor:"RightMiddle" }, outputDataEndpoint);
		p.id = div.id + "ep_0";
		t.id = div.id + "ep_1";
		o.id = div.id + "ep_2";
		endpoints.push(p);
		endpoints.push(t);
		endpoints.push(o);
		
		var windowMeta = new BlockMeta(div.id);		
		windowMeta.setSocketURL('ws://' + lsmTwitterSocket);
		windowMeta.setType(type);
		windowMeta.setCategory(category);
		windowMeta.setX(x);
		windowMeta.setY(y);

		div.setAttribute("meta",windowMeta);
		metaHash.put(div.id,windowMeta);
		if(selectedBlock==null ) selectedBlock = div.id;

		var tweetDiv = document.createElement("div");					
		tweetDiv.className = "tweets";				
		tweetDiv.setAttribute("id", "twitterSearch"+div.id);
		$('#visualizationTab').append(tweetDiv);
		$(div).click(function(){		
			var lastSelected_meta = metaHash.get(selectedBlock);
	        if((lastSelected_meta!=null)&&(lastSelected_meta.id!=div.id)){
	        	lastSelected_meta.setSparql($("#txtQuery").val());
				selectedBlock = this.id;				
				sparqlReload(windowMeta);
				if((windowMeta.sparql==null)||(windowMeta.sparql===""))
					$("#txtQuery").attr('readonly',true);					
				
				$('#visualizationTab').empty();
				var s = $('#visualizationTab');
				$('#visualizationTab').append(tweetDiv);			
				
			}
		});

	}
    
      
    jsPlumbDemo.addEndPointWindow = function(x,y,category,type){			
		var div = document.createElement("div");
		div.style.left = x + "px";
		div.style.top = y + "px";
		div.style.zIndex = 100;
		div.className = "SparqlEndPoint";
		div.setAttribute("id", "div-" + (eleNum ++));		
		
		var tag = '<label><img src="images/32x32/sparql.png"/>Sparql Endpoint</label>';
		div.innerHTML+=tag; 
		  
		var input = document.createElement("input");
		input.className = "URLInput";
		input.type = "text";
		input.style = "position: bottom";
		input.setAttribute("id", div.id+"endpoint");
			
	
		var childDiv = document.createElement("div");
		childDiv.className = "context-menu-sub box menu-1";
		childDiv.appendChild(input);
//		childDiv.appendChild(btn);
		
		tag = '<a href="#detach" class="cmdLink detach" rel="'+div.id+'">Detach</a>'+
				  '<a href="#remove" class="cmdLink remove" rel="'+div.id+'">Remove</a>' +						  
				  '<a href="#submit" class="cmdLink submit" rel="'+div.id+'">Submit</a>';						  
		childDiv.innerHTML+=tag;
		childDiv.setAttribute("id", "childDiv-" + (eleNum ));
		div.appendChild(childDiv);							
		$("#demo").append(div);			
					
		var divsWithWindowClass = jsPlumb.CurrentLibrary.getSelector(".SparqlEndPoint");
		jsPlumb.draggable(divsWithWindowClass);
		var c = jsPlumb.addEndpoint(div, { anchor:[0.35, 1, 0, 1] }, cqelsEndpoint);
		var f = jsPlumb.addEndpoint(div, { anchor:[0.7, 1, 0, 1] }, filterEndpoint);
		var t = jsPlumb.addEndpoint(div, { anchor:"RightMiddle" }, outputDataEndpoint);
		c.id = div.id + "ep_0";
		t.id = div.id + "ep_1";
		f.id = div.id + "ep_2";
		endpoints.push(c);
		endpoints.push(t);
		endpoints.push(f);
		
		var windowMeta = new BlockMeta(div.id);		
		windowMeta.setSocketURL('ws://' + lsmEndPointSocket);
		windowMeta.setType(type);
		windowMeta.setCategory(category);
		windowMeta.setX(x);
		windowMeta.setY(y);

		div.setAttribute("meta",windowMeta);
		metaHash.put(div.id,windowMeta);
		if(selectedBlock==null ) selectedBlock = div.id;

		$(div).click(function(){		
		   	var lastSelected_meta = metaHash.get(selectedBlock);
	        if(lastSelected_meta!=null)
	        	lastSelected_meta.setSparql($("#txtQuery").val());			
			selectedBlock = this.id;
			sparqlReload(windowMeta);
		});

		$("#"+div.id+"endpoint").addClass("ui-widget ui-widget-content ui-corner-left").autocomplete({
		    source: availableSparqlEndPoints,
		    minLength: 0,		    
		    select: function( event, ui ) {
				ui.item.selected = true;
				var item= ui.item.value;
				windowMeta.setURL(item);
//				alert(item);
				for (var j = 0; j < connections.length; j++) {
					if((connections[j].scope==="filter dot")&&
							(connections[j].targetId===div.id||connections[j].sourceId===div.id)){	
						var t = connections[j].targetId===div.id?connections[j].sourceId:connections[j].targetId;
						if(metaHash.get(t).type==='Location'){
							windowMeta.setPlace(metaHash.get(t).place)
							$("#txtQuery").val(getPlaceSparqlQuery(windowMeta.place));				
							windowMeta.setSparql(getPlaceSparqlQuery(windowMeta.place));
						}											
					}				
				}
			}
		});
		
		$("#"+div.id+"endpoint").click(function(){		
			if ($("#"+div.id+"endpoint").autocomplete("widget").is(":visible")) {$("#"+div.id+"endpoint").autocomplete( "close" );
            	return;                         
			}                                           
	       $("#"+div.id+"endpoint").autocomplete("search", "" );                         
	       $("#"+div.id+"endpoint").focus();
		});
	}
    
    jsPlumbDemo.addCqelsWindow = function(x,y,category,type){			
		var div = document.createElement("div");
		div.style.left = x + "px";
		div.style.top = y + "px";
		div.style.zIndex = 100;		
		div.style.width = "12em";
		div.style.height = "3.5em";		
		div.className = "operators";		
		div.setAttribute("id", "div-" + (eleNum ++));				
		var image = document.createElement("img");		
		image.style = "position: bottom";
		image.setAttribute("id", "text-" + (eleNum ));
		image.src = lsmIcon.get(type);
		div.appendChild(image);
		div.innerHTML += "Cqels filter<br/>";
		
		var childDiv = document.createElement("div");
		childDiv.className = "context-menu-sub box menu-1";		
		var tag = '<a href="#" class="cmdLink cqels_editor" rel="'+div.id+'">Editor</a>'+
				  '<a href="#detach" class="cmdLink detach" rel="'+div.id+'">Detach</a>'+
				  '<a href="#remove" class="cmdLink remove" rel="'+div.id+'">Remove</a>';
		childDiv.innerHTML+=tag;
		childDiv.setAttribute("id", "childDiv-" + (eleNum ));
		div.appendChild(childDiv);							
//		var id = "#"+input.id;
		$("#demo").append(div);			
		
		var divsWithWindowClass = jsPlumb.CurrentLibrary.getSelector(".operators");
		jsPlumb.draggable(divsWithWindowClass);
		var p = jsPlumb.addEndpoint(div, { anchor:anchors }, cqelsEndpoint);	
		p.id = div.id + "ep_0";
		endpoints.push(p);
		
		var windowMeta = new BlockMeta(div.id);	
		windowMeta.setType(type);
		windowMeta.setCategory(category);
		windowMeta.setX(x);
		windowMeta.setY(y);
		
		div.setAttribute("meta",windowMeta);
		metaHash.put(div.id,windowMeta);
		$(div).click(function(){		
			var lastSelected_meta = metaHash.get(selectedBlock);
	        if((lastSelected_meta!=null)&&(lastSelected_meta.id!=div.id)){
	        	lastSelected_meta.setSparql($("#txtQuery").val());
	        	selectedBlock = this.id;				
	        	sparqlReload(windowMeta);	        	
	        }
		});

	}
    
    function sparqlReload(meta){
    	$("#txtQuery").attr('readonly',false);
    	$("#txtQuery").val(meta.sparql);
    	$("#txtRawData").val('');
    	$('#tableContent').find("table:first").remove();
    	$('#visualizationTab').empty();
    	loadLatestData(meta);
//    	$('#tableContent').remove("table:first");
    }

    
    function getPlaceSparqlQuery(place){
    	var query = 'prefix geo: <http://www.w3.org/2003/01/geo/wgs84_pos#> \n'+
    		'select ?id ?distance \n'+
    				'	where { \n'+ 
    				  '		{select distinct(?id) bif:st_distance(?geo,<bif:st_point>('+place.long+','+place.lat+')) as ?distance \n'+
    				    '		where{ \n'+
    				      '			?id geo:geometry ?geo.\n'+
    				       '		filter (bif:st_intersects(?geo,bif:st_point('+place.long+','+place.lat+'),50)).\n'+
    				    '		}\n'+    
    				  '		}\n'+
//    				  '		?id ?p ?o.\n'+
    				'	} order by ?distance limit 20\n';
    	return query;
    }
})();