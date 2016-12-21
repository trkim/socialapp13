var express = require('express');
var router = express.Router();

var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var share_scrap_boxSchema = new Schema({
    share_scrap_id : String,
    room_id : String,
    article_title : String,
    url : String,
    content : String,
    opinion : String,
    keyword : String,
    date : String,
    email : String
});


mongoose.model('share_scrap_box',share_scrap_boxSchema);