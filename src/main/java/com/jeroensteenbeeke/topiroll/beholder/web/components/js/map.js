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

var lastRenderedMap;
var multiCanvas;

function clearMap(containerId) {
    var container = document.getElementById(containerId);
    var children = [];
    for (i = 0; i < container.childElementCount; i++) {
        children.push(container.childNodes.item(i));
    }
    children.forEach(function(child) {
        container.removeChild(child);
    });
    lastRenderedMap = null;
    multiCanvas = null;
}

function renderMap(containerId, map) {
	var src = map.src; // String
	var width = map.width; // int
	var height = map.height; // int
	var revealed = map.revealed; // Array of shapes
	var tokens = map.tokens; // Array of tokens
	var markers = map.area_markers; // Array of area markers

	if (lastRenderedMap !== src) {
		clearMap(containerId);

		multiCanvas = new MultiCanvas(containerId, width, height);

		lastRenderedMap = src;
	}

	var context = multiCanvas.getContext('2d');
	context.clearRect(0, 0, multiCanvas.width, multiCanvas.height);

	context = multiCanvas.getContext('2d');
	
	var img = new Image();
	img.onload = function() {
	
		context.save();
		context.beginPath();
		if (revealed) {
			revealed.forEach(function(shape) {
				if (shape.type === 'rect') {
					applyRectangle(context, shape);
				} else if (shape.type === 'circle') {
					applyCircle(context, shape);
				} else if (shape.type === 'polygon') {
					applyPoly(context, shape);
				}
			});
		}
		
		context.closePath();
		context.clip();
		
		try {
			context.drawImage(img, 0, 0, width, height);
			
			tokens.forEach(function(token) {
				renderToken(context, token);
			});
			
			markers.forEach(function(marker) {
				renderMarker(context, marker);
			});
		} catch (e) {
			console.log(e.message, e.name);
		}
		
		context.restore();
	};
	
	img.src = src;
}