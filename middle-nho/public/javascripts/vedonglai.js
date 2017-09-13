var PICKER_SIZE = 24;

var buttonDongLaiEl = $('.ve-dong-lai');
var buttonDeleteEl = $('.delete-objs');
var windowEl = $('.window');
var lastMovedPath;
var deletedLiveObjIds = {};

var debouncedSendPickMessage = debounce(function() {
  var topLeftPos = timDiemTraiTrenCuaTatCa(lastMovedPath.liveObjId);
  var dataPos = {x: topLeftPos.x / canvasWidth, y: topLeftPos.y / canvasHeight};
  messageService.sendPickDataMessage(lastMovedPath.liveObjId, dataPos);
}, 100);


function veDongLai() {
  buttonDongLaiEl.on('click', function() {
    buttonDongLaiEl.toggleClass('button-wrapper--activated')
  });
  buttonDeleteEl.on('click', function() {
    messageService.sendDeleteAllLiveObjsMessage(deleteAllLiveObjs());
    buttonDeleteEl.toggleClass('delete-objs--is-shown', false);
  });
}

function dangBatVeDongLai() {
  return buttonDongLaiEl.hasClass('button-wrapper--activated');
}

function bienNetThanhDongLai(path) {
  path.didBecomeLive = true;
  path.picker = taoCaiCam(path);
  path.destroy = function() {
    path.picker.remove();
    deletedLiveObjIds[path.liveObjId] = true;
  }
  path.picker.on('mousedown touchstart', xuLyBamXuongTayCam.bind(this, path));
  path.picker.on('mousemove touchmove', xuLyDiChuyenTayCam.bind(this, path));
  windowEl.on('mouseup touchend', xuLyNhacLenTayCam.bind(this, path));
  if (path.isMe || !path.liveObjId) {
    path.liveObjId = messageService.createLiveObject();
  }
  // Nếu có ít nhất 1 obj thì bật nút xóa hết lên.
  buttonDeleteEl.toggleClass('delete-objs--is-shown', true);
  capNhapDiemTraiTren(path);
}

function xuLyBamXuongTayCam(path, ev) {
  if (path.isDrawing) {
    return;
  }
  path.isMoving = true;
  path.startPickerPos = docViTri(path.picker);
  path.startMousePos = getXAndY(ev);
  path.startPoints = xaoChepSau(path.points);
}

function xuLyDiChuyenTayCam(path, ev) {
  if (path.isMoving) {
    diChuyenNet(path, ev);
    ev.preventDefault();
  }
}

function xuLyNhacLenTayCam(path, ev) {
  path.isMoving = false;
}

function diChuyenNet(path, ev) {
  var endMousePos = getXAndY(ev);
  var deltaX = endMousePos.x - path.startMousePos.x;
  var deltaY = endMousePos.y - path.startMousePos.y;
  for (var i = 0; i < path.points.length; i++) {
    path.points[i].x = path.startPoints[i].x + deltaX;
    path.points[i].y = path.startPoints[i].y + deltaY;
  }
  path.isPathStale = true;
  path.picker.css('left', path.startPickerPos.x + deltaX);
  path.picker.css('top', path.startPickerPos.y + deltaY);

  capNhapDiemTraiTren(path);

  lastMovedPath = path;
  debouncedSendPickMessage();
}

function taoCaiCam(path) {
  // Xem xem có tay cầm sẵn chưa.
  var sameObjPaths = getLiveObjPaths(path.liveObjId);
  if (sameObjPaths.length > 0 && sameObjPaths[0] !== path && sameObjPaths[0].picker) {
    return sameObjPaths[0].picker;
  }
  // Tạo tay cầm mới.
  var el = $('<div>').addClass('picker');
  var pickerPos = timeViTriCuaCaiCam(path);
  el.css('left', pickerPos.x);
  el.css('top', pickerPos.y);
  windowEl.append(el);
  return el;
}

function timeViTriCuaCaiCam(path) {
  var minX = -1;
  var minY = -1;
  var maxX = -1;
  var maxY = -1;
  for (var i = 0; i < path.points.length; i++) {
    var point = path.points[i];
    if (i === 0) {
      minX = point.x;
      maxX = point.x;
      minY = point.y;
      maxY = point.y;
    }
    minX = Math.min(minX, point.x);
    maxX = Math.max(maxX, point.x);
    minY = Math.min(minY, point.y);
    maxY = Math.max(maxY, point.y);
  }

  return {x: maxX, y: minY - PICKER_SIZE};
}

function docViTri(el) {
  return {
    x: parseInt(el.css('left'), 10),
    y: parseInt(el.css('top'), 10)
  };
}

function xaoChepSau(arr) {
  return JSON.parse(JSON.stringify(arr));
}

function diChuyenTatCa(liveObjId, newX, newY) {
  var pathsToMove = getLiveObjPaths(liveObjId);
  var topLeftPos = timDiemTraiTrenCuaTatCa(liveObjId);

  if (!pathsToMove.length || !topLeftPos) {
    return;
  }

  var targetX = newX * canvasWidth;
  var targetY = newY * canvasHeight;

  for (var k = 0; k < pathsToMove.length; k++) {
    diChuyenTheoToaDo(pathsToMove[k],
        targetX - topLeftPos.x, targetY - topLeftPos.y);
  }
}
function timDiemTraiTrenCuaTatCa(liveObjId) {
  var paths = getLiveObjPaths(liveObjId);
  if (!paths.length) {
    return;
  }
  var left = paths[0].pathLeft;
  var top = paths[0].pathTop;
  for (var k = 1; k < paths.length; k++) {
    left = Math.min(left, paths[k].pathLeft);
    top = Math.min(top, paths[k].pathTop);
  }
  return {x: left, y: top};
}

function capNhapDiemTraiTren(path) {
  if (!path.points.length) {
    return;
  }
  var pathLeft = path.points[0].x;
  var pathTop = path.points[0].y;
  for (var i = 1; i < path.points.length; i++) {
    pathLeft = Math.min(pathLeft, path.points[i].x);
    pathTop = Math.min(pathTop, path.points[i].y);
  }
  path.pathLeft = pathLeft;
  path.pathTop = pathTop;
}

function diChuyenTheoToaDo(path, deltaX, deltaY) {
  for (var i = 0; i < path.points.length; i++) {
    path.points[i].x = path.points[i].x + deltaX;
    path.points[i].y = path.points[i].y + deltaY;
    if (i == 0) {
      path.pathLeft = path.points[i].x;
      path.pathTop = path.points[i].y;
    }
    path.pathLeft = Math.min(path.pathLeft, path.points[i].x);
    path.pathTop = Math.min(path.pathTop, path.points[i].y);
  }
  var pickerPos = timeViTriCuaCaiCam(path);
  path.picker.css('left', pickerPos.x);
  path.picker.css('top', pickerPos.y);
  path.isPathStale = true;
}

veDongLai();