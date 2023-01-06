var DataModel = require('../models/dataModel.js');
const dayjs = require('dayjs');

/**
 * dataController.js
 *
 * @description :: Server-side logic for managing datas.
 */
module.exports = {

    /**
     * dataController.list()
     */
    list: function (req, res) {
        /*DataModel.find(function (err, datas) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when getting data.',
                    error: err
                });
            }

            return res.json(datas);
        });*/
        var id = req.session.userId;

        DataModel.find().populate('author').then(datas => {
            var data = [];
            data.dat = datas;
            data.id = id;
            return res.render('data/show', data);
            //return res.json(questions);
        })
    },

    //Prikazi moje
    showMine: function (req, res) {
        var id = req.session.userId;

        DataModel.find().populate('author').then(datas => {
            var data = [];
            data.dat = datas;
            data.id = id;
            return res.render('data/showMine', data);
        })
    },

    //Prikazi 
    showRides: function (req, res) {
        var id = req.session.userId;

        DataModel.find().populate('author').then(datas => {
            var data = [];
            data.dat = datas;
            data.id = id;
            return res.render('data/rides', data);
        })
    },
    
    //Prikazi chart
    showOneRide: function (req, res) {
        var id = req.session.userId;
        var idR = req.params.id;

        DataModel.find().populate('author').then(datas => {
            var data = [];
            data.dat = datas;
            data.id = id;
            data.idData = idR.substring(1);
            return res.render('data/showRide', data);
        })
    },

    //Dodaj
    add: function (req, res) {
        return res.render('data/add');
    },

    /**
     * dataController.show()
     */
    show: function (req, res) {
        var id = req.params.id;

        DataModel.findOne({_id: id}, function (err, data) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when getting data.',
                    error: err
                });
            }

            if (!data) {
                return res.status(404).json({
                    message: 'No such data'
                });
            }

            return res.json(data);
        });
    },

    /**
     * dataController.create()
     */
    create: function (req, res) {
        var today = dayjs();
        var data = new DataModel({
            data: req.body.data,
            time: today.format("YYYY-MM-DD h:mm:ss"),
            author: req.session.userId
        });

        data.save(function (err, data) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when creating data',
                    error: err
                });
            }

            return res.redirect('/data');
        });
    },

    createPhone: function (req, res) {
        var today = dayjs();
        var data = new DataModel({
            data: req.body.data,
            time: today.format("DD-MM-YYYY HH:mm:ss"),
            author: req.body.author
        });

        data.save(function (err, data) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when creating data',
                    error: err
                });
            }

            return res.redirect('/data');
        });
    },

    /**
     * dataController.update()
     */
    update: function (req, res) {
        var id = req.params.id;

        DataModel.findOne({_id: id}, function (err, data) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when getting data',
                    error: err
                });
            }

            if (!data) {
                return res.status(404).json({
                    message: 'No such data'
                });
            }

            data.latitude = req.body.latitude ? req.body.latitude : data.latitude;
            data.longitude = req.body.longitude ? req.body.longitude : data.longitude;

            data.save(function (err, data) {
                if (err) {
                    return res.status(500).json({
                        message: 'Error when updating data.',
                        error: err
                    });
                }

                return res.json(data);
            });
        });
    },

    /**
     * dataController.remove()
     */
    remove: function (req, res) {
        var id = req.params.id;

        DataModel.findByIdAndRemove(id, function (err, data) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when deleting the data.',
                    error: err
                });
            }

            return res.status(204).json();
        });
    },

    grafika_ride: (req, res) => {
        var id = '63b7fe17716a87601cd5bb93'
        DataModel.findById(id, function (err, data) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when finding the data.',
                    error: err
                });
            }
            return res.status(200).send(data.data);
        });
    }
};
