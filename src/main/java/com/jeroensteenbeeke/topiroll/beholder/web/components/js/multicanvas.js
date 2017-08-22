const SEGMENT_SIZE = 500;


function MultiCanvas(containerId, requiredWidth, requiredHeight) {
    var totalWidth = requiredWidth + (requiredWidth % SEGMENT_SIZE);
    var totalHeight = requiredHeight + (requiredHeight % SEGMENT_SIZE);
    var numSegmentsX = totalWidth / SEGMENT_SIZE;
    var numSegmentsY = totalHeight / SEGMENT_SIZE;
    var canvases = [];
    var contexts = [];

    // Step 1: Create Canvas HTML
    var canvasHtml = '<!--';
    for (y = 0; y < numSegmentsY; y++) {

        canvases[y] = [];
        contexts[y] = [];
        for (x = 0; x < numSegmentsX; x++) {
            canvasHtml = canvasHtml + '--><canvas id="multicanvas-' + x + '-' + y + '" ' +
                'width="' + SEGMENT_SIZE + '" ' +
                'height="' + SEGMENT_SIZE + '" style="';

                canvasHtml = canvasHtml +
                    'display: inline-block;';
            canvasHtml = canvasHtml + ' width: '+ SEGMENT_SIZE+'px; height: '+ SEGMENT_SIZE+'px; padding: 0px; margin: 0px; border: 0px; background: transparent;"></canvas><!--';
        }

        // canvasHtml = canvasHtml + '--><br style="display: inline-block;"/><!--';
    }
    canvasHtml = canvasHtml + '-->';

    // Step 2: Replace indicated canvas with canvas grid
    var container = document.getElementById(containerId);
    container.style = 'line-height: 0; padding: 0; margin: 0; width: '+ totalWidth + 'px; height: '+ totalHeight +'px;';

    container.insertAdjacentHTML('beforeend', canvasHtml);

    // Step 3: Find all created canvases and add to array
    for (x = 0; x < numSegmentsX; x++) {
        for (y = 0; y < numSegmentsY; y++) {
            canvases[y][x] = document.getElementById('multicanvas-' + x + '-' + y);
            contexts[y][x] = canvases[y][x].getContext('2d');
            contexts[y][x].id = 'x'+ x +'/y' + y;
            contexts[y][x].canvasOffsetX = x * SEGMENT_SIZE;
            contexts[y][x].canvasOffsetY = y * SEGMENT_SIZE;

            canvases[y][x].width = SEGMENT_SIZE;
            canvases[y][x].height = SEGMENT_SIZE;
        }
    }

    this.canvases = canvases;
    this.drawContexts = contexts;
}

function MultiCanvasContext(canvases, contexts) {
    this.canvases = canvases;
    this.drawContexts = contexts;
}

MultiCanvasContext.prototype.forEachContext = function (name, operation) {
    this.drawContexts.forEach(function (row) {

        row.forEach(function(ctx) {
            console.log('Apply ' + name + ' to '+ ctx.id)
            operation(ctx);
        });
    });
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

MultiCanvasContext.prototype.arc = function(x, y, radius, startAngle, endAngle, antiClockWise) {
    this.forEachContext('arc', function(ctx) {
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

        ctx.drawImage(img, adjustedX, adjustedY, sWidth, sHeight, dx, dy, dWidth, dHeight);
    });
};

MultiCanvas.prototype.getContext = function (contextId) {
    if (contextId === '2d') {
        return new MultiCanvasContext(this.canvases, this.drawContexts);
    }

    return null;
};