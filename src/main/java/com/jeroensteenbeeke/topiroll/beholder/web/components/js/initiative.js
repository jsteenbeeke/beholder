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

function renderInitiative(initiative) {
	var id = initiative.id;
	var show = initiative.show;
	var position = initiative.position;
	var participants = initiative.participants;

	var element = document.getElementByID(id);

	if (element) {
		if (show) {
			var style = 'display: block; z-index: 1; position: fixed; ';
			var html = '';

			if (position.indexOf('top') !== -1) {
				style = style + 'top: 5px;';
			} else if (position.indexOf('bottom') !== -1) {
				style = style + 'bottom: 5px;';
			}

			if (position.indexOf('left') !== -1) {
				style = style + 'left: 5px;'
			} else if (position.indexOf('right') !== -1) {
				style = style + 'right: 5px;';
			}

			element.style.cssText = style;

			participants.forEach(function(p) {
				var name = p.name;
				var score = p.score;
				var selected = p.selected;
				
				html = html + '<button class="btn ';
				if (selected) {
					html = html + 'btn-primary';
				} else {
					html = html + 'btn-default';
				}
				html = html + '" type="button">';
				html = html + name;
				html = html + '<span class="badge">';
				html = html + p.score;
				html = html + '</span></button>';
			});
			
			element.innerHtml = html;

		} else {
			element.style.cssText = 'display: none;';
		}
	}
}