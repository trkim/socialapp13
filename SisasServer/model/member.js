var express = require('express');
var router = express.Router();

var mongoose = require('mongoose');

var memberSchema = mongoose.Schema({
        name:String,
        email:String,
        password:String,
        major:String,
        category:String,
        coupon:Number,
        rating:Number
});

var Member = mongoose.model('member',memberSchema);

router.post('/insert',function(req,res){
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
        res.status(200).send("inserted");
    });
});

router.post('/delete',function(req,res){
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

router.post('/update',function(req,res){
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
            res.status.(200).send('Updated');
        });
    });
});


module.exports = router;