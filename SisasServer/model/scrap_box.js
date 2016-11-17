var express = require('express');
var router = express.Router();

var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var scrap_boxSchema = new Schema({
    article_title : String,
    url : String,
    comment : String,
    keyword_box_id : String,
    email : String
});


mongoose.model('scrap_box',scrap_boxSchema);