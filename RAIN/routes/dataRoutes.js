var express = require('express');
var router = express.Router();
var dataController = require('../controllers/dataController.js');

function requiresLogin(req, res, next){
    if(req.session && req.session.userId){
        return next();
    }else{
        var err = new Error("You must be logged in to view this page!");
        err.status = 401;
        return next(err);
    }
}

/*
 * GET
 */
router.get('/', requiresLogin, dataController.list);
router.get('/add', dataController.add);
router.get('/mydata', requiresLogin, dataController.showMine);
router.get('/myrides', requiresLogin, dataController.showRides);
router.get('/grafikaRide', dataController.grafika_ride);

/*
 * GET
 */
router.get('/:id', dataController.show);
router.get('/myrides/:id', dataController.showOneRide);



/*
 * POST
 */
router.post('/', dataController.create);
router.post('/createPhone', dataController.createPhone);

/*
 * PUT
 */
router.put('/:id', dataController.update);

/*
 * DELETE
 */
router.delete('/:id', dataController.remove);

module.exports = router;
