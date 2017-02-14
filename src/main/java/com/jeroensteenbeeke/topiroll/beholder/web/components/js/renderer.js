Wicket.Event.subscribe("/websocket/message", function(jqEvent, message) {
	var payload = JSON.parse(message);
	
	if (payload && payload.type) {
		if ("map" === payload.type) {
			renderMap(payload);
		}
	
	}
});