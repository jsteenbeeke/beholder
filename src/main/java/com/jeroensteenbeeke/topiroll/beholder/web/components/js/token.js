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
	context.setLineWidth(1);
	context.setStrokeStyle(color);
	context.setFillStyle('#ffffff');
	context.moveTo(box_left_x, box_top_y);
	context.fillRect(box_left_x, box_top_y, box_width, box_height);
	context.strokeRect(box_left_x, box_top_y, box_width, box_height);
	context.restore();
	
	context.save();
	context.setFillStyle(color);
	context.setFont(text_scale + 'pt Arial');
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
		context.setLineWidth(radius / 7);
		context.setStrokeStyle(color);
		context.stroke();
		context.restore();

		// Step 3: Draw text
		if (label) {
			drawText(context, token);
		}
	}
	img.src = src;
}