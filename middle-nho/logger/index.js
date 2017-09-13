"use strict";
var winston = require('winston');
var getLabel = function(folder){
	var parts = folder.filename.split('/');
	return parts[parts.length - 2] + '/' + parts.pop();
};
var tsFormat = new Date().toLocaleTimeString();


module.exports = function(callingModule){
	return new winston.Logger({
		level: 'debug',
		transports: [
			new (winston.transports.Console)({ label: getLabel(callingModule)}),
			new (winston.transports.File)({ filename: 'console.log', timestamp: tsFormat, label: getLabel(callingModule)})
		]
	});
};