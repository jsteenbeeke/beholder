function renderRectangle(canvasId, color, rect) {
	var x = rect.x; // int
	var y = rect.y; // int
	var width = rect.width; // int
	var height = rect.height; // int
	
	// TODO: render rectangle
	
}

function renderCircle(canvasId, color, circle) {
	var x = circle.x; // int
	var y = circle.y; // int
	var radius = circle.radius; // int
	
	// TODO: render circle
}

function renderPoly(canvasId, color, poly) {
	var points = poly.points; // Array of { x: int, y: int }	
	
	// TODO: render polygon
}

function renderMarker(canvasId, marker) {
	var color = marker.color;
	var shape = marker.shape;
	
	if (shape.type === 'rect') {
		renderRectangle(canvasId, color, shape);
	} else if (shape.type === 'circle') {
		renderCircle(canvasId, color, shape);
	} else if (shape.type === 'polygon') {
		renderPoly(canvasId, color, shape);
	}

}