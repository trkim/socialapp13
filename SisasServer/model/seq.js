var express = require('express');
var router = express.Router();

var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var seqSchema = new Schema({
    _id:String,
    seq:Number

});

seqSchema.statics.findAndModify = function (query, sort, doc, options, callback) {
    return this.collection.findAndModify(query, sort, doc, options, callback);
};

mongoose.model('seq',seqSchema);