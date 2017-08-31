/*
 * This file is part of Beholder
 * (C) 2016 Jeroen Steenbeeke
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

var enableWebSocketReloadOnError = true;


Wicket.Event.subscribe("/websocket/notsupported", function(jqEvent, message) {
	enableWebSocketReloadOnError = false;
});

Wicket.Event.subscribe("/websocket/closed", function(jqEvent, message) {
	if (enableWebSocketReloadOnError === true) {
		Wicket.WebSocket.close();
		Wicket.WebSocket.createDefaultConnection();
	}
});

Wicket.Event.subscribe("/websocket/error", function(jqEvent, message) {
	if (enableWebSocketReloadOnError === true) {
		window.location.reload(false);
	}
});

Wicket.Event.subscribe("/websocket/message", function(jqEvent, message) {
	var payload = JSON.parse(message);


	if (payload && payload.data) {
		var containerId = payload.canvas_id;
		var data = payload.data;

		if (data.type) {
			if ("map" === data.type) {
				renderMap(containerId, data);
			} else if ("initiative" === data.type) {
				renderInitiative(data);
			} else if ("clear" === data.type) {
				clearMap(containerId);
			} else if ("portraits" === data.type) {
				updatePortraits(data);
			}
		}

	}
});
