var express = require('express');
var router = express.Router();

var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var keyword_boxSchema = new Schema({
   keyword_box_id : String,
    date : String,
    keyword : String,
    room_id : Number
});

mongoose.model('keyword_box',keyword_boxSchema);