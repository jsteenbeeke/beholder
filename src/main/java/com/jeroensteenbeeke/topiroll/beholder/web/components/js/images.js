/*
 * This file is part of Beholder
 * Copyright (C) 2016 - 2023 Jeroen Steenbeeke
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
function ImageHandler() {
    this.images = {};
}

ImageHandler.prototype.load = function(src, onLoaded) {
    var handler = this;
    var img;

    if (src in handler.images) {
        img = handler.images[src];

        onLoaded(img);

        return img;
    } else {
        img = new Image();
        img.onload = function() {
            handler.images[src] = img;
            onLoaded(img);
        };
        img.src = src;
        return img;
    }
};

var Images = new ImageHandler();
