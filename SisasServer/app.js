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

//socket.io
var io = socket_io();
app.io = io;



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


require('./model/member');
require('./model/room');
require('./model/seq');
require('./model/keyword_box');
require('./model/scrap_box');
require('./model/timeline');
require('./model/share_scrap_box');
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

////////socket.io/////////
//socket.io
io.on('connection', function(socket){
  console.log('socket 연결됨')
  var addedUser = false;
 /* socket.on('login', function(username){
    console.log('username : '+username);
    socket.username = username;
  });

  socket.on('setting_roomid', function(room_id){
    console.log('room_id : '+room_id);
    socket.room_id = room_id;
  })*/

  socket.emit('connection', {
    type : 'connected'
  });

  //otchat start
  socket.on('joinroom', function(data){
    console.log(data.type);
    if(data.type == 'join'){
     // socket.join(data.room_id);
     // console.log('현재 room_id : '+data.room_id);

      //socket.room_id = data.room_id;
    }
  });  


  socket.on('new message', function(data){
   // console.log('현재 socket room_id : '+socket.room_id);
    //console.log('send message room_id : '+data.room_id);
    console.log('send message : '+data.message);
    console.log('send message username : '+data.username);
    socket.broadcast.emit('new message', data);
  });

//ot chat end

  //main chat start
  socket.on('watchroom', function(data){
    console.log('main 채팅방 입장');
    console.log(data.type);
      if(data.type == 'watch'){
       // socket.join(data.room_id);
        console.log('현재 room_id : '+data.room_id);
        console.log('현재 keyword : '+data.keyword);

       // socket.room_id = data.room_id;
        socket.keyword = data.keyword;
        socket.date = data.date;

      }
  });

  socket.on('new article', function(data){
    console.log('socket 기사 가져오기');
    console.log('username : '+data.username);
    console.log('기사 제목 : '+data.title);
    console.log('url : '+data.url);
    console.log('opinion : '+data.opinion);
    socket.broadcast.to(data.room_id).emit('new article', data) ;

  });



  //main chat end

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


})

module.exports = app;
