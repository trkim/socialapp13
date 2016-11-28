var express = require('express');
var router = express.Router();
var mongoose = require('mongoose');
var bodyParser = require('body-parser');
var exec = require('child_process').exec;

// Setup basic express server
var app = express();
var server = require('http').createServer(app);
var io = require('socket.io')(server);
var port = process.env.PORT || 3003;


var Member = mongoose.model('member');
var Room = mongoose.model('room');
var Seq = mongoose.model('seq');
var timeline = mongoose.model('timeline');
var keyword_box = mongoose.model('keyword_box');
var scrap_box = mongoose.model('scrap_box');
//var ObjectId = require('mongodb').ObjectId;

/* GET home page. */
router.get('/', function(req, res, next) {
  res.render('index', { title: 'SocialApp' });
});


////////////////////////////member//////////////////////////////////////

router.post('/insert_member',function(req,res){
  req.accepts('application/json');
  var member = new Member();

  var name = req.body.name;
  var email = req.body.email;
  var password = req.body.password;
  var major = req.body.major;
  var category = req.body.category;
  var coupon = req.body.coupon;
  var rating = req.body.rating;

  Member.findOne({'email':email}, function(err, member){
    if( member == "" || member == null || member == undefined || ( member != null && typeof member == "object" && !Object.keys(member).length )){
      console.log('email 사용가능')
      var member = new Member({'name':name,'email':email,'password':password,'major':major,'category':category,'coupon':coupon, 'rating':rating});
      member.save(function(err){
        if(err){
          console.log(err);
          res.status(500).send('update error');
          return;
        }
        console.log("회원가입 완료");
        res.json({'result':'insert_success'});
      });
    }else{
      console.log('이미 있는 email')
      res.json({'result':'already_used'});
    }

  })

});

router.post('/delete_member',function(req,res){
  var email = req.body.email;

  Member.findOne({'email':email}, function(err,member){
    member.remove(function(err){
      if(err){
        console.log(err);
        res.status(500).send('remove error');
      }
      res.status(200).send("Removed");
    });
  });
});

router.post('/update_member',function(req,res){
  var email = req.body.email;
  var password = req.body.password;
  var major = req.body.major;
  var category = req.body.category;

  Member.findOne({'email':email},function(err,member){
    if(err){
      console.log(err);
      res.status(500).send('update find error');
      return;
    }
    member.password = password;
    member.major = major;
    member.category = category;
    member.save(function(err){
      if(err){
        console.log(err);
        res.status(500).send('update save error');
        return;
      }
      res.status(200).send('Updated');
    });
  });
});


router.post('/get_member',function(req,res){
  var select_email = req.body.email;

  Member.findOne({'email':select_email}, function(err,member){
    if(err){
      console.error(err);
      res.json({'result':'fail'});
    }
    if(member){
      console.log('회원정보 조회 완료');
      console.log(member);
      return res.json(member);
    }
  });
});

router.post('/get_study_member', function(req,res){
  var room_id = req.body.room_id;

  Room.find({'room_id':room_id}, function(err, emaillist){
      if(err){
        console.error(err);
        res.json({'result':'fail'});
      }else{
        var count = 1;
        var memberlist = [];
        emaillist.forEach(function(email){
          Member.findOne({'email':email}, function(err, member){
            if(err){
              console.error(err);
              res.json({'result':'fail'});
            }
            if(member){
              memberlist.push(member);
              if(count == emaillist.length){
                res.json(memberlist);
              }else{
                count++;
              }
            }
          });
        });
      }
  });
});

router.post('/login',function(req,res){
  Member.findOne({'email':req.body.email}, function(err,member){
  console.log(req.body.email);
  console.log(req.body.password);
  console.log("err"+err);
  console.log("member :"+member);
  if(err){
    console.log('err 발생');
    return res.json({'result':'fail'});
  }
  if(member){
    console.log('아이디 존재')
    if(member.password == req.body.password){
      console.log('로그인 성공')
      return res.json(member);
    }else{
      console.log('비밀번호 틀림')
      return res.json({'result':'fail_pwd'});
    }
  }else{
    console.log('아이디 없음')
    return res.json({'result':'fail_id'});
  }
  });

});


router.post('/insert_room', function(req,res){
  req.accepts('application/json');

  var room = new Room();
  Seq.findOne({'_id':'seq_post'}, function(err, result){
    if(err){
      console.error(err);
      res.json({'result':'fail'});
    }else{
      console.log('스터디룸 생성')
      result.seq = result.seq+1;

      room.room_id = result.seq;
      room.email = req.body.email;
      room.king_name = req.body.king_name;
      room.room_name = req.body.room_name;
      room.capacity = req.body.capacity;
      room.category = req.body.category;
      room.start_date = req.body.start_date;
      room.end_date = req.body.end_date;
      room.comment = req.body.comment;


      result.save();

      room.save(function(err){
        if(err){
          console.error(err);
          res.json({'result':'fail'});
        }
        else {
          console.log('스터디룸 생성 완료')
          res.json({'result': 'success'});
        }
      });
    }
  });
});

router.post('/delete_room_req', function(req,res){
  var message = req.body.message;//팀원들에게 보낼 메세지
  var room_id = req.body.room_id;//삭제할 방의 room_id

  Room.find({'room_id':room_id}, function(err, room){
    if(err){
      console.error(err);
      res.json({'result':'fail'});
    }else{
      Member.find({'name':{$ne:room.king_name}}, function(err,member_list){
        if(err){
          console.error(err);
          res.json({'result':'fail'});
        }else{
          console.log('스터디원에게 방삭제 메세지 전송');
          res.json(member_list);
        }
      });
    }
  })
});

