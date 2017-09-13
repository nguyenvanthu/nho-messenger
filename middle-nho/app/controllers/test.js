'use strict';
var logger = require('../../logger/index')(module);

var demo = function(req,res){
	logger.debug('demo');
}


exports = module.exports = {
	demo: demo
}