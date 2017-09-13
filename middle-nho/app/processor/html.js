'use strict';

var beautify = require('js-beautify').js_beautify;

exports = module.exports.render = function(req, res, object, options) {
  var template;
  object = object || {};
  if (!options) {
    template = 'index';
  } else {
    if (options.template) {
      template = options.template;
    } else {
      template = 'index';
    }
  }

  // switch (template) {
  // default:
  var items = 0,
    results = beautify(JSON.stringify(object), {
      indent_size: 2
    });
  if (object.items) {
    items = object.items;
  }
  res.render(template, {
    topic: req.params.topic,
    id: req.params.id,
    body: results,
    data: object,
    items: items
  });
  // }
};
