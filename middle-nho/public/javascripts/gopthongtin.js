/**
 * Service to bundle messages before sending out.
 * @param {MessageService} messageService
 * @constructor
 */
DataBundler = function(messageService) {
  /**
   * Lists of messages which has been bundled.
   */
  this.bundled_ = [];

  /**
   * Message service.
   */
  this.messageService_ = messageService;

  window.setInterval(
      this.onTick_.bind(this),
      DataBundler.SEND_INTERVAL_MS);
};

/**
 * Sending frequency.
 */
DataBundler.SEND_INTERVAL_MS = 500; 
 

/**
 * Bundle a new message into a buffer to send later.
 * @param {Object} message
 */
DataBundler.prototype.bundle = function(message) {
  this.bundled_.push(message);
}

/**
 * Drawing tick.
 */
DataBundler.prototype.onTick_ = function() {
  if (!this.bundled_.length) {
    return;
  }

  var result;
  while (this.bundled_.length) {
    var item = this.bundled_.shift();
    if (!result) {
      result = item;
      continue;
    }
    result[5].value = result[5].value.concat(item[5].value);
    if (item[5].value.action === ACTION_UP) {
      this.sendBundledMessage_(result);
      result = null;
    }
  }

  this.sendBundledMessage_(result);
}

/**
 * Send a bundled message;
 */
DataBundler.prototype.sendBundledMessage_ = function(bundledMessage) {
  trackDraw(bundledMessage[5].value.length);
  bundledMessage[5].value = JSON.stringify(bundledMessage[5].value);
  this.messageService_.sendMessage(bundledMessage);
};