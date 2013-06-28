Array.prototype.remove = function(from, to) {
  var rest = this.slice((to || from) + 1 || this.length);
  this.length = from < 0 ? this.length + from : from;
  return this.push.apply(this, rest);
}

function BlockMeta(id) {
	this.id = id;
	this.filterArr = new Array();
	this.childs = new Array();
} 

function BlockMeta() {
	this.filterArr = new Array();
	this.childs = new Array();
} 

BlockMeta.prototype.getId = function(){
	return this.id;
}

BlockMeta.prototype.setId = function(id){
	this.id = id;
}

BlockMeta.prototype.setSparql = function(sparql){
	this.sparql = sparql;
}

BlockMeta.prototype.setURL = function(url){
	this.url = url;
}

BlockMeta.prototype.setX = function(x){
	this.x = x;	
}

BlockMeta.prototype.setY = function(y){
	this.y = y;	
}

BlockMeta.prototype.setXY = function(x,y){
	this.y = y;	
	this.x = x;
}

BlockMeta.prototype.setIsDetach = function(isDetach){
	this.isDetach = isDetach;
}

BlockMeta.prototype.setUserId = function(userId){
	this.userId = userId;
}

BlockMeta.prototype.setIterationId = function(iterationId){
	this.iterationId = iterationId;
}

BlockMeta.prototype.setSocketURL = function(socketURL){
	this.socketURL = socketURL;
}

//for sources window
BlockMeta.prototype.addFilter = function(newFilter){
	this.filterArr.push(newFilter);
}

//for operator window
BlockMeta.prototype.setFilter = function(filter){	
	this.filterArr = filter;
}


BlockMeta.prototype.setType = function(type){
	this.type = type;
}

BlockMeta.prototype.setCategory = function(category){
	this.category = category;
}

BlockMeta.prototype.setPlace = function(place){
	this.place = place;
}

//BlockMeta.prototype.setLatestData = function(data){
//	this.latestData = data;
//}

BlockMeta.prototype.addChild = function(block){
	this.childs[this.childs.length] = block;
}

BlockMeta.prototype.removeChild = function(block){
	for(var i=0;i<this.childs.length;i++){
		var id = this.childs[i].id;
		if(id ==block.id){
			this.childs.remove(i);
			return;
		}
	}		
}











