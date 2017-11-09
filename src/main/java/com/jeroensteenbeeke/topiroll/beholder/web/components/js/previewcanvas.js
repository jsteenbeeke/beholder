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

function previewRectangle(canvasId, color, alpha, rect) {
    var canvas = document.getElementById(canvasId);
    var ctx = canvas.getContext('2d');

    ctx.save();
    ctx.beginPath();
    applyRectangle(ctx, rect);
    ctx.globalAlpha = alpha;
    ctx.fillStyle = color;
    ctx.closePath();
    ctx.fill();
    ctx.restore();

}

function previewCircle(canvasId, color, alpha, circle) {
    var canvas = document.getElementById(canvasId);
    var ctx = canvas.getContext('2d');

    ctx.save();
    ctx.beginPath();
    applyCircle(ctx, circle);
    ctx.globalAlpha = alpha;
    ctx.fillStyle = color;
    ctx.closePath();
    ctx.fill();
    ctx.restore();
}

function previewPolygon(canvasId, color, alpha, poly) {
    var canvas = document.getElementById(canvasId);
    var ctx = canvas.getContext('2d');

    ctx.save();
    ctx.beginPath();
    applyPoly(ctx, poly);
    ctx.globalAlpha = alpha;
    ctx.fillStyle = color;
    ctx.closePath();
    ctx.fill();
    ctx.restore();
}

function determineBorderColor(type) {
    if ("Neutral" === type) {
        return "#ffff00";
    } else if ("Ally" === type) {
        return "#00ff00";
    } else if ("Enemy" === type) {
        return "#ff0000";
    }

    // Default to black border
    return "#000000";
}

function previewToken(canvasId, token) {
    var canvas = document.getElementById(canvasId);
    var context = canvas.getContext('2d');

    var src = token.src; // string
    var borderType = token.border_type; // enum
    var x = token.x; // int
    var y = token.y; // int
    var width = token.width; // int
    var height = token.height; // int

    var radius = (width + height) / 4;
    var ox = x + radius;
    var oy = y + radius;
    var color = determineBorderColor(borderType);

    var img = new Image();

    img.onload = function () {
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

    }
    img.src = src;
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
