var express = require('express');
var app = express();
var router = express.Router();
var mongoose = require('mongoose');
var PythonShell = require('python-shell');
var encoding = require('encoding');
//var gcm = require('node-gcm');



var Member = mongoose.model('member');
var Room = mongoose.model('room');
var Seq = mongoose.model('seq');
var Timeline = mongoose.model('timeline');
var Keyword_box = mongoose.model('keyword_box');
var Scrap_box = mongoose.model('scrap_box');
var Share_scrap_box = mongoose.model('share_scrap_box');
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
  var profile_pic = req.body.profile_pic;

  Member.findOne({'email':email}, function(err, member){
    if( member == "" || member == null || member == undefined || ( member != null && typeof member == "object" && !Object.keys(member).length )){
      console.log('email 사용가능')
      var member = new Member({'name':name,'email':email,'password':password,'major':major,'category':category,'coupon':coupon, 'rating':rating, 'profile_pic':profile_pic});
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
        res.json({'result':'fail'})
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

router.post('/insert_profile', function(req, res){
  console.log('insert_profile');
  var email = req.body.email;
  var profile_pic = req.body.profile_pic;

  Member.findOne({'email':email}, function(err, member){
    if(err){
      console.error(err);
      res.json({'result':'fail'});
    }
    else{
      console.log('profile_pic : '+profile_pic)
      member.profile_pic = profile_pic;
      member.save(function(err){
        if(err){
          console.error(err);
          res.json({'result':'fail'});
        }
        else{
          console.log('프로필 사진 수정 성공');
          res.json({'result':'success'});
        }
      });
    }
  });

});
/*
router.post('/get_profile', function(req, res){
  console.log('set_profile_pic');
  var email = req.body.email;

  Member({'email':email}, function(err, member){
    if(err){
      console.error(err);
      res.json({'result':'fail'});
    }else{
      console.log('프로필 사진 설정 성공');
      res.json(member.profile_pic);
    }

  });
});*/

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
      res.json(member);
    }
  });
});

