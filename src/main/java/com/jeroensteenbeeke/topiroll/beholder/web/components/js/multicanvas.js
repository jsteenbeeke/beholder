const SEGMENT_SIZE = 500;
const CANVAS_RED = "red";
const CANVAS_BLACK = "black";
const CANVAS_TYPES = [ CANVAS_BLACK, CANVAS_RED ];

function MultiCanvas(containerId, requiredWidth, requiredHeight) {
    var container = document.getElementById(containerId);
    var bounds = container.getBoundingClientRect();
    var offsetX = bounds.left + window.pageXOffset;
    var offsetY = bounds.top + window.pageYOffset;

    var totalWidth = Math.ceil(requiredWidth / SEGMENT_SIZE) * SEGMENT_SIZE;
    var totalHeight = Math.ceil(requiredHeight / SEGMENT_SIZE) * SEGMENT_SIZE;
    var numSegmentsX = totalWidth / SEGMENT_SIZE;
    var numSegmentsY = totalHeight / SEGMENT_SIZE;
    var canvases = [];
    var contexts = [];
    var x, y, t;

    // Step 1: Create Canvas HTML
    var canvasHtml = '<!--';
    for (y = 0; y < numSegmentsY; y++) {

        canvases[y] = [];
        contexts[y] = [];
        for (x = 0; x < numSegmentsX; x++) {
            canvasHtml = canvasHtml + '--><canvas id="multicanvas-red-'+ containerId +'-' + x + '-' + y + '" ' +
                'width="' + Math.min(totalWidth, SEGMENT_SIZE) + '" ' +
                'height="' + Math.min(totalHeight, SEGMENT_SIZE) + '" style="';

            canvasHtml = canvasHtml + this.getCanvasCSS(0,
                offsetX + x * SEGMENT_SIZE,
                offsetY + y * SEGMENT_SIZE, totalWidth, totalHeight) + '"></canvas><!--';

            canvasHtml = canvasHtml + '--><canvas id="multicanvas-black-'+ containerId +'-' + x + '-' + y + '" ' +
                'width="' + Math.min(totalWidth, SEGMENT_SIZE) + '" ' +
                'height="' + Math.min(totalHeight, SEGMENT_SIZE) + '" style="';

            canvasHtml = canvasHtml + this.getCanvasCSS(-1, offsetX + x * SEGMENT_SIZE,
                offsetY + y * SEGMENT_SIZE, totalWidth, totalHeight) + '"></canvas><!--';

            canvases[y][x] = [];
            contexts[y][x] = [];
        }

        // canvasHtml = canvasHtml + '--><br style="display: inline-block;"/><!--';
    }
    canvasHtml = canvasHtml + '-->';

    // Step 2: Replace indicated canvas with canvas grid
    container.style = 'line-height: 0; padding: 0; margin: 0; width: ' + totalWidth + 'px; height: ' + totalHeight + 'px;';

    container.insertAdjacentHTML('beforeend', canvasHtml);

    // Step 3: Find all created canvases and add to array
    for (x = 0; x < numSegmentsX; x++) {
        for (y = 0; y < numSegmentsY; y++) {
            for (t = 0; t < CANVAS_TYPES.length; t++) {
                let z = CANVAS_TYPES[t];

                canvases[y][x][z] = document.getElementById('multicanvas-' + z + '-' + containerId + '-' + x + '-' + y);
                contexts[y][x][z] = canvases[y][x][z].getContext('2d');
                contexts[y][x][z].id = 'x' + x + '/y' + y + '/' + z;
                contexts[y][x][z].canvasOffsetX = x * SEGMENT_SIZE;
                contexts[y][x][z].canvasOffsetY = y * SEGMENT_SIZE;

                canvases[y][x][z].width = Math.min(totalWidth, SEGMENT_SIZE);
                canvases[y][x][z].height = Math.min(totalHeight, SEGMENT_SIZE);
            }
        }
    }

    this.canvases = canvases;
    this.drawContexts = contexts;

    this.width = totalWidth;
    this.height = totalHeight;

    this.selectedBuffer = CANVAS_BLACK;

    if (typeof renderListeners === 'object' || typeof renderListeners === 'array') {
        renderListeners.forEach(function(listener) {
            listener();
        });
    }
}

function MultiCanvasContext(canvases, contexts, canvas) {
    this.canvases = canvases;
    this.drawContexts = contexts;
    this.parentCanvas = canvas;
}

