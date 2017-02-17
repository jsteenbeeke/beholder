Wicket.Event.subscribe("/websocket/closed", function(jqEvent, message) {
	window.location.reload(false); 	
});

Wicket.Event.subscribe("/websocket/error", function(jqEvent, message) {
	window.location.reload(false); 	
});

Wicket.Event.subscribe("/websocket/message", function(jqEvent, message) {
	var payload = JSON.parse(message);
	

	if (payload && payload.canvas_id && payload.data) {
		var canvasId = payload.canvas_id;
		var data = payload.data;
		var canvas = document.getElementById(canvasId);
		
		if (data.type) {
			if ("map" === data.type) {
				renderMap(canvasId, data);
			} else if ("clear" === data.type) {
				context = canvas.getContext('2d');
				
				context.clearRect(0, 0, canvas.width, canvas.height);
				
			}
		}
	
	}
});