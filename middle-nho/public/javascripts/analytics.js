
(function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
(i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
})(window,document,'script','https://www.google-analytics.com/analytics.js','ga');

ga('create', 'UA-77472444-4', 'auto');
ga('send', 'pageview');

var SCREEN_CHAT = "ChatWithHuman";

var CATEGORY_DRAWING = "Drawing";
var CATEGORY_MISC = "Misc";
var CATEGORY_VEGIDUOCNAY = "LiveObjectDrawing";
var CATEGORY_STATUS = "Status";

var ACTION_POKE = "Poke";
var ACTION_DRAW_POINTS = "Draw Points";
var ACTION_DRAW_DURATION = "Draw Duration";
var ACTION_IDLE = "Idle";
var ACTION_STROKES = "Number of Strokes";
var ACTION_CREATE_OBJECT = "Create object";
var ACTION_MOVE_OBJECT = "Move object";
var ACTION_DELETE_OBJECT = "Delete object";
var ACTION_CLEAR_SCREEN = "Clear screen";
var ACTION_SWITCH_STICKER = "Switch sticker";
var ACTION_TRIGGER_STICKER = "Trigger sticker";
var ACTION_TRIGGER_GAME = "Trigger game";
var ACTION_OPEN_FEEDBACK = "Open feedback";
var ACTION_SEND_FEEDBACK = "Send feedback";

// New actions compared to Android.
var ACTION_OTHER_GOES_ONLINE = "Other goes online";
var ACTION_OTHER_GOES_OFFLINE = "Other goes offline";
var ACTION_OTHER_STARTED_WITH_ONLINE = "Other started with online";
var ACTION_OTHER_STARTED_WITH_OFFLINE = "Other started with offline";
var ACTION_OTHER_STARTED_WITH_UNAVAILABLE = "Other started with unavailable";
var ACTION_IS_READY = "Client is ready";
var ACTION_WRONG_LINK = "Wrong link";
var ACTION_RECEIVED_SOUND_WAVE = "Received sound wave";
var ACTION_BUBBLE_CHAT = "Bubble chat";

var didTrackSound = false;


function trackEvent(category, action, opt_isMe) {
  ga('send', {
    hitType: 'event',
    eventCategory: category,
    eventAction: action,
    eventValue: opt_isMe ? 1 : 0
  });
}

function trackDraw(numPoints) {
  // Note that drawing doesn't distinguish me and the other.
  ga('send', {
    hitType: 'event',
    eventCategory: CATEGORY_DRAWING,
    eventAction: ACTION_DRAW_POINTS,
    eventValue: numPoints
  });
}

function trackChatInitiation() {
  ga('send', {
    hitType: 'pageview',
    page: SCREEN_CHAT
  });
}

function trackChatStatus(status) {
  ga('send', {
    hitType: 'event',
    eventCategory: CATEGORY_STATUS,
    eventAction: status
  });
}

function trackSoundWave() {
  if (didTrackSound) {
    return;
  }
  didTrackSound = true;
  ga('send', {
    hitType: 'event',
    eventCategory: CATEGORY_MISC,
    eventAction: ACTION_RECEIVED_SOUND_WAVE
  });
}

function trackError(errMessage) {
  ga('send', 'exception', {
    'exDescription': errMessage,
    'exFatal': false
  });
}