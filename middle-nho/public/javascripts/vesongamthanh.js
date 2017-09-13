var MIN_SOUND = 35;
var MAX_SOUND = 100;
var SOUND_SLIDE_TIME_MS = 12000;
var NEW_SOUND_INTERVAL_MS = 200;

var lastSound = 0;
var soundWrapperEl = $('.sound-wrapper');

window.setInterval(function() {
  if (lastSound && lastSound != MAX_SOUND) {
    themSong(lastSound);
  }
}, NEW_SOUND_INTERVAL_MS);

function veSongAmThanh(soundLevel) {
  var level = Math.min(Math.max(soundLevel - MIN_SOUND, 0), MAX_SOUND);
  lastSound = level;
}

function themSong(level) {
  var bitEl = $('<div>').addClass('sound-bit slide');
  bitEl.css('height', level / 4 + '%');
  soundWrapperEl.append(bitEl);
  window.setTimeout(function() {
    bitEl.remove();
  }, SOUND_SLIDE_TIME_MS);
}