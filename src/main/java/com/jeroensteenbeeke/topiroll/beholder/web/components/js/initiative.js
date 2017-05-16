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
	var show = initiative.show;
	var position = initiative.position;
	var participants = initiative.participants;
	var margin = initiative.margin;

	var element = document.getElementById("initiative");

	if (element) {
		if (show) {
			var style = 'display: block; z-index: 1; position: fixed; ';
			var html = '';

			if (position.indexOf('top') !== -1) {
				style = style + 'top: '+ margin +'px;';
			} else if (position.indexOf('bottom') !== -1) {
				style = style + 'bottom: '+ margin +'px;';
			}

			if (position.indexOf('left') !== -1) {
				style = style + 'left: '+ margin +'px;'
			} else if (position.indexOf('right') !== -1) {
				style = style + 'right: '+ margin +'px;';
			}

			element.style.cssText = style;

			participants.forEach(function(p) {
				var name = p.name;
				var score = p.score;
				var selected = p.selected;
				
				html = html + '<button class="btn ';
				if (selected) {
					html = html + 'btn-success';
				} else {
					html = html + 'btn-default';
				}
				html = html + '" type="button"><span class="pull-left">';
				html = html + name;
				html = html + '</span>&nbsp;&nbsp;<span class="badge pull-right">';
				html = html + p.score;
				html = html + '</span></button>';
			});
			
			element.innerHTML = html;

		} else {
			element.style.cssText = 'display: none;';
		}
	}
}