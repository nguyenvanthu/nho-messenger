/**
 * Service to draw sequence of bundled points naturally.
 * @param {Function} onDraw
 * @constructor
 */
DrawSequence = function(onDraw) {

  /**
   * Lists of points to be drawn.
   */
  this.pointsToDraw_ = [];

  /**
   * Drawing function.
   */
  this.onDraw_ = onDraw;

  setInterval(
      this.onTick.bind(this),
      DrawSequence.DRAW_INTERVAL_MS);
};

/**
 * Drawing frequency.
 */
DrawSequence.DRAW_INTERVAL_MS = 15; 

/**
 * Drawing fast threshold.
 */
DrawSequence.DRAW_FAST_THRESHOLD = 100; 

/**
 * Drawing super fast threshold.
 */
DrawSequence.DRAW_SUPER_FAST_THRESHOLD = 200; 

/**
 * Executes a new list of points.
 * @param {Array} points
 */
DrawSequence.prototype.execute = function(points) {
  this.pointsToDraw_ = this.pointsToDraw_.concat(points);
}

/**
 * Drawing tick.
 */
DrawSequence.prototype.onTick = function() {
  var numPoints = this.pointsToDraw_.length;
  if (!numPoints) {
    return;
  }

  this.onDraw_(this.pointsToDraw_.shift());
  if (numPoints > 1 && numPoints > DrawSequence.DRAW_FAST_THRESHOLD) {
    this.onDraw_(this.pointsToDraw_.shift());
  }
  if (numPoints > 3 && numPoints > DrawSequence.DRAW_SUPER_FAST_THRESHOLD) {
    this.onDraw_(this.pointsToDraw_.shift());
    this.onDraw_(this.pointsToDraw_.shift());
  }
}
