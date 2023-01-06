var express = require('express');
var router = express.Router();
var userController = require('../controllers/userController.js');
const multer = require('multer')
const storageEngine = multer.diskStorage({
    destination: "./files",
    filename: (req, file, cb) => {
        cb(null, `${Date.now()}-${file.originalname}`);
      },
    });

const multerFilter = (req, file, cb) => {
    if (file.mimetype.split("/")[1] === "png" || file.mimetype.split("/")[1] === "jpg" || file.mimetype.split("/")[1] === "jpeg") {
        cb(null, true);
    } else {
        cb(new Error("Not an image!!"), false);
    }
};
  
const upload = multer({
        storage: storageEngine,
        fileFilter: multerFilter,
        limits: { fileSize: 50000000 },
    });
/*
 * GET
 */
router.get('/', userController.list);
router.get('/login', userController.showLogin);
router.get('/register', userController.showRegister);
router.get('/logout', userController.logout);
router.get('/profile', userController.profile);
router.get('/login_photo', userController.show_login_register);
router.get('/login_steg', userController.show_save_in_steg);

/*
 * GET
 */
router.get('/:id', userController.show);

/*
 * POST
 */
router.post('/', userController.create);
router.post('/login', userController.login);
router.post('/loginphone', userController.loginPhone);
router.post('/send_photo', upload.single('photo'), userController.check_photo);
router.post('/login_steg_save', upload.single('photo'), userController.save_in_steg);


/*
 * PUT
 */
router.put('/:id', userController.update);

/*
 * DELETE
 */
router.delete('/:id', userController.remove);

module.exports = router;
