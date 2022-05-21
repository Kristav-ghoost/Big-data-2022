var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var dataSchema = new Schema({
    'data' : [{ lat : Number, lng : Number, pow : Number, _id: false}],
    'time' : {type: Date, required: true},
    'author': {type: Schema.Types.ObjectId, ref: 'user'}
});

module.exports = mongoose.model('data', dataSchema);
