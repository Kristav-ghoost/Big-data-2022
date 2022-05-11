var UserModel = require('../models/userModel.js');

/**
 * userController.js
 *
 * @description :: Server-side logic for managing users.
 */
module.exports = {

    /**
     * userController.list()
     */
    list: function (req, res) {
        UserModel.find(function (err, users) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when getting user.',
                    error: err
                });
            }

            return res.json(users);
        });
    },

    showLogin: function(req, res){
        return res.render('user/login')
    },

    showRegister: function(req, res){
        return res.render('user/register')
    },

    //Login
    login: function(req, res, next){
        UserModel.authenticate(req.body.username, req.body.password, function(error, user){
            if(error || !user){
                var err = new Error("Wrong username or password");
                err.status = 401;
                return next(err);
            }else{
                req.session.userId = user._id;
                req.session.userName = user.username;
                return res.redirect("/");
                //return res.status(200).json(user);
            }
        });
    },

    //Login phone
    loginPhone: function(req, res, next){
        UserModel.authenticate(req.body.username, req.body.password, function(error, user){
            if(error || !user){
                var err = new Error("Wrong username or password");
                err.status = 401;
                return next(err);
            }else{
                //req.session.userId = user._id;
                //req.session.userName = user.username;
                //return res.redirect("/");
                return res.status(200).json(user);
            }
        });
    },

    //Logout
    logout: function(req, res, next){
        if(req.session){
            req.session.destroy(function(err){
                if(err){
                    return next(err);
                }else{
                    return res.redirect('/');
                }
            });
        }
    },

    //Profile
    profile: function(req, res, next){
        var id = req.session.userId;
        UserModel.findById(req.session.userId).exec(function(error, user){
            if(error){
                return next(error);
            }else{
                if(user === null){
                    var err = new Error("Not authenticated!");
                    err.status = 401;
                    return next(err);
                }else{
                    var data = [];
                    data.users = user;
                    data.id = id;
                    res.render('user/profile', data);
                }
            }
        })
    },



    /**
     * userController.show()
     */
    show: function (req, res) {
        var id = req.params.id;

        UserModel.findOne({_id: id}, function (err, user) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when getting user.',
                    error: err
                });
            }

            if (!user) {
                return res.status(404).json({
                    message: 'No such user'
                });
            }

            return res.json(user);
        });
    },

    /**
     * userController.create()
     */
    create: function (req, res) {
        var user = new UserModel({
			username : req.body.username,
			password : req.body.password,
			email : req.body.email
        });
        /*
        UserModel.checkNameEmail(req.body.username, req.body.email, function(error, user){
            if(user == null){
                console.log("sem pride");
                user.save(function (err, user) {
                    if (err) {
                        return res.status(500).json({
                            message: 'Error when creating user',
                            error: err
                        });
                    }
                    
                    return res.status(201).json(user);
                });
            }else{
                return res.status(404).json({ error: 'Unauthorized' });
            }
        })*/

        user.save(function (err, user) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when creating user',
                    error: err
                });
            }
            
            return res.redirect('/users/login');
        });
    },

    /**
     * userController.update()
     */
    update: function (req, res) {
        var id = req.params.id;

        UserModel.findOne({_id: id}, function (err, user) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when getting user',
                    error: err
                });
            }

            if (!user) {
                return res.status(404).json({
                    message: 'No such user'
                });
            }

            user.username = req.body.username ? req.body.username : user.username;
			user.password = req.body.password ? req.body.password : user.password;
			user.email = req.body.email ? req.body.email : user.email;
			
            user.save(function (err, user) {
                if (err) {
                    return res.status(500).json({
                        message: 'Error when updating user.',
                        error: err
                    });
                }

                return res.json(user);
            });
        });
    },

    /**
     * userController.remove()
     */
    remove: function (req, res) {
        var id = req.params.id;

        UserModel.findByIdAndRemove(id, function (err, user) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when deleting the user.',
                    error: err
                });
            }

            return res.status(204).json();
        });
    }
};
