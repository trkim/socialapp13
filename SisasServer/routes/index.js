var express = require('express');
var router = express.Router();
var mongoose = require('mongoose');

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

router.post('/insert_member',function(req,res){
  req.accepts('application/json');

  var name = req.body.name;
  var email = req.body.email;
  var password = req.body.password;
  var major = req.body.major;
  var category = req.body.category;
  var coupon = req.body.coupon;
  var rating = req.body.rating;

  var member = new Member({'name':name,'email':email,'password':password,'major':major,'category':category,'coupon':coupon, 'rating':rating});
  member.save(function(err){
    if(err){
      console.log(err);
      res.status(500).send('update error');
      return;
    }
    console.log("회원가입 완료");
    res.status(200).send("inserted");
  });
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

  var member = Member.find({email:select_email});
});

router.post('/login',function(req,res){
  //var input_email = req.body.email;//로그인 위해 사용자에게 입력받은 이메일
  //var input_pwd = req.body.password;

  /*var input_email = 'aa@aa.com';
  var input_pwd =  '1111';*/

  var member = Member.find({email : req.body.email, password : req.body.password});

  console.log(member);
  if(isEmpty(member) || isNull(member)){
    console.log('로그인 실패!');
    return res.json({'result': 'login_failed'});
  }else{
    console.log("로그인 성공!");


    return res.json(member);
  }
});

module.exports = router;
