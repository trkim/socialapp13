var express = require('express');
var router = express.Router();

var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var roomSchema = new Schema({
    email:String,
    king_name:String,
    room_name:String,
    capacity:Number,
    category:String,
    start_date:String,
    end_date:String,
    comment:String
});

mongoose.model('room',roomSchema);