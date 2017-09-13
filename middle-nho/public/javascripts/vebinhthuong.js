var REDRAW_REFRESH_MS = 50;
var FADE_RATE_MS = 1 / (5000 /* ms */ / REDRAW_REFRESH_MS);
var LIVE_OBJECT_RATE_MS = 1 / (2000 /* ms */ / REDRAW_REFRESH_MS);
var MIN_DIST_TO_DRAW_PX = 20;

var ACTION_DOWN = 0;
var ACTION_UP = 1;
var ACTION_MOVE = 2;

var AVOID_REMOVED_PATH = false;

var MY_COLOR = 'rgba(255, 36, 140, ';
var MY_COLOR_DONG_LAI = 'rgb(255, 146, 198)';
var THEIR_COLOR = 'rgba(134, 74, 255, ';

var canvasEl = $('#canvas');
var canvasWidth = canvasEl.parent().width();
var canvasHeight = canvasEl.parent().height();
var canvasTop = canvasEl.offset().top;
var canvasLeft = canvasEl.offset().left;
var ctx = canvasEl[0].getContext('2d');
ctx.canvas.width = canvasWidth;
ctx.canvas.height = canvasHeight;
ctx.lineWidth = 6;
ctx.lineCap = "round";

var buttonShakeEl = $('.shake');
var avatarEl = $('.avatar');
var errorWrapperEl = $('.error-wrapper');
var errorMessageEl = $('.error-message');
var pokeWrapperEl = $('.poke-wrapper');

var isDrawing = false;
var lastDrawnEv;
/** Stores all paths.
    @const {Array<Array>} */
var paths = [];
/** Last path. */
var lastPath;
var theirPath;
var drawTheirPathTimer;
var messageService;
var dataBundler;
var theirLastMessage;
var isShowingMessage = false;

function main() {
  messageService = new MessageService();
  messageService.subscribeNormalDraw(onNormalDraw.bind(this));

  dataBundler = new DataBundler(messageService);

  window.setInterval(function() {
    redraw();
    updatePathsOpacity();
  }, REDRAW_REFRESH_MS);

  canvasEl.on('mousedown touchstart', function(ev) {
    isDrawing = true;
    lastDrawnEv = ev;

    if (dangBatVeDongLai()) {
      lastPath = {points: [], alpha: 0.5, isDrawing: isDrawing, isMe: true, isLiveObj: true};
    } else {
      lastPath = {points: [], alpha: 1, isDrawing: isDrawing, isMe: true, isLiveObj: false};
    }
    paths.push(lastPath);
    addPointAt(lastPath, ev);
    dataBundler.bundle(createDrawMessage(ACTION_DOWN, ev));
  });

  canvasEl.on('mousemove touchmove', function(ev) {
    if (isDrawing && !isTooClose(lastDrawnEv, ev)) {
      lastDrawnEv = ev;
      addPointAt(lastPath, ev);
      redrawPath(lastPath);
      dataBundler.bundle(createDrawMessage(ACTION_MOVE, ev));
    }
    ev.preventDefault();
  });

  canvasEl.on('mouseup touchend', function(ev) {
    if (isDrawing) {
      isDrawing = false;
      lastPath.isDrawing = false;
      if (lastPath.points.length == 1) {
        lastPath.points.push(lastPath.points[0]);
      }
      dataBundler.bundle(createDrawMessage(ACTION_UP, ev));
    }
  });

  buttonShakeEl.on('click', function(ev) {
    poke();
  });

  pokeWrapperEl.on('click', function(ev) {
    poke();
  });
}

function onNormalDraw(message) {
  if (AVOID_REMOVED_PATH && deletedLiveObjIds && message.uuid &&
      deletedLiveObjIds[message.uuid]) {
    return;
  }
  switch (message.action) {
    case ACTION_DOWN:
      if (theirLastMessage) {
        finishTheirPath(theirLastMessage)
      }
      startTheirPath(message);
      break;
    case ACTION_MOVE:
      addToTheirPath(message);
      break;
    case ACTION_UP:
      finishTheirPath(message);
      theirLastMessage = null;
      break;
    default:
      // Do nothing.
      break;
  }
  theirLastMessage = message;
}

