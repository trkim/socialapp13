var express = require('express');
var router = express.Router();

var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var seqSchema = new Schema({
    _id:String,
    seq:Number

});

mongoose.model('seq',seqSchema);