MultiCanvas.prototype.recalculateOffset = function(offsetX, offsetY) {
    var numSegmentsX = this.width / SEGMENT_SIZE;
    var numSegmentsY = this.height / SEGMENT_SIZE;

    var selected = this.selectedBuffer;

    for (x = 0; x < numSegmentsX; x++) {
        for (y = 0; y < numSegmentsY; y++) {
            for (t = 0; t < CANVAS_TYPES.length; t++) {
                let z = CANVAS_TYPES[t];
                let index = z === selected ? 0 : -1;

                let segmentX = SEGMENT_SIZE * x;
                let segmentY = SEGMENT_SIZE * y;

                this.canvases[y][x][z].style = this.getCanvasCSS(index, segmentX + offsetY, segmentY + offsetY, this.width, this.height);
            }
        }
    }
}

MultiCanvas.prototype.getCanvasCSS = function(zIndex, offsetX, offsetY, totalWidth, totalHeight) {
    return 'display: inline-block; ' +
        'width: ' + Math.min(totalWidth, SEGMENT_SIZE) + 'px; ' +
        'height: ' + Math.min(totalHeight, SEGMENT_SIZE) + 'px; ' +
        'padding: 0px; ' +
        'margin: 0px; ' +
        'border: 0px; ' +
        'background: transparent; ' +
        'position: absolute;' +
        'left: '+ offsetX + 'px;' +
        'top: ' + offsetY + 'px;' +
        'z-index: ' + zIndex + ';';
};

MultiCanvasContext.prototype.forEachContext = function (name, operation) {
    var self = this;

    this.drawContexts.forEach(row => {
        row.forEach(ctx => {
            operation(ctx[self.parentCanvas.selectedBuffer]);
        });
    });
};

MultiCanvas.prototype.switchBuffer = function() {
    var self = this;
    var notSelected;

    if (this.selectedBuffer === CANVAS_BLACK) {
        notSelected = CANVAS_RED;
    } else {
        notSelected = CANVAS_BLACK;
    }

    this.canvases.forEach(row => {
        row.forEach(redAndBlack => {
            let offsetX = redAndBlack[notSelected].offsetLeft;
            let offsetY = redAndBlack[notSelected].offsetTop;

            // 1. Set z-index of non-selected to -2
           redAndBlack[notSelected].style = self.getCanvasCSS(-2, offsetX, offsetY, self.width, self.height);
            // 2. Set z-index of selected to 0
            redAndBlack[self.selectedBuffer].style = self.getCanvasCSS(0, offsetX, offsetY, self.width, self.height);
            // 3. Set z-index of non-selected to -1
            redAndBlack[notSelected].style = self.getCanvasCSS(-1, offsetX, offsetY, self.width, self.height);
        });
    });
    // 4. Set selected to non-selected
   this.selectedBuffer = notSelected;

};

MultiCanvasContext.prototype.measureText = function (text) {
    return this.drawContexts[0][0][this.parentCanvas.selectedBuffer].measureText(text);
};

MultiCanvasContext.prototype.setLineWidth = function (width) {
    this.forEachContext('setLineWidth', function (ctx) {
        ctx.lineWidth = width;
    });
};

MultiCanvasContext.prototype.setStrokeStyle = function (stroke) {
    this.forEachContext('setStrokeStyle', function (ctx) {
        ctx.strokeStyle = stroke;
    });
};

MultiCanvasContext.prototype.setFillStyle = function (fill) {
    this.forEachContext('setFillStyle', function (ctx) {
        ctx.fillStyle = fill;
    });
};

MultiCanvasContext.prototype.setFont = function (font) {
    this.forEachContext('setFont', function (ctx) {
        ctx.font = font;
    });
};

MultiCanvasContext.prototype.setGlobalAlpha = function (alpha) {
    this.forEachContext('setGlobalAlpha', function (ctx) {
        ctx.globalAlpha = alpha;
    });
};

MultiCanvasContext.prototype.moveTo = function (x, y) {
    this.forEachContext('moveTo', function (ctx) {
        var adjustedX = x - ctx.canvasOffsetX;
        var adjustedY = y - ctx.canvasOffsetY;
        ctx.moveTo(adjustedX, adjustedY);
    });
};

MultiCanvasContext.prototype.lineTo = function (x, y) {
    this.forEachContext('lineTo', function (ctx) {
        var adjustedX = x - ctx.canvasOffsetX;
        var adjustedY = y - ctx.canvasOffsetY;
        ctx.lineTo(adjustedX, adjustedY);
    });
};

