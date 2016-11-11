var express = require('express');
var path = require('path');
var favicon = require('serve-favicon');
var logger = require('morgan');
var cookieParser = require('cookie-parser');
var bodyParser = require('body-parser');
var mongoose = require('mongoose');
var session = require('express-session');

var app = express();

var Member = mongoose.model('member');

mongoose.Promise = global.Promise;
mongoose.connect('mongodb://52.78.157.250:27017/SisasDB', function(err){
  if(err){
    console.log("db error : ", err);
    throw err;
  }
  else {
    console.log("db connected!");
    Member.find(function(err,result){
      console.log('app.js : err : '+err);
      console.log('app.js : result : '+result);
      if(err){
        console.log('에러발생');
      }
      if(result){
        console.log('있음');
      }
    });
  }
});

require('./model/member');
require('./model/room');
require('./model/keyword_box');
require('./model/scrap_box');
require('./model/study_member');
require('./model/timeline');
var member = mongoose.model('member');

var routes = require('./routes/index');
var users = require('./routes/users');


// view engine setup
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'jade');

// uncomment after placing your favicon in /public
//app.use(favicon(path.join(__dirname, 'public', 'favicon.ico')));
app.use(logger('dev'));
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));

app.use('/', routes);
app.use('/users', users);

// catch 404 and forward to error handler
app.use(function(req, res, next) {
  var err = new Error('Not Found');
  err.status = 404;
  next(err);
});

// error handlers

// development error handler
// will print stacktrace
if (app.get('env') === 'development') {
  app.use(function(err, req, res, next) {
    res.status(err.status || 500);
    res.render('error', {
      message: err.message,
      error: err
    });
  });
}

// production error handler
// no stacktraces leaked to user
app.use(function(err, req, res, next) {
  res.status(err.status || 500);
  res.render('error', {
    message: err.message,
    error: {}
  });
});

app.use(session({
  secret:'keyboard cat',
  resave : false,
  saveUninitialized:true
}));


var ip = '52.78.157.250:3000';



module.exports = app;
