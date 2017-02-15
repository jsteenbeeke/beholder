Wicket.Event.subscribe("/websocket/message", function(jqEvent, message) {
	var payload = JSON.parse(message);
	
	if (payload && payload.canvas_id && payload.data) {
		var canvasId = payload.canvas_id;
		var data = payload.data;
		
		if (data.type) {
			if ("map" === data.type) {
				renderMap(canvasId, data);
			}
		}
	
	}
});