router.get('/get_study_member', function(req,res){
  console.log('get_study_member')
  var room_id = (req.query.room_id)*1;

  Room.find({'room_id':room_id}, function(err, emaillist){
      if(err){
        console.error(err);
        res.json({'result':'fail'});
      }else{
        console.log(emaillist)
        var count = 1;
        var memberlist = [];
        emaillist.forEach(function(room){
          Member.findOne({'email':room.email}, function(err, member){
            if(err){
              console.error(err);
              res.json({'result':'fail'});
            }
            if(member){
              console.log(member)
              memberlist.push(member);
              if(count == emaillist.length){
                console.log('get study member 성공')
                console.log(memberlist);
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


//study room을 삭제할 때 팀원에게 메세지 보내기
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

router.post('/sendMessage', function(req, res){
  var message = new gcm.Message();
});

router.post('/delete_room', function(req,res){
  console.log('delete_room');
  var email = req.body.email;
  var room_id = req.body.room_id;
  console.log('email : '+email+'  room_id : '+room_id);

  Room.findOne({'email':email, 'room_id':room_id}, function(err, room) {
    if(err){
      console.error(err);
      res.json({'result':'fail'});
    }
    else{
        //이 부분 안되면 함수 나눠서 따로 진행
        room.remove(function (err) {
          if (err) {
            console.error(err);
            res.json({'result': 'fail'});
          }
          else {
            console.log('방 삭제 성공');
            res.json({'result':'success'});
          }
        });
    }
  });
});

router.post('/update_room', function(req,res){
  console.log('update_room');
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
      console.error(err);
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
///////%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
//capacity 수정 필요
router.post('/join_room', function(req,res){
  var room_id = req.body.room_id;
  var email = req.body.email;

  Room.findOne({'room_id':room_id}).count(function(err,num){
      Room.findOne({'room_id':room_id}, function(err,room){
        if(room.capacity - num >= 1) {
          room.capacity = room.capacity -1;
          var myroom = new Room();
          myroom.email = email;
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
////////////////////////////////////////////
//관전하기 전 쿠폰 확인하기
router.post('/check_coupon', function(req, res){
  console.log('check_coupon');
  var email = req.body.email;

  Member.findOne({'email':email}, function(err, member){
    if(err){
      console.error(err);
      res.json({'result':'fail'});
    }
    if((member.coupon*1) >= 1){
      console.log('관전 가능. 잔여 쿠폰 : '+member.coupon);
      console.log('타입 : '+typeof(member.coupon));
      member.coupon = ((member.coupon*1) - 1)+'';
      console.log('잔여 쿠폰 : '+member.coupon);
      member.save(function(err){
        if(err){
          console.error(err);
          res.json({'result':'fail'});
        }
      });
      res.json({'result':'success'});

    }else{
      console.log('관전 포인트 부족');
      res.json({'result':'couponless'});
    }
  });
});
//쿠폰이 남아있으면 관전모드로 고고
router.post('/watch_study', function(req, res){
  console.log('watch_study');
  var room_id = req.body.room_id;

  Room.findOne({'room_id':room_id}, function(err, room){
    if(err){
      console.error(err);
      res.json({'result':'fail'});
    }
    else{
      console.log('관전하자앙');
      res.json({'result':'success'});
    }
  });
});

router.post('/get_room_and_member', function(req, res){
  var room_id = req.body.room_id;

  Room.findOne({'room_id':room_id}, function(err, room){
    if(err){
      console.error(err);
      res.json({'result':'fail'});
    }
    if(room){
      console.log('스터디 참여 회원 정보 조회 완료');
      res.json(room);
    }
  });
});

///chatting
router.post('/fix_keyword', function(req, res){
  req.accepts('application/json');

  //var keyword_box = new Keyword_box();

  var date = req.body.date;
  var keyword = req.body.keyword;
  var email = req.body.email;
  var keyword_box_id = date+keyword+'';
  var room_id = req.body.room_id;

  Keyword_box.findOne({'date':date, 'room_id':room_id}, function(err, keyword_box){
    if(err){
      console.error(err);
      res.json({'result':'fail'});
    }
    if(!keyword_box){
      var keyword_box = new Keyword_box({'date':date,'keyword':keyword,'email':email,'keyword_box_id':keyword_box_id,'room_id':room_id});

      keyword_box.save(function(err){
        if(err){
          console.error(err);
          res.json({'result':'fail'});
        }else{
          console.log('키워드 등록 완료');
          res.json({'result':'success'});
        }
      })
    }else{
      console.log("이미 키워드가 등록되어있습니다.");
      res.json({"result":"duplication"});
    }
  });
});

router.get('/get_keyword', function(req,res){
  console.log('get_keyword');
  var room_id = req.query.room_id;

  Keyword_box.find({'room_id' : room_id}, function(err, keywordlist){
    if(err) {
      console.error(err);
      res.json({'result': 'fail'});
    }
    else{
      console.log('get_keyword 성공');
      console.log(keywordlist);
      res.json(keywordlist);
    }
  });

});


router.get('/scrap_with_keyword', function(req,res){
  console.log('scrap_with_keyword 실행')
  var keyword = req.query.keyword;

  var options = {
    mode : 'text',
    pythonPath : '/usr/bin/python3.5',
    pythonOptions : ['-u'],
    scriptPath : './public/pythonscripts',
    args:[keyword]
  };

  PythonShell.run('run.py', options, function(err, results){
    console.log('python script 실행')
    if(err){
      console.error(err);
      res.json({'result':'fail'});
    }
    else{
      console.log('기사 크롤링 완료');
      console.log(results);
      res.json(results);

    }
  });
});


router.get('/send_fixkeyword', function(req,res){
  console.log('send fix keyword');
  var room_id = req.query.room_id;

  Keyword_box.find({'room_id':room_id}, function(err, keywordlist){
    if(err){
      console.error(err);
      res.json({'result':'fail'});
    }else{
      console.log('keywordlist 호출 완료');
      res.json(keywordlist);
    }
  });
});

router.post('/insert_scrap', function(req,res){
  req.accepts('application/json');
  console.log('insert_scrap');
  var scrap_box = new Scrap_box();

  scrap_box.article_title = req.body.article_title;
  scrap_box.url = req.body.url;
  scrap_box.opinion = req.body.opinion;
  scrap_box.content = req.body.content;
  scrap_box.keyword_box_id = req.body.keyword_box_id;
  scrap_box.email = req.body.email;
  scrap_box.scrap_id = scrap_box.article_title + scrap_box.keyword_box_id;
  scrap_box.room_id = req.body.room_id;

  Scrap_box.findOne({'scrap_id':scrap_box.scrap_id}, function(err, scrap){
    if(err){
      console.error(err);
      res.json({'result':'fail'});
    }
    console.log(scrap);
    if( scrap == "" || scrap == null || scrap == undefined || ( scrap != null && typeof scrap == "object" && !Object.keys(scrap).length )){
      scrap_box.save(function(err){
        if(err){
          console.error(err);
          res.json({'result':'fail'});
        }else{
          console.log('스크랩 성공');
          res.json({'result':'success'});
        }
      });
    }else{
      console.log('스크랩 중복');
      res.json({'result':'fail'});
    }
  });
});

router.post('/save_opinion', function(req, res){
  var scrap_id = req.body.scrap_id;
  var opinion = req.body.opinion;

  console.log(scrap_id);
  console.log(opinion);

  Scrap_box.findOne({'scrap_id':scrap_id}, function(err, scrap_box){
    if(err){
      console.error(err);
      res.json({'result':'fail'});
    }
    console.log(scrap_box);
    scrap_box.opinion = opinion;
    scrap_box.save(function(err){
      if(err){
        console.error(err);
        res.json({'result':'fail'});
      }else{
        console.log('scrap_box에 opinion 추가 성공');
        console.log(scrap_box);
        res.json({'result':'insert_success'});
      }
    });
  });




});

//scraplist와 roomlist 의 정보를 합쳐서 보내야함.
//테스트 필요
router.get('/get_myscraplist', function(req, res){
  console.log('scraplist 가져오기')
  var email = req.query.email;
  var room_id = req.query.room_id;

  Scrap_box.find({'email':email, 'room_id':room_id}, function(err, scraplist){
    if(err){
      console.error(err);
      res.json({'result':'fail'});
    }
    else{
      console.log('scraplist 가져오기 성공');
      res.json(scraplist);
    }
  });

/*  Scrap_box.find({'email':email}, function(err, scraplist){
    if(err){
      console.error(err);
      res.json({'result':'fail'});
    }else{
      console.log('scraplist 호출 성공');
      var resultlist = [];
      var results = {};
      var count = 1;
      scraplist.forEach(function(room_id){
        Room.findOne({'room_id':room_id}, function(err, roomlist){
          //roomlist정보와 scraplist 합치는 로직
          if(err){
            console.error(err);
            res.json({'result':'fail'});
          }
          else {
            for (var key in scraplist) {
              results[key] = scraplist[key];
            }
            for (var key in roomlist) {
              results[key] = roomlist[key];
            }
            resultlist.push(results);
            if(count == scraplist.length){
              console.log('전송하는 myscraplist 데이터');
              console.log(resultlist);
              res.json(resultlist);
            } else {
              count++;
            }
          }
        });
      });
    }
  });*/
});

//기사 공유하기
router.post('/insert_share_scrap', function(req, res){
  req.accepts('application/json');
  console.log('insert_share_scrap');
  var share_scrap_box = new Share_scrap_box();

  share_scrap_box.title = req.body.title;
  share_scrap_box.url = req.body.url;
  share_scrap_box.opinion = req.body.opinion;
  share_scrap_box.content = req.body.content;
  share_scrap_box.keyword = req.body.keyword;
  share_scrap_box.date = req.body.date;
  share_scrap_box.email = req.body.email;
  share_scrap_box.share_scrap_id = share_scrap_box.title + share_scrap_box.keyword_box_id + share_scrap_box.date;
  share_scrap_box.room_id = req.body.room_id;

  Share_scrap_box.findOne({'share_scrap_id':share_scrap_box.share_scrap_id}, function(err, scrap){
    if(err){
      console.error(err);
      res.json({'result':'fail'});
    }
    console.log(scrap);
    if( scrap == "" || scrap == null || scrap == undefined || ( scrap != null && typeof scrap == "object" && !Object.keys(scrap).length )){

      share_scrap_box.save(function(err){
        if(err){
          console.error(err);
          res.json({'result':'fail'});
        }else{
          console.log('스크랩 공유 성공');
          res.json({'result':'success'});
        }
      });
    }else{
      console.log('스크랩 중복');
      res.json({'result':'fail'});
    }
  });
});

//공유한 기사 리스트 가져오기
router.get('/get_share_scraplist', function(req, res){
  console.log('공유 기사 리스트 가져오기');
  var room_id = req.query.room_id;

  Share_scrap_box.find({'room_id':room_id}, function(err, share_scraplist){
    if(err){
      console.error(err);
      res.json({'result':'fail'});
    }
    else{
      console.log('공유한 기사 리스트 가져오기 성공');
      res.json(share_scraplist);
    }
  });
});

//스터디 중 기사 가져오기********
/*
router.get('/get_scraplist', function(req, res){
  console.log('get_scraplist')
  var email = req.query.email;
  var keyword_box_id = req.query.keyword_box_id;

  Scrap_box.find({'email':email, 'keyword_box_id':keyword_box_id}, function(err, scraplist){
    if(err){
      console.error(err);
      res.json({'result':'fail'});
    }
    else{
      console.log('기사 가져오기 성공');
      res.json(scraplist);
    }
  });
});

router.post('/get_scrap', function(req, res){
  console.log('get_scrap');
  var scrap_id = req.body.scrap_id;

  Scrap_box.findOne({'scrap_id':scrap_id}, function(err, scrap){
    if(err){
      console.error(err);
      res.json({'result':'fail'});
    }
    else{xox
      console.log('선택 기사 가져오기 성공');
      res.json(scrap);
    }
  });
});
*/


/////////////////timeline
router.post('/insert_timeline', function(req, res){
  req.accepts('application/json');
  console.log('insert_timeline');

  var timeline = new Timeline();

  timeline.keyword_box_id = req.body.keyword_box_id;
  timeline.title = req.body.title;
  timeline.content = req.body.content;
  timeline.url = req.body.url;
  timeline.opinion = req.body.opinion;
  timeline.email = req.body.email;

  Timeline.findOne({'title':timeline.title}, function(err, result){
    if(err){
      console.error(err);
      res.json({'result':'fail'});
    }
    else{
      if( result == "" || result == null || result == undefined || ( result != null && typeof result == "object" && !Object.keys(result).length )){

        timeline.save(function(err){
          if(err){
            console.error(err);
            res.json({'result':'fail'});
          }else{
            console.log('타임라인 저장 성공');
            res.json({'result':'success'});
          }
        });
      }else{
        console.log('타임라인 중복');
        res.json({'result':'fail'});
      }
    }
  })
});

router.get('/get_mytimelinelist', function(req, res){
  console.log('get_mytimelinelist');
  var email = req.query.email;

  Timeline.find({'email':email}, function(err, timelinelist){
    if(err){
      console.error(err);
      res.json({'result':'fail'});
    }
    else{
      console.log('타임라인 리스트 호출 성공');
      res.json(timelinelist);
    }
  });
});



module.exports = router;