MultiCanvasContext.prototype.rect = function (x, y, width, height) {
    this.forEachContext('rect', function (ctx) {
        var adjustedX = x - ctx.canvasOffsetX;
        var adjustedY = y - ctx.canvasOffsetY;
        ctx.rect(adjustedX, adjustedY, width, height);
    });
};

MultiCanvasContext.prototype.clearRect = function (x, y, width, height) {
    this.forEachContext('clearRect', function (ctx) {
        var adjustedX = x - ctx.canvasOffsetX;
        var adjustedY = y - ctx.canvasOffsetY;
        ctx.clearRect(adjustedX, adjustedY, width, height);
    });
};

MultiCanvasContext.prototype.fillRect = function (x, y, width, height) {
    this.forEachContext('fillRect', function (ctx) {
        var adjustedX = x - ctx.canvasOffsetX;
        var adjustedY = y - ctx.canvasOffsetY;
        ctx.fillRect(adjustedX, adjustedY, width, height);
    });
};

MultiCanvasContext.prototype.strokeRect = function (x, y, width, height) {
    this.forEachContext('strokeRect', function (ctx) {
        var adjustedX = x - ctx.canvasOffsetX;
        var adjustedY = y - ctx.canvasOffsetY;
        ctx.strokeRect(adjustedX, adjustedY, width, height);
    });
};

MultiCanvasContext.prototype.arc = function (x, y, radius, startAngle, endAngle, antiClockWise) {
    this.forEachContext('arc', function (ctx) {
        var adjustedX = x - ctx.canvasOffsetX;
        var adjustedY = y - ctx.canvasOffsetY;
        ctx.arc(adjustedX, adjustedY, radius, startAngle, endAngle, antiClockWise);
    });
};

MultiCanvasContext.prototype.fillText = function (text, x, y, maxWidth) {
    this.forEachContext('fillText', function (ctx) {
        var adjustedX = x - ctx.canvasOffsetX;
        var adjustedY = y - ctx.canvasOffsetY;
        ctx.fillText(text, adjustedX, adjustedY, maxWidth)
    });
};

MultiCanvasContext.prototype.clip = function () {
    this.forEachContext('clip', function (ctx) {
        ctx.clip();
    });
};

MultiCanvasContext.prototype.fill = function () {
    this.forEachContext('fill', function (ctx) {
        ctx.fill();
    });
};

MultiCanvasContext.prototype.strokeText = function (text, x, y, maxWidth) {
    this.forEachContext('strokeText', function (ctx) {
        var adjustedX = x - ctx.canvasOffsetX;
        var adjustedY = y - ctx.canvasOffsetY;
        ctx.strokeText(text, adjustedX, adjustedY, maxWidth)
    });
};

MultiCanvasContext.prototype.setTextBaseline = function(baseline) {
    this.forEachContext("setTextBaseline", function(ctx) {
        ctx.textBaseline = baseline;
    });
};


MultiCanvasContext.prototype.stroke = function () {
    this.forEachContext('stroke', function (ctx) {
        ctx.stroke();
    });
};

MultiCanvasContext.prototype.save = function () {
    this.forEachContext('save', function (ctx) {
        ctx.save();
    });
};

MultiCanvasContext.prototype.restore = function () {
    this.forEachContext('restore', function (ctx) {
        ctx.restore();
    });
};

MultiCanvasContext.prototype.beginPath = function () {
    this.forEachContext('beginPath', function (ctx) {
        ctx.beginPath();
    });
};

MultiCanvasContext.prototype.closePath = function () {
    this.forEachContext('closePath', function (ctx) {
        ctx.closePath();
    });
};

MultiCanvasContext.prototype.drawImage = function (img, x, y, sWidth, sHeight, dx, dy, dWidth, dHeight) {
    this.forEachContext('drawImage', function (ctx) {
            var adjustedX = x - ctx.canvasOffsetX;
            var adjustedY = y - ctx.canvasOffsetY;

            if (typeof sWidth === 'undefined' || typeof sHeight === 'undefined') {
                ctx.drawImage(img, adjustedX, adjustedY);
            } else if (typeof dx === 'undefined' || typeof dy === 'undefined' || typeof dWidth === 'undefined' || typeof dHeight === 'undefined') {
                ctx.drawImage(img, adjustedX, adjustedY, sWidth, sHeight);
            } else {
                ctx.drawImage(img, adjustedX, adjustedY, sWidth, sHeight, dx, dy, dWidth, dHeight);
            }
    });
};

MultiCanvas.prototype.getContext = function (contextId) {
    if (contextId === '2d') {
        return new MultiCanvasContext(this.canvases, this.drawContexts, this);
    }

    return null;
};
