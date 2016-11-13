var express = require('express');
var router = express.Router();
var mongoose = require('mongoose');
var bodyParser = require('body-parser')


var Member = mongoose.model('member');
/*var room = mongoose.model('room');
var timeline = mongoose.model('timeline');
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

  var member = Member.find({'email':email});
  member.remove(function(err){
    if(err){
      console.log(err);
      res.status(500).send('remove error');
      return;
    }
    res.status(200).send("Removed");
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

  Member.find({email:select_email}, function(err,member){
    if(err){
      console.error(err);
      res.json({'result':'fail'});
    }
    else{
      console.log('회원정보 조회 완료');
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
router.post('/')


module.exports = router;
