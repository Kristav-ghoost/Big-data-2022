var express = require('express');
var router = express.Router();
var dataController = require('../controllers/dataController.js');

/*
 * GET
 */
router.get('/', dataController.list);
router.get('/add', dataController.add);

/*
 * GET
 */
router.get('/:id', dataController.show);

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
