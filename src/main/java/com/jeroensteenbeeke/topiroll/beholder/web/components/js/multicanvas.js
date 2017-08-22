const SEGMENT_SIZE = 500;


function MultiCanvas(id, requiredWidth, requiredHeight) {
    var totalWidth = requiredWidth + (requiredWidth % SEGMENT_SIZE);
    var totalHeight = requiredHeight + (requiredHeight % SEGMENT_SIZE);
    var numSegmentsX = totalWidth / SEGMENT_SIZE;
    var numSegmentsY = totalHeight / SEGMENT_SIZE;
    var canvases = [];
    var contexts = [];

    // Step 1: Create Canvas HTML
    var canvasHtml = '';
    for (y = 0; y < numSegmentsY; y++) {

        canvases[y] = [];
        contexts[y] = [];
        for (x = 0; x < numSegmentsX; x++) {
            canvasHtml = canvasHtml + '\t<canvas id="multicanvas-' + x + '-' + y + '" width="' + SEGMENT_SIZE + '" height="' + SEGMENT_SIZE + '" style="float: left; white-space: nowrap"></canvas>\n';
        }

        canvasHtml = canvasHtml + '<br />';
    }

    // Step 2: Replace indicated canvas with canvas grid
    var oldCanvas = document.getElementById(id);
    var canvasParent = oldCanvas.parentNode;
    canvasParent.removeChild(oldCanvas);

    canvasParent.insertAdjacentHTML('beforeend', canvasHtml);

    // Step 3: Find all created canvases and add to array
    for (x = 0; x < numSegmentsX; x++) {
        for (y = 0; y < numSegmentsY; y++) {
            canvases[y][x] = document.getElementById('multicanvas-' + x + '-' + y);
            contexts[y][x] = canvases[y][x].getContext('2d');
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

MultiCanvasContext.prototype.forEachContext = function (operation) {
    this.drawContexts.forEach(function (row) {
        row.forEach(operation);
    });
};

MultiCanvasContext.prototype.setLineWidth = function (width) {
    this.forEachContext(function (context) {
        context.lineWidth = width;
    });
};

MultiCanvasContext.prototype.setStrokeStyle = function (stroke) {
    this.forEachContext(function (context) {
        context.strokeStyle = stroke;
    });
};

MultiCanvasContext.prototype.moveTo = function (x, y) {
    this.forEachContext(function (context) {
        var adjustedX = x - context.canvasOffsetX;
        var adjustedY = y - context.canvasOffsetY;
        context.moveTo(adjustedX, adjustedY);
    });
};

MultiCanvasContext.prototype.rect = function (x, y, width, height) {
    this.forEachContext(function (context) {
        var adjustedX = x - context.canvasOffsetX;
        var adjustedY = y - context.canvasOffsetY;
        context.rect(adjustedX, adjustedY, width, height);
    });
};

MultiCanvasContext.prototype.stroke = function () {
    this.forEachContext(function (context) {
        context.stroke();
    });
}

MultiCanvas.prototype.getContext = function (contextId) {
    if (contextId === '2d') {
        return new MultiCanvasContext(this.canvases, this.drawContexts);
    }

    return null;
};