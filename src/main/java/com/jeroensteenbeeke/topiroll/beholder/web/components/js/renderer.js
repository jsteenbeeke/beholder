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
var renderListeners = [];


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

        function handleData(data) {
            if (data.type) {
                if ("map" === data.type) {
                    renderMap(containerId, data);
                } else if ("initiative" === data.type) {
                    renderInitiative(data);
                } else if ("clear" === data.type) {
                    clearMap(containerId);
                } else if ("portraits" === data.type) {
                    updatePortraits(data);
                } else if ("youtube" === data.type) {
                    document.getElementById('youtube').src = data.url;
                } else if ("scroll" === data.type) {
                    var w = Math.max(document.documentElement.clientWidth, window.innerWidth || 0);
                    var h = Math.max(document.documentElement.clientHeight, window.innerHeight || 0);

                    window.scrollTo(data.x - w / 4, data.y - h / 4);
                } else if ("compound" === data.type) {
                	data.renderables.forEach(function(d) {
                		handleData(d);
                    });
				} else if ("doorbell" === data.type) {

                    let doorbellContainer = document.getElementById('doorbell');

                    if (doorbellContainer) {
                        doorbellContainer.innerText = 'Doorbell rung by '+ data.username;

                        let audio = new Audio('../audio/doorbell.mp3');
                        audio.play();

                        setTimeout(function() {
                            doorbellContainer.innerText = '';
                        }, 30000);


                    }

                }
            }
        }


        handleData(payload.data);
	}
});
