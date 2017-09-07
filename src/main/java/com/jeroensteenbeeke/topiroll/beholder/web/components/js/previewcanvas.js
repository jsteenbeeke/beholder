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

function previewRectangle(canvasId, color, rect) {
    var canvas = document.getElementById(canvasId);
    var ctx = canvas.getContext('2d');

    ctx.save();
    ctx.fillStyle = color;
    applyRectangle(ctx, rect);
    ctx.fill();
    ctx.restore();

}

function previewCircle(canvasId, color, circle) {
    var canvas = document.getElementById(canvasId);
    var ctx = canvas.getContext('2d');

    ctx.save();
    applyCircle(ctx, circle);
    ctx.fillStyle = color;
    ctx.fill();
    ctx.restore();
}

function previewPolygon(canvasId, color, poly) {
    var canvas = document.getElementById(canvasId);
    var ctx = canvas.getContext('2d');

    ctx.save();
    applyPoly(ctx, poly);
    ctx.fillStyle = color;
    ctx.fill();
    ctx.restore();
}


function renderMapToCanvas(canvasId, src, targetWidth, onDrawn) {
    var canvas = document.getElementById(canvasId);

    var img = new Image();
    img.onload = function () {
        var ctx = canvas.getContext('2d');
        var w, h;

        if (typeof targetWidth === 'undefined') {
		    w = img.width;
		    h = img.height;
        } else {
            w = targetWidth;
            h = (targetWidth / img.width) * img.height;
        }

        canvas.width = w;
        canvas.height = h;

        ctx.drawImage(img, 0, 0, w, h);

        onDrawn();
    };

    img.src = src;
}