function createDrawMessage(action, ev) {
  return [
    MessageType.CHAT,
    0 /* messageId */,
    messageService.userBId /* from */,
    messageService.channelId /* to */,
    (new Date()).getTime() * 1000 /* sent time */,
    {
      value : [{
        action: action,
        index: lastPath.points.length,
        x: getX(ev) / canvasWidth,
        y: getY(ev) / canvasHeight
      }]
    },
    0 /* stickerType */,
    null /* error */
  ];
}

function startTheirPath(message) {
  theirPath = {
    points: [convertDrawMessageToPoint(message)],
    alpha: 1,
    isDrawing: true,
    isLiveObj: !!message.uuid,
    liveObjId: message.uuid,
    isMe: false};
  paths.push(theirPath);
}

function addToTheirPath(message) {
  theirPath.points.push(convertDrawMessageToPoint(message));
}

function finishTheirPath(message) {
  theirPath.points.push(convertDrawMessageToPoint(message));
  redrawPath(theirPath, true /* opt_forceDraw */)
  theirPath.isDrawing = false;
}

function addPointAt(path, mouseEv) {
  var mouseX = mouseEv.clientX ? mouseEv.clientX : mouseEv.touches[0].clientX;
  var mouseY = mouseEv.clientY ? mouseEv.clientY : mouseEv.touches[0].clientY;
  var newPoint = {
      x: mouseX - canvasLeft,
      y: mouseY - canvasTop};
  path.points.push(newPoint);
}

function redraw() {
  ctx.clearRect(0, 0, canvasWidth, canvasHeight);
  if (!paths.length) {
    return;
  }
  for (var i = 0; i < paths.length; i++) {
    redrawPath(paths[i]);
  }
}

function redrawPath(path, opt_forceDraw) {
  if (!path.points.length) {
    return;
  }
  if (opt_forceDraw || !path.path2d || path.isDrawing || path.isPathStale) {
    var drawing = new Path2D();
    drawing.moveTo(path.points[0].x, path.points[0].y);
    for (var i = 1; i < path.points.length; i++) {
      if (i < path.points.length - 1) {
        drawing.quadraticCurveTo(path.points[i - 1].x,
            path.points[i - 1].y,
            (path.points[i - 1].x + path.points[i].x) / 2,
            (path.points[i - 1].y + path.points[i].y) / 2);
      } else {
        drawing.lineTo(path.points[i].x, path.points[i].y);
      }
    }
    path.path2d = drawing;
    path.isPathStale = false;
  }
  if (path.isMe && path.isLiveObj && path.isDrawing) {
    ctx.strokeStyle = MY_COLOR_DONG_LAI;
  } else {
    ctx.strokeStyle = (path.isMe ? MY_COLOR : THEIR_COLOR) + path.alpha + ')';
  }
  ctx.stroke(path.path2d);
}

function updatePathsOpacity() {
  if (!paths.length) {
    return;
  }
  var pathIndex = paths.length - 1;
  while (pathIndex >= 0) {
    var path = paths[pathIndex];
    if (path.isDrawing) {
      pathIndex--;
      continue;
    }
    if (!path.isLiveObj) {
      path.alpha -= FADE_RATE_MS;
    }
    if (path.isLiveObj && !path.didBecomeLive) {
      path.alpha = Math.min(path.alpha + LIVE_OBJECT_RATE_MS, 1);
      if (path.alpha === 1) {
        bienNetThanhDongLai(path);
      }
    }

    if (path.alpha <= 0) {
      if (path.destroy) {
        path.destroy();
      }
      paths.splice(pathIndex, 1);
    }
    pathIndex--;
  }
}

