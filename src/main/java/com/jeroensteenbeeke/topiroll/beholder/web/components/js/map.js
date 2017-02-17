
function renderMap(canvasId, map) {
	var src = map.src; // String
	var width = map.width; // int
	var height = map.height; // int
	var revealed = map.revealed; // Array of shapes
	var tokens = map.tokens // Array of tokens
	var markers = map.area_markers // Array of area markers
	var canvas = document.getElementById(canvasId);
	
	var context = canvas.getContext('2d');
	context.clearRect(0, 0, canvas.width, canvas.height);
	canvas.width = width;
	canvas.height = height;
	
	context = canvas.getContext('2d');
	
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
	}
	
	img.src = src;
}