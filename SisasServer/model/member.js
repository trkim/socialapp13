var express = require('express');
var router = express.Router();

var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var memberSchema = new Schema({
        name:String,
        email:String,
        password:String,
        major:String,
        category:String,
        coupon:String,
        rating:String,
        profile_pic:String
});

mongoose.model('member',memberSchema);

