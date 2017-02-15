function renderMarker(context, marker) {
	var color = marker.color;
	var shape = marker.shape;
	
	context.save();
	context.globalAlpha = 0.5;
	context.beginPath();
	if (shape.type === 'rect') {
		applyRectangle(context, shape);
	} else if (shape.type === 'circle') {
		applyCircle(context, shape);
	} else if (shape.type === 'polygon') {
		applyPoly(context, shape);
	}
	context.closePath();
	context.fillStyle = color;
	context.fill();
	
	context.restore();
}