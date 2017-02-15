function applyRectangle(context, rect) {
	var x = rect.x; // int
	var y = rect.y; // int
	var width = rect.width; // int
	var height = rect.height; // int

	context.moveTo(x, y);
	context.rect(x, y, width, height);
}

function applyCircle(context, circle) {
	var x = circle.x; // int
	var y = circle.y; // int
	var radius = circle.radius; // int
	var thetaOffset = circle.theta_offset; // double
	var thetaExtent = circle.theta_extent; // double

	context.moveTo(x, y);
	context.arc(x, y, radius, thetaOffset, thetaOffset + thetaExtent);
}

function applyPoly(context, poly) {
	var points = poly.points;
	var final = points[points.length-1];
	
	context.moveTo(final.x, final.y);
	
	points.forEach(function(point) {
		var x = point.x;
		var y = point.y;
		context.lineTo(x,y);
	});
}
