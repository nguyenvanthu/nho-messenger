var BUBBLE_FLY_TIME_MS = 12000;

var btnBubbleEl = $('.bubble-chat');
var bubbleInput = $('.bubble-input');
var bubbleCounter = 0;

btnBubbleEl.on('click', function() {
  bubbleInput.css('display', 'inline-block');
  bubbleInput.focus();
});

bubbleInput.blur(function () {
  checkAndSendMyMessage();
  bubbleInput.css('display', 'none');
});

$(document).keypress(function(e) {
  if(e.which == 13) {
    checkAndSendMyMessage();
  }
});

function checkAndSendMyMessage() {
  if (bubbleInput.val()) {
    chatBubble(bubbleInput.val(), true /* isMe */);
    bubbleInput.val('');
  }
}

function chatBubble(message, isMe) {
  bubbleCounter++;
  var bubbleEl = $('<div>').addClass('bubble');
  bubbleEl.text(message);
  bubbleEl.addClass(isMe ? 'fly-up' : 'fly-down');
  $('.bubble-wrapper').append(bubbleEl);
  window.setTimeout(function() {
    bubbleEl.remove();
  }, BUBBLE_FLY_TIME_MS);

  if (isMe) {
    messageService.sendBubbleMessage(message, bubbleCounter);
  }
}