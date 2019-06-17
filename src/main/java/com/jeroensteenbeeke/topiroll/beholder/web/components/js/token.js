/*
 * This file is part of Beholder
 * (C) 2016-2019 Jeroen Steenbeeke
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

function determineFontSize(context, width, height, label) {
    var valuesToTry = [108, 100, 96, 88, 84, 72, 66, 60, 56, 54, 48, 44, 42, 40, 36, 32, 30, 28,
        26, 24, 22, 20, 18, 16, 15, 14, 12, 11, 10, 9, 8, 7, 6, 5, 4];

    var found = {
        fontSize: 4,
        characterHeight: 6
    };
    var chars = label.split('');

    for (idx = 0; idx < valuesToTry.length; idx++) {
        var value = valuesToTry[idx];
        context.setFont(value + 'pt Arial');
        var measure = context.measureText(label);

        var cwidth = chars.map(function(c) {
           return Math.round(context.measureText(c).width);
        }).reduce(function(a,b) {
            return Math.max(a,b);
        });
        var textHeight = Math.round(cwidth / 0.78);

        if (measure.width < width && textHeight < height) {
            found = {
                fontSize: value,
                characterHeight: textHeight
            };
            break;
        }

    }

    return found;

}

function drawText(context, token) {
    var label = token.label; // string
    var x = token.x; // int
    var y = token.y; // int
    var width = token.width; // int
    var height = token.height; // int
    var color = "#000000;"

    var box_top_y = y + 5 * (height / 6);

    var box_height = height / 6;

    var box_width = width;

    var box_left_x = x;

    context.save();
    context.setLineWidth(1);
    context.setStrokeStyle(color);
    context.setFillStyle('#ffffff');
    context.moveTo(box_left_x, box_top_y);
    context.fillRect(box_left_x, box_top_y, box_width, box_height);
    context.strokeRect(box_left_x, box_top_y, box_width, box_height);
    context.restore();

    context.save();

    var fontSize = determineFontSize(context, box_width, box_height, label);
    context.setFont(fontSize.fontSize + 'pt Arial');

    var text_width = context.measureText(label).width;
    var text_x = box_left_x + (box_width - text_width)/2;
    var text_y = box_top_y + (box_height - fontSize.characterHeight) / 2;

    context.setTextBaseline("top");
    context.setFillStyle(color);
    context.fillText(label, text_x, text_y);

    context.restore();
}

function determineHealthAngle(intensity) {
    if ("HEALTHY" === intensity) {
        return Math.PI * 2;
    } else if ("MINOR_INJURIES" === intensity) {
        return Math.PI * 1.5;
    } else if ("MODERATELY_INJURED" === intensity) {
        return Math.PI;
    } else if ("HEAVILY_INJURED" === intensity) {
        return Math.PI * 0.5;
    } else if ("DEAD" === intensity) {
        return 0;
    }

    // If unknown assume healthy
    return Math.PI * 2;
}

function renderToken(context, token) {
    return new Promise(resolve => {
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
        var color = determineBorderColor(borderType);

        Images.load(src, function (img) {
            // Step 1: Draw image (with circle clip path)
            context.save();
            context.beginPath();
            context.arc(ox, oy, radius, 0, 2 * Math.PI);
            context.closePath();
            context.clip();
            context.drawImage(img, x, y, width, height);
            context.restore();

            // Step 2: Draw base border (black)
            context.save();
            context.beginPath();
            context.arc(ox, oy, radius, 0, 2 * Math.PI);
            context.setLineWidth(radius / 7);
            context.setStrokeStyle('#000000');
            context.stroke();
            context.restore();

            // Step 3: Draw health border (user-determined)
            var healthAngle = determineHealthAngle(borderIntensity);

            context.save();
            context.beginPath();
            context.arc(ox, oy, radius, 0, healthAngle);
            context.setLineWidth(radius / 8);
            context.setStrokeStyle(color);
            context.stroke();
            context.restore();

            // Step 4: Draw text
            if (label) {
                drawText(context, token);
            }
            
            // Step 5: Draw status effect icon (if present)
            if (token.status_effect) {
            	const statusEffectUrl = '../img/statuseffects/' + token.status_effect + '.png';
            
    	        Images.load(statusEffectUrl, function(statusImg){
    	        	const statusIconOffsetX = width / 4;
    	            const statusIconOffsetY = height / 4;
    	        	context.save();
    	            context.drawImage(statusImg, x - statusIconOffsetX, y - statusIconOffsetY, width * 0.9, height * 0.9);
    	            context.restore();
    	            resolve();
    	        });
            } else {
            	resolve();
            }
        });
    });
}
