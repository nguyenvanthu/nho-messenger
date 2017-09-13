var multer = require('multer'),
	upload = multer(),
	processor = require('./processor')();

var requireSession = function(req, res, next){
  console.log(req.session.user);
  if (!req.session.user){
    processor.render(req, res, {
      result: null,
      error: 'Authorization required'
    });
  } else {
    next();
  }

}

exports = module.exports = function(app, controllers, modules) {
  app.get('/', controllers.chat.renderUi);

  app.get('/messages', controllers.chat.pullMessages);

  app.post('/send', controllers.chat.sendMessage);

  app.post('/test', controllers.test.demo);

}
