var BlockDom = (function() {
	var FUNCTION = "function";
	
	function BlockDom(metaHash,connections,viewName) {
		var blockArray = new Array();
		var connectionArray;
		
		connectionArray = new Array();
		var that = this;
		var blocks = metaHash==null?null:metaHash.values();
		var js = new Object();
		
		this.blockDomToJson = function() {
			for(i=0;i<blocks.length;i++){
				var meta = blocks[i].getBlockMeta();
				var block = new Object();
				block.type = meta.type;
				block.category = meta.category;
				block.filterArr = meta.filterArr;
				block.url = meta.url==null?'':meta.url;
				block.sparql = "";
				block.socketURL = meta.socketURL;
				block.id = meta.id;
				block.place = meta.place==null?'':meta.place;
				block.x = meta.x;
				block.y = meta.y;
				blockArray.push(block);				
			}		
						
			for(j=0;j<connections.length;j++){
				var conn = new Object();
				var e1 = connections[j].endpoints[0];
				var e2 = connections[j].endpoints[1];
				conn.source = e1.isSource==true?e1.id:e2.id;
				conn.target = e1.isTarget==true?e1.id:e2.id;
				conn.scope = connections[j].scope;
				connectionArray.push(conn);
			}
			
			js.blocks = blockArray;
			js.userId = user.userId;
			js.connections = connectionArray;
			js.viewname = viewName;
			var jArr = JSON.stringify(js);
			return jArr;
		};

		this.saveBlocks = function(json){
			$.getJSON(SSCHost+"userview_save.html",{blocks:json}, function(data) {
				if(data.success!=true){
					alert('Save failed!');
				}
			}, "json");
		};

		this.loadBlocks = function(){
			var jo = new Object();
			jo.userId = user.userId;
			jo.viewname = viewName;
			var jStr = JSON.stringify(jo);
			$.getJSON(SSCHost+"userview_load.html",{view:jStr}, function(data) {
				JsonToBlocksDom(data);
			}, "json");
		};

		
		JsonToBlocksDom  = function(data){
			if(data.success==false) return;			
			var view = data.view;
			var blockArr = view.blocks;
			var blockConnection = view.connections;
			for(var i=blockArr.length-1;i>=0;i--){
				var b = blockArr[i];				
				var block = new Block();
				block.initialized(b.x, b.y, b.category, b.type);
				block.getBlockMeta().setURL(b.url==null?'':b.url);
				block.getBlockMeta().setSparql(b.sparql==null?'':b.sparql);
				block.render();	
			}
						
			for(var j=0;j<blockConnection.length;j++){
				var e1 =null;
				var e2 = null;
				var conn = blockConnection[j];
				for(var k=0;k<endpoints.length;k++){
					if(endpoints[k].id === conn.source)
						e1 = endpoints[k];
					if(endpoints[k].id ===conn.target)
						e2 = endpoints[k];
				}
				if((e1!=null)&&(e2!=null))jsPlumb.connect({source:e1, target:e2});
			}
		};

	}
	return BlockDom;
})();