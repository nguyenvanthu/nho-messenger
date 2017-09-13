'use strict';

var current,
  processors;
processors = require('./processor/index');
exports = module.exports = function(injections) {
  if (!processors) {
    processors = injections;
  }
  if (!current) {
    console.log('DEFAULT PROCESSOR = JSON');
    current = processors.json;
  }
  return {
    render: function(req, res, object, options) {
      if (options && options.processor) {
        switch (options.processor) {
          case 'json':
            processors.json.render(req, res, object, options);
            break;
          case 'html':
            processors.html.render(req, res, object, options);
            break;
        }
      } else {
        if (req.query.format) {
          // is a processor format option
          switch (req.query.format) {
            case 'json':
              processors.json.render(req, res, object, options);
              break;
            case 'html':
              processors.html.render(req, res, object, options);
              break;
          }
        } else {
          processors.json.render(req, res, object, options);
        }
      }
    }
  };
};
