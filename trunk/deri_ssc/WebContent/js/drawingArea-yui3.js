;(function() {

	var _initialised = false;
	var inputDataEndpoint,outputDataEndpoint,filterEndpoint,exampleEndpoint3,cqelsEndpoint,mergedDataEndpoint,locationEndpoint,filterEndpointSource;	
	var anchors;
	var connections;
	jsPlumbDemo.showConnectionInfo = function(s) {
		Y.one('#list').setContent(s);
		Y.one('#list').setStyle("display","block");
	};
	
	jsPlumbDemo.hideConnectionInfo = function() {
		Y.one('#list').setStyle("display","none");
	};
	
	jsPlumbDemo.attachBehaviour = function() {
		if (!_initialised) {
			Y.all(".hide").each(function(h) {
				h.on('click', function() {
//					jsPlumb.toggle(h.get("rel"));
				});
			});
			
			Y.all(".drag").each(function(d) {
				d.on('click', function() {
					var s = jsPlumb.toggleDraggable(d.get("rel"));
					d.setContent(s ? 'disable dragging' : 'enable dragging');
					var rel = "#" + d.getAttribute("rel");
					if (!s) Y.one(rel).addClass('drag-locked'); 
					else Y.one(rel).removeClass('drag-locked');
					Y.one(rel).setStyle("cursor", s ? "pointer" : "default");
				});
			});

			Y.all(".detach").each(function(d) {
				d.on('click', function() {
					jsPlumb.detachAllConnections(d.getAttribute("rel"));
				});
			});

			Y.one("#clear").on('click', function() { 
				jsPlumb.detachEveryConnection();
				jsPlumbDemo.showConnectionInfo("");
			});
			
//			Y.all(".remove").each(function(d) {
//				d.on('click', function() {
//					jsPlumb.detachAllConnections(d.getAttribute("rel"));
//					jsPlumb.removeAllEndpoints(d.getAttribute("rel"));
//					$(this).attr("rel").remove();
//				});
//			});

			_initialised = true;
		}
				
	};

})();