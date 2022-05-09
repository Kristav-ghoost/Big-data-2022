var mongoose = require('mongoose');
var Schema   = mongoose.Schema;

var dataSchema = new Schema({
	'latitude' : Number,
	'longitude' : Number,
	'author' : {type: Schema.Types.ObjectId, ref: 'user'}
});

module.exports = mongoose.model('data', dataSchema);
