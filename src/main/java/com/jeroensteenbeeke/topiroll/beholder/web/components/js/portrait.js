/*
 * This file is part of Beholder
 * (C) 2017 Jeroen Steenbeeke
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
function removePortraitImages(containerId) {
    var container = document.getElementById(containerId);
    var i;
    var remove = [];

    for (i = 0; i < container.childNodes.length; i++) {
        var elem = container.childNodes.item(i);
        if (elem.tagName === 'IMG') {
            remove.push(elem);
        }
    }

    remove.forEach(function(e) {
        container.removeChild(e);
    });


}

function updatePortraits(data) {
    var portraits = data.portraits;

    // First, remove all existing portraits
    removePortraitImages('fs');
    removePortraitImages('left');
    removePortraitImages('right');
    removePortraitImages('topleft');
    removePortraitImages('topright');
    removePortraitImages('bottomleft');
    removePortraitImages('bottomright');

    portraits.forEach(function(p) {
        var target = '';

        if (p.location === 'full') {
            target = 'fs';
        } else if (p.location === 'left') {
            target = 'left';
        } else if (p.location === 'right') {
            target = 'right';
        } else if (p.location === 'top_left') {
            target = 'topleft';
        } else if (p.location === 'top_right') {
            target = 'topright';
        } else if (p.location === 'bottom_left') {
            target = 'bottomleft';
        } else if (p.location === 'bottom_right') {
            target = 'bottomright';
        }

        if (target !== '') {
            Images.load(p.url, function(img) {
                document.getElementById(target).appendChild(img);
            });
        }
    });
}
