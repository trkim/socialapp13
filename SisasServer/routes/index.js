var express = require('express');
var router = express.Router();
var mongoose = require('mongoose');
var bodyParser = require('body-parser')


var Member = mongoose.model('member');
var Room = mongoose.model('room');
var Seq = mongoose.model('seq');
/*var timeline = mongoose.model('timeline');
var keyword_box = mongoose.model('keyword_box');
var scrap_box = mongoose.model('scrap_box');
var study_member = mongoose.model('study_member');
var ObjectId = require('mongodb').ObjectId;*/

/* GET home page. */
router.get('/', function(req, res, next) {
  res.render('index', { title: 'SocialApp' });
});

/*router.get('/insert',function(req,res){
  //var member = new Member.save({name:'haha',email:'b@b.com',password:'1111',major:'bio',category:'politic',coupon:'2',rating:'1'});
  Member.create({
    name:'qq',
    email:'c@c.com',
    password:'1111',
    major:'bio',
    category:'politic',
    coupon:'2',
    rating:'1'
  },function(err,insmember){
    if(err){
      console.error(err);
      throw err;
    }
    console.log("insert complete");
    console.log(insmember);
    res.send('success');
  })
})

router.get('/membersaa',function(req,res){
  Member.find().select('email').exec(function(err, member){
    if(err){
      console.error(err);
      res.send(err);
    }
    else{
      console.log("members:")
      console.log(member);
      res.send(member);
    }

  })
})*/

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

  Member.find({'email':email}, function(err,member){
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

  Member.find({'email':select_email}, function(err,member){
    if(err){
      console.error(err);
      res.json({'result':'fail'});
    }
    if(member){
      console.log('회원정보 조회 완료');
      console.log(member);
      res.json(member);
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

////////////////////////////room//////////////////////////////////////
router.post('/insert_room', function(req,res){
  req.accepts('application/json');

  var room = new Room();
  Seq.findAndModify({query: {"_id":"seq_post"},update: {$inc: {"seq":1}},new: true},function(err,room_id){
    if(err){
      console.error(err);
    }else{
      room.room_id = room_id;
      room.email = req.body.email;
      room.king_name = req.body.king_name;
      room.room_name = req.body.room_name;
      room.capacity = req.body.capacity;
      room.category = req.body.category;
      room.start_date = req.body.start_date;
      room.end_date = req.body.end_date;
      room.comment = req.body.comment;

      room.save();

      res.json({'result':'success'});
    }

  });




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

  Room.findOne({'email':email, 'room_id':room_id}, function(err, room){
    room.room_name = room_name;
    room.capacity = capacity;
    room.category = category;
    room.start_date = start_date;
    room.end_date = end_date;
    room.comment = comment;

    room.save();
    res.json({'result':'room_update_success'})

  })


});

router.post('/get_room', function(req,res){
  var email = req.body.email;
  var room_id = req.body.room_id;

  Room.find({'email':email, 'room_id':room_id}, function(err, room){
    if(err){
      console.error(err);
    }else{
      console.log('get room 성공');
      res.json(room);
    }
  });
});

router.post('/get_myroomlist', function(req,res){
  var email = req.body.email;
  Room.find({'email':email}, function(err, roomlist){
    if(err){
      console.log('get_myroomlist 에러');
    }
    else{
      console.log('get_myroomlist 성공');
      res.json(roomlist);
    }
  });
});

router.post('/get_ctgroomlist', function(req,res){
  var category = req.body.category;

  Room.find({'category':category}, function(err, roomlist){
    if(err){
      console.error(err);
    }
    else{
      console.log('get_category room list 성공');
      res.json(roomlist);
    }
  });
});

router.post('/join_room', function(req,res){
  var email = req.body.email;
  var room_id = req.body.room_id;

  Room.find({'room_id':room_id}).count(function(err,capacity){
    if(capacity >= 1){
      console.log('room capacity'+capacity);
      var room = new Room();
      room.email = email;
      room.room_id = room_id;

      room.save();
      console.log('study 참여 완료');
    }else{
    console.log('인원 초과');
    }
  });
});

module.exports = router;
