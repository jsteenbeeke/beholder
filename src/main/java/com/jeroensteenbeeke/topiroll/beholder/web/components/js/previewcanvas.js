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

function previewRectangle(canvas, color, alpha, rect) {
    var ctx = canvas.getContext('2d');

    ctx.save();
    ctx.beginPath();
    applyRectangle(ctx, rect);
    ctx.setGlobalAlpha(alpha);
    ctx.setFillStyle(color);
    ctx.closePath();
    ctx.fill();
    ctx.restore();

    canvas.switchBuffer();

    ctx = canvas.getContext('2d');

    ctx.save();
    ctx.beginPath();
    applyRectangle(ctx, rect);
    ctx.setGlobalAlpha(alpha);
    ctx.setFillStyle(color);
    ctx.closePath();
    ctx.fill();
    ctx.restore();
}

function previewCircle(canvas, color, alpha, circle) {
    var ctx = canvas.getContext('2d');

    ctx.save();
    ctx.beginPath();
    applyCircle(ctx, circle);
    ctx.setGlobalAlpha(alpha);
    ctx.setFillStyle(color);
    ctx.closePath();
    ctx.fill();
    ctx.restore();

    canvas.switchBuffer();

    ctx = canvas.getContext('2d');

    ctx.save();
    ctx.beginPath();
    applyCircle(ctx, circle);
    ctx.setGlobalAlpha(alpha);
    ctx.setFillStyle(color);
    ctx.closePath();
    ctx.fill();
    ctx.restore();
}

function previewPolygon(canvas, color, alpha, poly) {
    var ctx = canvas.getContext('2d');

    ctx.save();
    ctx.beginPath();
    applyPoly(ctx, poly);
    ctx.setGlobalAlpha(alpha);
    ctx.setFillStyle(color);
    ctx.closePath();
    ctx.fill();
    ctx.restore();

    canvas.switchBuffer();

    ctx = canvas.getContext('2d');

    ctx.save();
    ctx.beginPath();
    applyPoly(ctx, poly);
    ctx.setGlobalAlpha(alpha);
    ctx.setFillStyle(color);
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

function previewToken(canvas, token) {
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

    Images.load(src, function(img) {
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

        canvas.switchBuffer();

        context = canvas.getContext('2d');

        // Step 3: Draw image (with circle clip path)
        context.save();
        context.beginPath();
        context.arc(ox, oy, radius, 0, 2 * Math.PI);
        context.closePath();
        context.clip();
        context.drawImage(img, x, y, width, height);
        context.restore();

        // Step 4: Draw border
        context.save();
        context.beginPath();
        context.arc(ox, oy, radius, 0, 2 * Math.PI);
        context.closePath();
        context.setLineWidth(radius / 7);
        context.setStrokeStyle(color);
        context.stroke();
        context.restore();
    });

}

function renderMapToCanvas(canvas, src, targetWidth, onDrawn) {
    Images.load(src, function(img) {

        var ctx = canvas.getContext('2d');
        var w, h;

        if (typeof targetWidth === 'undefined') {
            w = img.width;
            h = img.height;
        } else {
            w = targetWidth;
            h = (targetWidth / img.width) * img.height;
        }

        ctx.drawImage(img, 0, 0, w, h);

        canvas.switchBuffer();

        onDrawn();
    });
}
