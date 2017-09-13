'use strict';
var logger = require('../../logger/index')(module);

var ACTION_DOWN = 0;
var ACTION_UP = 1;
var ACTION_MOVE = 2;

var renderUi = function(req,res){
  var tenNguoiA = req.query.ten ? req.query.ten : 'Thử';
	res.render('index', {tenNguoiA: tenNguoiA, chuCaiDauA: tenNguoiA[0]});
}

var sendMessage = function(req,res){
  // TODO(thunv): Connect voi server that.
  res.status(200).send('');
}

var pullMessages = function(req,res){
  // TODO(thunv): Connect voi server that.
  // Make a fake path.
  var points = [];
  if (Math.random() < 0.2) {
    var numPoints = randomInt(40) + 10;
    var width = 400;
    var height = 600;
    var x = randomInt(width);
    var y = randomInt(height - 100) + 100;

    points.push({action: ACTION_DOWN, index: 0, x: x / width, y: y / height});
    for (var i = 0; i < numPoints; i++) {
      x = Math.max(0, Math.min(width, x + randomInt(60) - 30));
      y = Math.max(0, Math.min(height, y + randomInt(60) - 30));
      points.push({action: ACTION_MOVE, index: i + 1, x: x / width, y: y / height});
    }
    points.push({action: ACTION_UP, index: numPoints + 1, x: x / width, y: y / height});
  }
  res.status(200).send({data: points});
}

function randomInt(num) {
  return Math.floor(Math.random() * num);
} 

exports = module.exports = {
	renderUi: renderUi,
  sendMessage: sendMessage,
  pullMessages: pullMessages
}