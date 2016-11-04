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

mongoose.model('member',memberSchema);




module.exports = router;
