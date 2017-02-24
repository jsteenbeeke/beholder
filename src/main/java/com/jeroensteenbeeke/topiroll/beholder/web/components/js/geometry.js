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
function applyRectangle(context, rect) {
	var x = rect.x; // int
	var y = rect.y; // int
	var width = rect.width; // int
	var height = rect.height; // int

	context.moveTo(x, y);
	context.rect(x, y, width, height);
}

function applyCircle(context, circle) {
	var x = circle.x + circle.radius; // int
	var y = circle.y + circle.radius; // int
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
