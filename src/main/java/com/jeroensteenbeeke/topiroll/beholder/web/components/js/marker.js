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