var mongoose = require('mongoose');
var Schema = mongoose.Schema;
var bcrypt = require('bcrypt');


var userSchema = new Schema({
    'username': {type: String, required: true, unique: true},
    'password': {type: String, required: true},
    'email': {type: String, required: true, unique: true}
});

userSchema.statics.authenticate = function (username, password, callback) {
    User.findOne({username: username})
        .exec(function (err, user) {
            if (err) {
                return callback(err);
            } else if (!user) {
                var err = new Error("User not found");
                err.status = 401;
                return callback(err);
            }
            bcrypt.compare(password, user.password, function (err, result) {
                if (result === true) {
                    return callback(null, user);
                } else {
                    return callback();
                }
            });
        });
};

userSchema.statics.findMyUser = function (username, password) {
    User.findOne({ username }).exec(function (err, user) {
            if (err) {
                return  new Error("User not found")
            }
            return user
        });
}

userSchema.pre('save', function (next) {
    var user = this;
    bcrypt.hash(user.password, 10, function (err, hash) {
        if (err) {
            return next(err);
        }
        user.password = hash;
        next();
    });
});

var User = mongoose.model('user', userSchema);
module.exports = User; 