router.post('/delete_room', function(req,res){
  var email = req.body.email;
  var room_id = req.body.room_id;

  Room.findOne({'email':email, 'room_id':room_id}, function(err, room){
    if( room == "" || room == null || room == undefined || ( room != null && typeof room == "object" && !Object.keys(room).length )){
      res.json({'result':'delete_room'});
      //이 부분 안되면 함수 나눠서 따로 진행
      room.remove(function(err){
        if(err){
          console.error(err);
          res.json({'result':'fail'});
        }
        else{
          console.log('room delete success');

        }
      });
    }
  });
});

router.post('/update_room', function(req,res){
  var room_id = req.body.room_id;
  var email = req.body.email;
  var king_name = req.body.king_name;
  var room_name = req.body.room_name;
  var capacity = req.body.capacity;
  var category = req.body.category;
  var start_date = req.body.start_date;
  var end_date = req.body.end_date;
  var comment = req.body.comment;

  Room.findOne({'room_id':room_id}, function(err, room){

    if(err){
      console.log('room 수정 완료');
      res.json({'result':'fail'});
    }
    if(room) {
      room.room_name = room_name;
      room.capacity = capacity;
      room.category = category;
      room.start_date = start_date;
      room.end_date = end_date;
      room.comment = comment;

      room.save();
      res.json({'result': 'room_update_success'})
    }

  })


});

router.post('/get_room', function(req,res){
  var email = req.body.email;
  var room_id = req.body.room_id;

  Room.findOne({'email':email, 'room_id':room_id}, function(err, room){
    if(err){
      console.error(err);
      res.json({'result':'fail'});
    }else{
      console.log('get room 성공');
      res.json(room);
    }
  });
});

router.get('/get_myroomlist', function(req,res){
  var email = req.query.email;
  console.log('email : '+email);
  Room.find({'email':email}, function(err, roomlist){
    if(err){
      console.log('get_myroomlist 에러');
      res.json({'result':'fail'});
    }
    if(roomlist){
      console.log('get_myroomlist 성공');
      console.log(roomlist);
      res.json(roomlist);
    }else{
      res.json({'result':'fail'});
    }
  });
});

router.get('/get_ctgroomlist', function(req,res){
  var category = req.query.category;
  if(category == 'all'){
    category = '전체';
  }else if(category == 'politics'){
    category = '정치';
  }else if(category == 'economics'){
    category = '경제';
  }else if(category == 'social'){
    category = '사회';
  }else if(category == 'it'){
    category = 'IT';
  }else if(category == 'world'){
    category = '세계';
  }
  console.log('category : '+category);
  if(category == '전체'){



    Room.distinct('room_id', function(err, room_id_list){
      if(err){
        console.error(err);
        res.json({'result':'fail'});
      }else{
        var count = 1;
        var roomlist = [];
        room_id_list.forEach(function(room_id){
          Room.findOne({'room_id':room_id}, function(err, room){
            if(err){
              console.error(err);
            }
            if(room) {
              roomlist.push(room);
              if(count == room_id_list.length){
                res.json(roomlist);
              } else {
                count++;
              }
            }
          })
        })
      }
    });


  }else {
    Room.find({'category': category}, function (err, roomlist) {
      if (err) {
        console.error(err);
        res.json({'result': 'fail'});
      }
      else {
        console.log('get_category room list 성공');
        console.log(roomlist);
        res.json(roomlist);
      }
    });
  }
});

router.post('/join_room', function(req,res){
  var room_id = req.body.room_id;

  Room.findOne({'room_id':room_id}).count(function(err,num){
      Room.findOne({'room_id':room_id}, function(err,room){
        if(room.capacity - num >= 1) {
          var myroom = new Room();
          myroom.email = room.email;
          myroom.room_id = room.room_id;
          myroom.king_id = room.king_id;
          myroom.room_name = room.room_name;
          myroom.capacity = room.capacity;
          myroom.category = room.category;
          myroom.start_date = room.start_date;
          myroom.end_date = room.end_date;
          myroom.comment = room.comment;

          myroom.save();
          console.log('study 참여 완료');
          res.json({'result': 'success'});
        }else{
          console.log('인원 초과');
          res.json({'result':'fail'});
        }
      });
  });
});

router.post('/get_room_and_member', function(req, res){
  var room_id = req.body.room_id;

  Room.find({'room_id':room_id}, function(err, memberlist){
    if(err){
      console.error(err);
      res.json({'result':'fail'});
    }
    if(memberlist){
      console.log('스터디 참여 회원 정보 조회 완료');
      res.json(memberlist);
    }
  })
})


/////////////////chatting socket.io
server.listen(port, function () {
  console.log('Server listening at port %d', port);
});

// Routing
app.use(express.static(__dirname + '/public'));

// Chatroom

var numUsers = 0;

io.on('connection', function (socket) {
  var addedUser = false;

  // when the client emits 'new message', this listens and executes
  socket.on('new message', function (data) {
    // we tell the client to execute 'new message'
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

      // echo globally that this client has left
      socket.broadcast.emit('user left', {
        username: socket.username,
        numUsers: numUsers
      });
    }
  });
});

module.exports = router;
