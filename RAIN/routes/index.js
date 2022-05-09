var express = require('express');
var router = express.Router();

/* GET home page. */
router.get('/', function(req, res, next) {
  res.render('index', { title: 'Express', ide: req.session.userId, userName: req.session.userName });
});

module.exports = router;
