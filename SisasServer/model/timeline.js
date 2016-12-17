var express = require('express');
var router = express.Router();

var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var timelineSchema = new Schema({
    keyword_box_id : String,
    title : String,
    content : String,
    url : String,
    opinion : String,
    email : String
});


mongoose.model('timeline',timelineSchema);