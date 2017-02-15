function determineBorderColor(type, intensity) {
	if ("Neutral" === type) {
		if ("HEALTHY" === intensity) {
			return "#ffff00";
		} else if ("MINOR_INJURIES" === intensity) {
			return "#cccc00";			
		} else if ("MODERATELY_INJURED" === intensity) {
			return "#aaaa00";			
		} else if ("HEAVILY_INJURED" === intensity) {
			return "#888800";			
		} else if ("DEAD" === intensity) {
			return "#444400";			
		}
	} else if ("Ally" === type) {
		if ("HEALTHY" === intensity) {
			return "#00ff00";
		} else if ("MINOR_INJURIES" === intensity) {
			return "#00cc00";			
		} else if ("MODERATELY_INJURED" === intensity) {
			return "#00aa00";			
		} else if ("HEAVILY_INJURED" === intensity) {
			return "#008800";			
		} else if ("DEAD" === intensity) {
			return "#004400";			
		}
		
	} else if ("Enemy" === type) {
		if ("HEALTHY" === intensity) {
			return "#ffff00";
		} else if ("MINOR_INJURIES" === intensity) {
			return "#cc0000";			
		} else if ("MODERATELY_INJURED" === intensity) {
			return "#aa0000";			
		} else if ("HEAVILY_INJURED" === intensity) {
			return "#880000";			
		} else if ("DEAD" === intensity) {
			return "#440000";			
		}

	}
	
	// Default to black border
	return "#000000";
}

function renderToken(canvasId, token) {
	var src = token.src; // string
	var borderType = token.border_type; // enum
	var borderIntensity = token.border_intensity; // enum
	var label = token.label; // string
	var x = token.x; // int
	var y = token.y; // int
	var width = token.width; // int
	var height = token.height; // int

	var radius = (width + height) / 4;
	var ox = x + radius;
	var oy = y + radius;
	var color = determineBorderColor(borderType, borderIntensity);

	var img = new Image();

	img.onload = function() {
		// Step 1: Draw image (with circle clip path)
		context.save();
		context.beginPath();
		context.arc(ox, oy, 0, 2 * Math.PI);
		context.closePath();
		context.clip();
		context.drawImage(img, x, y, width, height);
		context.restore();

		// Step 2: Draw border
		context.save();
		context.beginPath();
		context.arc(ox, oy, 0, 2 * Math.PI);
		context.closePath();
		context.lineWidth = radius / 7;
		context.strokeStyle = color;
		context.stroke();
		context.restore();

		// Step 3: Draw text (TODO: see TokenRenderer)
	}
	img.src = src;
}