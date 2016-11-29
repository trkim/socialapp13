var express = require('express');
var path = require('path');
var favicon = require('serve-favicon');
var logger = require('morgan');
var cookieParser = require('cookie-parser');
var bodyParser = require('body-parser');
var mongoose = require('mongoose');
var session = require('express-session');

var socket_io = require('socket.io');

var app = express();


mongoose.Promise = global.Promise;
mongoose.connect('mongodb://52.78.157.250:27017/SisasDB', function(err){
  if(err){
    console.log("db error : "+err);
    throw err;
  }
  else {
    console.log("db connected!");
  }
});

/*
require('./model/member');
var Member = mongoose.model('member');

app.get('/members',function(req,res,err){
  var members = new Member();
  Member.find().select('email').exec(function(err,members){
    if(err){
      console.error(err);
      throw err;
    }
    console.log("member : ");
    console.log(members);
    res.send(members);
  })
})
app.get('/member/:email',function(req,res,err){

  Member.findOne({'email':req.params.email},function(err,result){
    if(err){
      console.log(err);
      throw err;
    }
    console.log("%%%");
    console.log(req.params.email);
    console.log(result);
    res.send(result);
    //res.status(status).send(result);
  });
});
*/

require('./model/member');
require('./model/room');
require('./model/seq');
require('./model/keyword_box');
require('./model/scrap_box');
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

//socket.io
var io = socket_io();
app.io = io;

var routes = require('./routes/index')(io);

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

//socket.io
/*io.on('connection', function(socket){
  console.log('socket 연결됨')
  var addedUser = false;

  // when the client emits 'new message', this listens and executes
  socket.on('new message', function (data) {
    // we tell the client to execute 'new message'
    console.log(data);
    socket.broadcast.emit('new message', {
      username: socket.username,
      message: data
    });
  });

  // when the client emits 'add user', this listens and executes
  socket.on('add user', function (username) {
    if (addedUser) return;

    // we store the username in the socket session for this client
    socket.username = username;
    ++numUsers;
    addedUser = true;
    socket.emit('login', {
      numUsers: numUsers
    });
    // echo globally (all clients) that a person has connected
    socket.broadcast.emit('user joined', {
      username: socket.username,
      numUsers: numUsers
    });
  });

  // when the client emits 'typing', we broadcast it to others
  socket.on('typing', function () {
    socket.broadcast.emit('typing', {
      username: socket.username
    });
  });

  // when the client emits 'stop typing', we broadcast it to others
  socket.on('stop typing', function () {
    socket.broadcast.emit('stop typing', {
      username: socket.username
    });
  });

  // when the user disconnects.. perform this
  socket.on('disconnect', function () {
    if (addedUser) {
      --numUsers;
      console.log('disconnect');
      // echo globally that this client has left
      socket.broadcast.emit('user left', {
        username: socket.username,
        numUsers: numUsers
      });
    }
  });


})*/



var ip = '52.78.157.250:3000';



module.exports = app;
