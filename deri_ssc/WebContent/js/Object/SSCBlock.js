var Block = (function() {
	var FUNCTION = "function";
	
	function Block() {
		var blockMeta = null;
		var blockId = null;
		var latestData = null;
		var myProgressBar;
		var time_iter_progressBar = 0;
		this.initialized =  function(x,y,category,type){
			blockMeta = new BlockMeta();	
			blockMeta.setType(type);
			blockMeta.setCategory(category);
			blockMeta.setX(x);
			blockMeta.setY(y);
		}
		this.getBlockMeta = function(){
			return blockMeta;
		}
		this.getId = function(){
			return blockId;
		}
		
		this.setId = function(id){
			blockId = id;
		}
		
		this.addChild = function(child){
			blockMeta.addChild(child.getBlockMeta());
		}
		
		this.removeChild = function(child){
			blockMeta.removeChild(child.getBlockMeta());
		}
		
		this.setLatestData = function(data){
			latestData = data;
		}
		
		this.getLatestData = function(){
			return latestData;
		}
		
		this.render = function(){
			switch(blockMeta.category){
			case "LSMSensor":
				this.addLSMSensorWindow();
				break;
			case "Sources":
				if(blockMeta.type==="EndPoint")
					this.addEndPointWindow();
				else if(blockMeta.type==="URL")
					this.addURLWindow();
				break;	
			case "Stream":
				if(blockMeta.type==="LSMStream")
					this.addLSMStreamWindow();
				else if(blockMeta.type==="Twitter")
					this.addTwitterStreamWindow();						
			case "Operators":
					if(blockMeta.type==="Merge"){
						this.addMergedWindow();
					}else if(blockMeta.type==="Location"){
						this.addLocationWindow();
					}else if(blockMeta.type==="Timer"){
						this.addTimepicker();
					}else if(blockMeta.type==="Cqels"){
						this.addCqelsWindow();
					}else if(blockMeta.type==="ToRDFStream"){
						this.addToStreamWindow();
					}
				break;
			}
		}
		
		function onClick(){
			var previousBlock = metaHash.get(selectedBlock);
	        if(previousBlock!=null){
	        	if(previousBlock.getId()!=blockId){
	        		previousBlock.getBlockMeta().setSparql($("#txtQuery").val());
	        		sparqlReload(blockMeta);
	        	}
	        }
	        selectedBlock = blockId;
		}
		
		this.loadBlocks = function(){
			var jo = new Object();
			jo.userId = user.userId;
			jo.viewname = viewName;
			var jStr = JSON.stringify(jo);
			$.getJSON(SSCHost+"userview_load.html",{view:jStr}, function(data) {
				JsonToBlocksDom(data);
			}, "json");
		};

		this.loadProgressBar = function(){
			time_iter_progressBar = startProgressBar();
			$( "#loading-form" ).dialog('open');
		}	
		
		this.stopProgressBar = function(){
			clearInterval(time_iter_progressBar);
			$( "#loading-form" ).dialog('close');
		}
		
		this.addMergedWindow = function(){			
			var mergedDiv = document.createElement("div");
			mergedDiv.style.left = (blockMeta.x-50) + "px";
			mergedDiv.style.top = blockMeta.y + "px";
			mergedDiv.style.width = "11em";
			mergedDiv.style.height = "3.5em";
			mergedDiv.style.zIndex = 101;
			mergedDiv.className = "operators";
			mergedDiv.setAttribute("id", "div-" + (eleNum ++));
			blockId = mergedDiv.id;
			
			var image = document.createElement("img");		
			image.style = "position: bottom";
			image.setAttribute("id", "text-" + (eleNum ));
			image.src = lsmIcon.get(blockMeta.type);
			mergedDiv.appendChild(image);
			mergedDiv.innerHTML += "Merge <br/>";
			
			var childDiv = document.createElement("div");
			childDiv.className = "context-menu-sub box menu-1";		
			var tag = '<img style="margin-right:7px;" src="images/8x8/detach.png" title="Detach" class="cmdLink detach" rel="'+mergedDiv.id+'"/>'+
			  '<img style="margin-right:7px;" src="images/8x8/remove.png" title="Remove" class="cmdLink remove" rel="'+mergedDiv.id+'"/>' +						  
			  '<img style="margin-right:7px;" src="images/8x8/submit.png" title="Debug" class="cmdLink submit" rel="'+mergedDiv.id+'"/>' +
			  '<img style="margin-right:7px;" src="images/8x8/publish.png" title="Publish" class="cmdLink publish" rel="'+mergedDiv.id+'"/>';							  
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
			
			blockId = mergedDiv.id;
			blockMeta.setId(mergedDiv.id);	
			
			mergedDiv.setAttribute("meta",blockMeta);
			metaHash.put(mergedDiv.id,this);
			
			$(mergedDiv).click(function(){		
				var previousBlock = metaHash.get(selectedBlock);
		        if((previousBlock!=null)&&(previousBlock.getId()!=mergedDiv.id)){
		        	previousBlock.getBlockMeta().setSparql($("#txtQuery").val());
		        	selectedBlock = this.id;				
		        	sparqlReload();
		        	$("#txtQuery").attr('readonly',true);
		        }
			});
			
		}
	    
	    
	    this.addLSMSensorWindow = function(){			
			var div = document.createElement("div");
			div.style.left = blockMeta.x + "px";
			div.style.top = blockMeta.y + "px";
			div.style.width = "12em";
			div.style.height = "5.5em";
			div.style.zIndex = 100;
			div.className = "LSMWindow window";
			div.setAttribute("id", "div-" + (eleNum ++));		
			blockId = div.id;
			
			var image = document.createElement("img");		
			image.style = "position: bottom";
			image.setAttribute("id", "text-" + (eleNum ));
			image.src = lsmIcon.get(blockMeta.type);
			div.appendChild(image);
			div.innerHTML += blockMeta.type + " <br/>";			
			var input = document.createElement("input");
			input.type = "text";
			input.style = "position: bottom";
			input.setAttribute("id", "url"+div.id);			
			input.className = "URLInput";
						
			var childDiv = document.createElement("div");
			childDiv.className = "context-menu-sub box menu-1";			
			childDiv.appendChild(input);
			if(blockMeta.type!="COSM")
				childDiv.innerHTML +='<img title="Please input your LSM sensor URL or use the LSM discovery functionality " alt="" src="images/8x8/question.png"/>';
			else{				
				var img = document.createElement('img');
        		img.src = "images/8x8/question.png";
        		img.title = "Help - How to integrate your COSM sensor";
        		img.id = "COSMHelp";
        		childDiv.appendChild(img);
			}
			var tag = '<br/><img style="margin-right:7px;" src="images/8x8/find.png" title="Discover" class="cmdLink discover" rel="'+div.id+'"/>'+
					  '<img style="margin-right:7px;" src="images/8x8/detach.png" title="Detach" class="cmdLink detach" rel="'+div.id+'"/>'+
					  '<img style="margin-right:7px;" src="images/8x8/remove.png" title="Remove" class="cmdLink remove" rel="'+div.id+'"/>' +						  
					  '<img style="margin-right:7px;" src="images/8x8/submit.png" title="Debug" class="cmdLink submit" rel="'+div.id+'"/>' +
					  '<img style="margin-right:7px;" src="images/8x8/publish.png" title="Publish" class="cmdLink publish" rel="'+div.id+'"/>';						  
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
			
			 $("#COSMHelp").click(function() {		
             	var content = "To integrate your COSM sensor into SSC system, please follow these steps:<br>" +
             			"1. Login to <a href='#https://cosm.com'>COSM website<a/>.<br>" +        			
             			"2. Copy the XML data format link of your COSM sensor(ex:<i>https://api.cosm.com/v2/feeds/41617.xml</i>).<br>"+
             			"3. Paste the link into the COSM text box.<br>"+
             			"4. Click <b>Debug</b> to import.<br>";
             	$( "#help" ).data("content",content).dialog('open');
     		});
			 
			if(blockMeta.url!=null)
				$("#"+input.id).val(blockMeta.url);
			
			blockId = div.id;			
			blockMeta.setId(div.id);		
			blockMeta.setSocketURL('ws://' +  lsmSensorSocket);			
			blockMeta.setSparql(defaultQuery);
			
			div.setAttribute("meta",blockMeta);
			metaHash.put(div.id,this);
			if(selectedBlock==null ) selectedBlock = div.id;
			
			$(div).click(function(){		
				$("#txtQuery").attr('readonly',true);
				onClick();
			});
		}
	    
	    this.addURLWindow = function(){			
			var div = document.createElement("div");
			div.style.left = blockMeta.x + "px";
			div.style.top = blockMeta.y + "px";
			div.style.zIndex = 100;
			div.style.width = "12em";
			div.style.height = "5.3em";
			div.className = "sources";		
			div.setAttribute("id", "div-" + (eleNum ++));		
			blockId = div.id;
			
			var image = document.createElement("img");		
			image.style = "position: bottom";
			image.setAttribute("id", "text-" + (eleNum ));
			image.src = lsmIcon.get(blockMeta.type);
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
			var tag = '<br/><img style="margin-right:7px;" src="images/8x8/find.png" title="Discover" class="cmdLink URLdiscover" rel="'+div.id+'"/>'+
			  '<img style="margin-right:7px;" src="images/8x8/detach.png" title="Detach" class="cmdLink detach" rel="'+div.id+'"/>'+
			  '<img style="margin-right:7px;" src="images/8x8/remove.png" title="Remove" class="cmdLink remove" rel="'+div.id+'"/>' +						  
			  '<img style="margin-right:7px;" src="images/8x8/submit.png" title="Debug" class="cmdLink submit" rel="'+div.id+'"/>' +
			  '<img style="margin-right:7px;" src="images/8x8/publish.png" title="Publish" class="cmdLink publish" rel="'+div.id+'"/>';							  
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
			if(blockMeta.url!=null)
				$("#"+input.id).val(blockMeta.url);
						
			blockId = div.id;
			blockMeta.setId(div.id);		
			blockMeta.setSocketURL('ws://' +  lsmSensorSocket);
						
			div.setAttribute("meta",blockMeta);
			metaHash.put(div.id,this);
			if(selectedBlock==null ) selectedBlock = div.id;
			
			$(div).click(function(){		
				onClick();
				$("#txtQuery").attr('readonly',true);				
			});

		}
	    
	    this.addTimepicker = function(){
	    	var div = document.createElement("div");
			div.style.left = blockMeta.x + "px";
			div.style.top = blockMeta.y + "px";
			div.style.zIndex = 100;		
			div.style.width = "15em";
			div.style.height = "5.3em";
			div.className = "operators";
			div.setAttribute("id", "div-" + (eleNum ++));
			blockId = div.id;
			
			var image = document.createElement("img");		
			image.style = "position: bottom";
			image.setAttribute("id", "text-" + (eleNum ));
			image.src = lsmIcon.get(blockMeta.type);
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
//			operInput.onchange= function(){dbrOptionChange()};
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
			var tag = '<img style="margin-right:7px;" src="images/8x8/detach.png" title="Detach" class="cmdLink detach" rel="'+div.id+'"/>'+
			  		'<img style="margin-right:7px;" src="images/8x8/remove.png" title="Remove" class="cmdLink remove" rel="'+div.id+'"/>';	
			childDiv.innerHTML+=tag;
			childDiv.setAttribute("id", "childDiv-" + (eleNum ));
			div.appendChild(childDiv);	
			$("#demo").append(div);
			
			var divsWithWindowClass = jsPlumb.CurrentLibrary.getSelector(".operators");
			jsPlumb.draggable(divsWithWindowClass);
			var p = jsPlumb.addEndpoint(div, { anchor:anchors }, filterEndpointSource);	
			p.id = div.id + "ep_0";
			endpoints.push(p);
			blockId = div.id;
			blockMeta.setId(div.id);
			div.setAttribute("meta",blockMeta);
			metaHash.put(div.id,this);			
			
			if(blockMeta.url!=null)
				$("#"+input.id).val(blockMeta.url);
			
			$(div).focusout(function(){ 	
				setFilter(this.id);
			});
			
			operInput.onchange=function(){
				setFilter(div.id);
			}
			$(div).click(function(){
				$("#txtQuery").attr('readonly',true);
			});
	    }
	    
	    this.addLocationWindow = function(){			
			var div = document.createElement("div");
			div.style.left = blockMeta.x + "px";
			div.style.top = blockMeta.y + "px";
			div.style.zIndex = 100;
			div.className = "operators";
			div.setAttribute("id", "div-" + (eleNum ++));		
			blockId = div.id;
			
			var image = document.createElement("img");		
			image.style = "position: bottom";
			image.setAttribute("id", "text-" + (eleNum ));
			image.src = lsmIcon.get(blockMeta.type);
			div.appendChild(image);
			div.innerHTML += "Location <br/>";
			
			var input = document.createElement("input");
			input.type = "text";
			input.style = "position: bottom";
			input.style.width = "80%";
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
			div.innerHTML+="  Place  ";
			div.appendChild(input);
			childDiv.innerHTML+="</br";
			childDiv.innerHTML+="  Lat  ";
			childDiv.appendChild(lat);
			childDiv.innerHTML+="  Long  ";
			childDiv.appendChild(lng);
			var tag = '<br/><img style="margin-right:7px;" src="images/8x8/detach.png" title="Detach" class="cmdLink detach" rel="'+div.id+'"/>'+
			  '<img style="margin-right:7px;" src="images/8x8/remove.png" title="Remove" class="cmdLink remove" rel="'+div.id+'"/>';	
			childDiv.innerHTML+=tag;
			childDiv.setAttribute("id", "childDiv-" + (eleNum ));
			div.appendChild(childDiv);
			
			$("#demo").append(div);		

			var divsWithWindowClass = jsPlumb.CurrentLibrary.getSelector(".operators");
			jsPlumb.draggable(divsWithWindowClass);
			var p = jsPlumb.addEndpoint(div, { anchor:anchors }, locationEndpoint);		
			p.id = div.id + "ep_0";
			endpoints.push(p);

			blockId = div.id;
			blockMeta.setId(div.id);	
						
			div.setAttribute("meta",blockMeta);
			metaHash.put(div.id,this);
			
			$(div).focusout(function(){ 		
				setFilter(div);
			});
			
			if(blockMeta.place!=null){
				$("#"+input.id).val(blockMeta.place.address);
				$("#"+div.id+"lat").val(blockMeta.place.lat);
				$("#"+div.id+"long").val(blockMeta.place.long);
			}
			
			
			$("#"+input.id).keyup(function (e) {
			    if (e.keyCode == 13) {
			    	var place = new Object();
					place.lat = $("#"+div.id+"lat").val();
					place.long =  $("#"+div.id+"long").val();
					place.info =  $(this).val();
					
					var interval = startProgressBar();
					$( "#loading-form" ).dialog('open');
								
					var message = JSON.stringify(place);
					$.getJSON(SSCHost+"get_location.html",{info:message}, function(data) {
						clearInterval(interval);
						$( "#loading-form" ).dialog('close');
						$("#"+input.id).val(data.address);
						$("#"+div.id+"lat").val(data.lat);
						$("#"+div.id+"long").val(data.long);
						blockMeta.setPlace(data);
					}, "json");
			    }
			});
			$(div).click(function(){
				$("#txtQuery").attr('readonly',true);
			});
//			$("#"+input.id).onEnter(function(){
//				var place = new Object();
//				place.lat = $("#"+div.id+"lat").val();
//				place.long =  $("#"+div.id+"long").val();
//				place.info =  $(this).val();
//							
//				var message = JSON.stringify(place);
//				$.getJSON(SSCHost+"get_location.html",{info:message}, function(data) {
//					$("#"+input.id).val(data.address);
//					$("#"+div.id+"lat").val(data.lat);
//					$("#"+div.id+"long").val(data.long);
//					blockMeta.setPlace(data);
//				}, "json");
//			});
		}
	    
	    function startProgressBar(){
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
	    
	    this.addToStreamWindow = function(){			
			var div = document.createElement("div");
			div.style.left = blockMeta.x + "px";
			div.style.top = blockMeta.y + "px";
			div.style.zIndex = 103;
			div.style.width = "130px";
			div.style.height = "70px";
			div.className = "operators";
			div.setAttribute("id", "div-" + (eleNum ++));				
			blockId = div.id;
			
			var image = document.createElement("img");		
			image.style = "position: bottom";
			image.setAttribute("id", "text-" + (eleNum ));
			image.src = lsmIcon.get(blockMeta.type);
			div.appendChild(image);
			div.innerHTML += " To RDF Stream <br/>";
			
			var input = document.createElement("input");
			input.type = "text";
//			input.style = "position: bottom";
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
			var tag = '<img style="margin-right:7px;" src="images/8x8/detach.png" title="Detach" class="cmdLink detach" rel="'+div.id+'"/>'+
			  '<img style="margin-right:7px;" src="images/8x8/remove.png" title="Remove" class="cmdLink remove" rel="'+div.id+'"/>' +						  
			  '<img style="margin-right:7px;" src="images/8x8/submit.png" title="Debug" class="cmdLink submit" rel="'+div.id+'"/>' +
			  '<img style="margin-right:7px;" src="images/8x8/publish.png" title="Publish" class="cmdLink publish" rel="'+div.id+'"/>';							  
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
			
			blockId = div.id;
			blockMeta.setId(div.id);	
			blockMeta.setSocketURL('ws://' +  lsmCqelsSocket);
						
			div.setAttribute("meta",blockMeta);
			metaHash.put(div.id,this);
			
			$("#"+div.id+"iterUnit").addClass("ui-widget ui-widget-content ui-corner-left").autocomplete({
			    source: timeUnits,
			    minLength: 0,		    
			    select: function( event, ui ) {
					ui.item.selected = true;
					var item= ui.item.value;
					blockMeta.setURL(item);
					for (var j = 0; j < connections.length; j++) {
						if((connections[j].scope==="filter dot")&&
								(connections[j].targetId===div.id||connections[j].sourceId===div.id)){	
							var t = connections[j].targetId===div.id?connections[j].sourceId:connections[j].targetId;
							var blMeta = metaHash.get(t).getBlockMeta();
							if(blMeta.type==='Location'){
								blockMeta.setPlace(blMeta.place)
								$("#txtQuery").val(getPlaceSparqlQuery(blockMeta.place));				
								blockMeta.setSparql(getPlaceSparqlQuery(blockMeta.place));
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
				var previousBlock = metaHash.get(selectedBlock);
		        if((previousBlock!=null)&&(previousBlock.getId()!=div.id)){
		        	previousBlock.getBlockMeta().setSparql($("#txtQuery").val());
		        	selectedBlock = this.id;				
		        	sparqlReload();
		        	$("#txtQuery").attr('readonly',true);
		        }
			});
		}
	    function setFilter(targetId){					
			var meta = metaHash.get(targetId);
			if(meta==null) return;
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
//			alert(meta.filterArr);
		 }
	    
	    
	    this.addLSMStreamWindow = function(){			
			var div = document.createElement("div");
			div.style.left = blockMeta.x + "px";
			div.style.top = blockMeta.y + "px";
			div.style.zIndex = 100;
			div.style.height = "3.7em";
			div.className = "sources";
			
			div.setAttribute("id", "div-" + (eleNum ++));	
			blockId = div.id;
			
			var image = document.createElement("img");		
			image.style = "position: bottom";
			image.setAttribute("id", "text-" + (eleNum ));
			image.src = lsmIcon.get(blockMeta.type);
			div.appendChild(image);
			div.innerHTML += "LSM Stream";
			
			var childDiv = document.createElement("div");
			childDiv.className = "context-menu-sub box menu-1";		
			var tag = '<img style="margin-right:7px;" src="images/8x8/detach.png" title="Detach" class="cmdLink detach" rel="'+div.id+'"/>'+
			  '<img style="margin-right:7px;" src="images/8x8/remove.png" title="Remove" class="cmdLink remove" rel="'+div.id+'"/>' +						  
			  '<img style="margin-right:7px;" src="images/8x8/submit.png" title="Debug" class="cmdLink submit" rel="'+div.id+'"/>' +
			  '<img style="margin-right:7px;" src="images/8x8/publish.png" title="Publish" class="cmdLink publish" rel="'+div.id+'"/>';							  
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
			
			blockId = div.id;
			blockMeta.setId(div.id);		
			blockMeta.setSocketURL('ws://' +  lsmStreamSocket);
			
			div.setAttribute("meta",blockMeta);
			metaHash.put(div.id,this);
			if(selectedBlock==null ) selectedBlock = div.id;
				
			$(div).click(function(){		
				var previousBlock = metaHash.get(selectedBlock);
		        if((previousBlock!=null)&&(previousBlock.getId()!=div.id))
		        	previousBlock.getBlockmeta().setSparql($("#txtQuery").val());
				selectedBlock = this.id;				
				sparqlReload(blockMeta);
//				if((blockMeta.sparql==null)||(blockMeta.sparql===""))
					$("#txtQuery").attr('readonly',true);			
			});

		}
	    
	    this.addTwitterStreamWindow = function(){			
			var div = document.createElement("div");
			div.style.left = blockMeta.x + "px";
			div.style.top = blockMeta.y + "px";
			div.style.zIndex = 100;
			div.style.height="4.8em";
			div.className = "sources";
			div.setAttribute("id", "div-" + (eleNum ++));	
			blockId = div.id;
			
			var image = document.createElement("img");		
			image.style = "position: bottom";
			image.setAttribute("id", "text-" + (eleNum ));
			image.src = lsmIcon.get(blockMeta.type);
			div.appendChild(image);
			
			var label = document.createElement("label");
			label.innerHTML='tags ';
			
			var input = document.createElement("input");
			input.className = "URLInput";
			input.type = "text";
			input.style = "position: bottom";
			input.setAttribute("id", "text-" + (eleNum ));		
			input.title = "Input tweet keywords here"
			if(blockMeta.url!=null)
				input.setAttribute("value",blockMeta.url);
			
			var childDiv = document.createElement("div");
			childDiv.className = "context-menu-sub box menu-1";
			childDiv.appendChild(label);
			childDiv.appendChild(input);
			var tag = '<br/><img style="margin-right:7px;" src="images/8x8/detach.png" title="Detach" class="cmdLink detach" rel="'+div.id+'"/>'+
			  '<img style="margin-right:7px;" src="images/8x8/remove.png" title="Remove" class="cmdLink remove" rel="'+div.id+'"/>' +						  
			  '<img style="margin-right:7px;" src="images/8x8/submit.png" title="Debug" class="cmdLink submit" rel="'+div.id+'"/>' +
			  '<img style="margin-right:7px;" src="images/8x8/publish.png" title="Publish" class="cmdLink publish" rel="'+div.id+'"/>';							  
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
						
			blockId = div.id;
			blockMeta.setId(div.id);		
			blockMeta.setSocketURL('ws://' +  lsmTwitterSocket);
			
			div.setAttribute("meta",blockMeta);
			metaHash.put(div.id,this);
			if(selectedBlock==null ) selectedBlock = div.id;

			var tweetDiv = document.createElement("div");					
			tweetDiv.className = "tweets";				
			tweetDiv.setAttribute("id", "twitterSearch"+div.id);
			$('#visualizationTab').append(tweetDiv);
						
			$(div).click(function(){		
				var previousBlock = metaHash.get(selectedBlock);
		        if((previousBlock!=null)&&(previousBlock.getId()!=div.id)){
		        	previousBlock.getBlockMeta().setSparql($("#txtQuery").val());
					selectedBlock = this.id;				
					sparqlReload(blockMeta);
//					if((blockMeta.sparql==null)||(blockMeta.sparql===""))
						$("#txtQuery").attr('readonly',true);					
					
					$('#visualizationTab').empty();
					var s = $('#visualizationTab');
					$('#visualizationTab').append(tweetDiv);			
					
				}
			});

		}
	    
	      
	    this.addEndPointWindow = function(){			
			var div = document.createElement("div");
			div.style.left = blockMeta.x + "px";
			div.style.top = blockMeta.y + "px";
			div.style.zIndex = 100;
			div.className = "SparqlEndPoint";
			div.setAttribute("id", "div-" + (eleNum ++));		
			blockId = div.id;
			
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
			childDiv.innerHTML +='<img title="Please select the SPARQL Endpoint and input your query in the Query text box" alt="" src="images/8x8/question.png"/>';
			
			tag = '<br/><img style="margin-right:7px;" src="images/8x8/detach.png" title="Detach" class="cmdLink detach" rel="'+div.id+'"/>'+
			  '<img style="margin-right:7px;" src="images/8x8/remove.png" title="Remove" class="cmdLink remove" rel="'+div.id+'"/>' +						  
			  '<img style="margin-right:7px;" src="images/8x8/submit.png" title="Debug" class="cmdLink submit" rel="'+div.id+'"/>' +
			  '<img style="margin-right:7px;" src="images/8x8/publish.png" title="Publish" class="cmdLink publish" rel="'+div.id+'"/>';							  
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
			
			if(blockMeta.url!=null)
				$("#"+input.id).val(blockMeta.url);
			if(blockMeta.sparql!=null)
				$("#txtQuery").val(blockMeta.sparql);
			blockId = div.id;
			blockMeta.setId(div.id);		
			blockMeta.setSocketURL('ws://' +  lsmEndPointSocket);

			div.setAttribute("meta",blockMeta);
			metaHash.put(div.id,this);
			if(selectedBlock==null ) selectedBlock = div.id;

			$(div).click(function(){	
				$("#txtQuery").attr('readonly',false);
			   	onClick();
			});

			$("#"+div.id+"endpoint").addClass("ui-widget ui-widget-content ui-corner-left").autocomplete({
			    source: availableSparqlEndPoints,
			    minLength: 0,		    
			    select: function( event, ui ) {
					ui.item.selected = true;
					var item= ui.item.value;
					blockMeta.setURL(item);
//					alert(item);
					for (var j = 0; j < connections.length; j++) {
						if((connections[j].scope==="filter dot")&&
								(connections[j].targetId===div.id||connections[j].sourceId===div.id)){	
							var t = connections[j].targetId===div.id?connections[j].sourceId:connections[j].targetId;
							var blMeta = metaHash.get(t).getBlockMeta();
							if(blMeta.type==='Location'){
								blockMeta.setPlace(blMeta.place)
								$("#txtQuery").val(getPlaceSparqlQuery(blockMeta.place));				
								blockMeta.setSparql(getPlaceSparqlQuery(blockMeta.place));								
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
	    
	    this.addCqelsWindow = function(){			
			var div = document.createElement("div");
			div.style.left = blockMeta.x + "px";
			div.style.top = blockMeta.y + "px";
			div.style.zIndex = 100;		
			div.style.width = "12em";
			div.style.height = "3.5em";		
			div.className = "operators";		
			div.setAttribute("id", "div-" + (eleNum ++));		
			blockId = div.id;
			
			var image = document.createElement("img");		
			image.style = "position: bottom";
			image.setAttribute("id", "text-" + (eleNum ));
			image.src = lsmIcon.get(blockMeta.type);
			div.appendChild(image);
			div.innerHTML += "Cqels filter<br/>";
			
			var childDiv = document.createElement("div");
			childDiv.className = "context-menu-sub box menu-1";		
			var tag = '<img style="margin-right:7px;" src="images/8x8/cqels-editor.png" title="Cqels editor" class="cmdLink cqels_editor" rel="'+div.id+'"/>'+
			  '<img style="margin-right:7px;" src="images/8x8/detach.png" title="Detach" class="cmdLink detach" rel="'+div.id+'"/>'+
			  '<img style="margin-right:7px;" src="images/8x8/remove.png" title="Remove" class="cmdLink remove" rel="'+div.id+'"/>';	
			childDiv.innerHTML+=tag;
			childDiv.setAttribute("id", "childDiv-" + (eleNum ));
			div.appendChild(childDiv);							
//			var id = "#"+input.id;
			$("#demo").append(div);			
			
			var divsWithWindowClass = jsPlumb.CurrentLibrary.getSelector(".operators");
			jsPlumb.draggable(divsWithWindowClass);
			var p = jsPlumb.addEndpoint(div, { anchor:anchors }, cqelsEndpoint);	
			p.id = div.id + "ep_0";
			endpoints.push(p);
			
			if(blockMeta.sparql!=null)
				$("#txtQuery").val(blockMeta.sparql);
			blockId = div.id;
			blockMeta.setId(div.id);	
			
			div.setAttribute("meta",blockMeta);
			metaHash.put(div.id,this);
			$(div).click(function(){		
				$("#txtQuery").attr('readonly',false);
				onClick();
			});

		}
	    
	    function sparqlReload(){	    		    	
	    	$("#txtQuery").val(blockMeta.sparql);
	    	$("#txtRawData").val('');
	    	$('#tableContent').find("table:first").remove();
	    	$('#visualizationTab').empty();
	    	loadLatestData(blockId);
//	    	$('#tableContent').remove("table:first");
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
//	    				  '		?id ?p ?o.\n'+
	    				'	} order by ?distance limit 20\n';
	    	return query;
	    }
		

	}
	return Block;
})();