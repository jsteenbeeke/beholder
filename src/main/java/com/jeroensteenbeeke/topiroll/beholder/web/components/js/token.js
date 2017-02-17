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
			return "#ff0000";
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

function drawText(context, token) {
	var src = token.src; // string
	var borderType = token.border_type; // enum
	var borderIntensity = token.border_intensity; // enum
	var label = token.label; // string
	var x = token.x; // int
	var y = token.y; // int
	var width = token.width; // int
	var height = token.height; // int
	var color = "#000000;"

	var radius = (width + height) / 4;
	var ox = x + radius;
	var oy = y + radius;
	
	var box_top_y = y + 5 * (height / 6);
	var box_bottom_y = y + height;
	
	var box_height = height / 6;
	
	var text_ratio = 0.375 * radius;
	var box_ratio = 0.5 * radius;
	
	var box_width = width;
	var text_width = label.length * text_ratio;
	
	var box_left_x = x;
	var text_x = box_left_x + (box_width / 2);
	var text_y = box_bottom_y - (box_height / 6);
	var char_count = label.length;
	
	var horizontal_pixels_per_character = Math.round(box_width / char_count);	
	// Assume 33% increase converting from text to pixels
	var text_scale = Math.round(1.2 * Math.min(horizontal_pixels_per_character, box_height));
	
	text_x = text_x - ((horizontal_pixels_per_character * char_count) / 2.4);
	
	context.save();
	context.lineWidth = 1;
	context.strokeStyle = color;
	context.fillStyle = '#ffffff';
	context.moveTo(box_left_x, box_top_y);
	context.fillRect(box_left_x, box_top_y, box_width, box_height);
	context.strokeRect(box_left_x, box_top_y, box_width, box_height);
	context.restore();
	
	context.save();
	context.fillStyle = color;
	context.font = text_scale + 'pt Arial';
	context.fillText(label, text_x, text_y);

	context.restore();
}

	

function renderToken(context, token) {
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
		context.arc(ox, oy, radius, 0, 2 * Math.PI);
		context.closePath();
		context.clip();
		context.drawImage(img, x, y, width, height);
		context.restore();

		// Step 2: Draw border
		context.save();
		context.beginPath();
		context.arc(ox, oy, radius, 0, 2 * Math.PI);
		context.closePath();
		context.lineWidth = radius / 7;
		context.strokeStyle = color;
		context.stroke();
		context.restore();

		// Step 3: Draw text
		if (label) {
			drawText(context, token);
		}
	}
	img.src = src;
}