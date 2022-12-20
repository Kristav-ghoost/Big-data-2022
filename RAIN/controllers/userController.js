var UserModel = require('../models/userModel.js');
var fs = require('fs');
const { exec } = require("child_process");
var bcrypt = require('bcrypt');
const path = require("path");

function execShellCommand(cmd) {
    return new Promise((resolve, reject) => {
     exec(cmd, (error, stdout, stderr) => {
      if (error) {
       console.warn(error);
      }
      resolve(stdout? stdout : stderr);
     });
    });
   }

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

    showLogin: function (req, res) {
        return res.render('user/login')
    },

    showRegister: function (req, res) {
        return res.render('user/register')
    },

    //Login
    login: function (req, res, next) {
        UserModel.authenticate(req.body.username, req.body.password, function (error, user) {
            if (error || !user) {
                var err = new Error("Wrong username or password");
                err.status = 401;
                return next(err);
            } else {
                req.session.userId = user._id;
                req.session.userName = user.username;
                return res.redirect("/");
            }
        });
    },

    //docker run -it --rm -v /home/projekt/projekt/ORV:/app app:1.0 leo.jpg
    login_2fa: function (req, res) {
        try {
            let data = req.body.data
            let user = req.body.user
            let name = user + '_' + Date.now()+'.png'
            const path = '/home/projekt/projekt/ORV/' + name
            fs.writeFileSync(path, data,  {encoding: 'base64'});

            exec("docker run -i --rm -v /home/projekt/projekt/ORV:/app app:1.0 " + name, (error, stdout, stderr) => {
                if (error) {
                    console.log(`error: ${error.message}`);
                    return res.status(400).json(error.message);
                }
                if (stderr) {
                    return res.status(400).json(stderr);
                }
                console.log(`stdout: ${stdout}`);
                return res.status(200).json({"name": stdout.replace(/\n/g, '').replace(/"/g, '')});
            });

        } catch (e) {
            console.log(e)
        }
    },

    //Login phone (za NPO)
    loginPhone: function (req, res, next) {
        UserModel.authenticate(req.body.username, req.body.password, function (error, user) {
            if (error || !user) {
                var err = new Error("Wrong username or password");
                err.status = 401;
                return next(err);
            } else {
                return res.status(200).json(user);
            }
        });
    },

    //Logout
    logout: function (req, res, next) {
        if (req.session) {
            req.session.destroy(function (err) {
                if (err) {
                    return next(err);
                } else {
                    return res.redirect('/');
                }
            });
        }
    },

    //Profile
    profile: function (req, res, next) {
        var id = req.session.userId;
        UserModel.findById(req.session.userId).exec(function (error, user) {
            if (error) {
                return next(error);
            } else {
                if (user === null) {
                    var err = new Error("Not authenticated!");
                    err.status = 401;
                    return next(err);
                } else {
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
            username: req.body.username,
            password: req.body.password,
            email: req.body.email
        });

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
    },

    show_login_register: async (req, res) => {
        return res.render('user/login_photo')
    },

    show_save_in_steg: async (req, res) => {
        return res.render('user/save_steg')
    },

    check_photo: async (req, res) => {
        try {
            const run = await execShellCommand('docker run -i -v /home/kristav/Desktop/3_letnik/Projekt/RAIN/files:/app extract:1.0 ' + req.file.filename)
            const array = await run.split("*")
            const user = await UserModel.findOne({username: array[0]})
            if (user){
                if (bcrypt.compare(array[1].replace(/\r?\n|\r/g, ""), user.password)){
                    req.session.userId = user._id;
                    req.session.userName = user.username;
                    return res.redirect("/");
                }
            } else {
                res.status(401).send({err: "User not found"})
            }
        } catch (err){
            res.json({err})
        }
    },

    save_in_steg: async (req, res) => {
        try {   
            await exec("docker run -i -v /home/kristav/Desktop/3_letnik/Projekt/RAIN/files:/app hide:1.0 " + req.file.filename + ' ' + req.body.username+'*'+req.body.password)
            return res.download("/home/kristav/Desktop/3_letnik/Projekt/RAIN/files/stegimage.png")
        } catch(e){
            res.status(401).json({err: e})
        }
    }

};