function isTooClose(mouseEv1, mouseEv2) {
  return distBetweenEvents(mouseEv1, mouseEv2) < MIN_DIST_TO_DRAW_PX;
}

function distBetweenEvents(mouseEv1, mouseEv2) {
  return dist(mouseEv1.clientX, mouseEv1.clientY, mouseEv2.clientX, mouseEv2.clientY);
}

function dist(x1, y1, x2, y2) {
  return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
}

function removePoint(point) {
  var mirrorIndex = mirrorPoints.indexOf(point);
  if (mirrorIndex >= 0) {
    delete mirrorPoints[mirrorIndex];
  }
  point.remove();
}

function getX(mouseEv) {
  var x;
  if (mouseEv.touches || mouseEv.changedTouches) {
    x = mouseEv.touches.length ? mouseEv.touches[0].clientX : mouseEv.changedTouches[0].clientX;
  } else {
    x = mouseEv.clientX;
  }
  return x - canvasLeft;
}

function getY(mouseEv) {
  var y;
  if (mouseEv.touches || mouseEv.changedTouches) {
    y = mouseEv.touches.length ? mouseEv.touches[0].clientY : mouseEv.changedTouches[0].clientY;
  } else {
    y = mouseEv.clientY;
  }
  return y - canvasTop;
}

function getXAndY(mouseEv) {
  return {x: getX(mouseEv), y: getY(mouseEv)};
}

function convertDrawMessageToPoint(message) {
  return {
    x: message.x * canvasWidth,
    y: message.y * canvasHeight
  };
}

function showMessage(text, opt_showPoke) {
  errorMessageEl.text(text);
  errorWrapperEl.toggleClass('error-wrapper--shown', true);
  pokeWrapperEl.toggleClass('poke-wrapper--shown', !!opt_showPoke);
  isShowingMessage = true;
}

function hideMessage() {
  errorWrapperEl.toggleClass('error-wrapper--shown', false);
  pokeWrapperEl.toggleClass('poke-wrapper--shown', false);
  isShowingMessage = false;
}

function poke() {
  messageService.sendNotification();
  avatarEl.toggleClass('shaking', false);
  window.setTimeout(function() {
    avatarEl.toggleClass('shaking', true);
  }, 100);
}

function showUserInfo(displayName, avatarUrl) {
  var avatarEl = $('.avatar');
  var avatarImageEl= $('.avatar .real-image');
  var displayNameEl = $('.display-name');

  avatarEl.addClass('avatar--has-image');
  avatarImageEl.attr('src', avatarUrl);
  displayNameEl.text(displayName);
}

function deleteAllLiveObjs(opt_liveObjs) {
  var pathIndex = 0;
  var deletedLiveObjIds = [];
  while (pathIndex < paths.length) {
    var path = paths[pathIndex];
    var isMatchedId = !opt_liveObjs ||
        (path.liveObjId && opt_liveObjs.indexOf(path.liveObjId) >= 0);
    if (path.isLiveObj && isMatchedId) {
      deletedLiveObjIds.push(path.liveObjId);
      deletePath(paths, pathIndex);
    } else {
      pathIndex++;
    }
  }
  return deletedLiveObjIds;
}

function deletePath(paths, pathIndex) {
  var path = paths[pathIndex];
  if (path.destroy) {
    path.destroy();
  }
  paths.splice(pathIndex, 1);
}

function getLiveObjPaths(liveObjId) {
  var results = [];
  for (var i = 0; i < paths.length; i++) {
    if (paths[i].liveObjId === liveObjId) {
      results.push(paths[i]);
    }
  }
  return results;
}

function debounce(func, wait, immediate) {
  var timeout;
  return function() {
    var context = this, args = arguments;
    var later = function() {
      timeout = null;
      if (!immediate) func.apply(context, args);
    };
    var callNow = immediate && !timeout;
    clearTimeout(timeout);
    timeout = setTimeout(later, wait);
    if (callNow) func.apply(context, args);
  };
};

main();