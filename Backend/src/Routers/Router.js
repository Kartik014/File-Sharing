const express = require('express')
const Function = require('../Function')

const apiRouter = express.Router()

apiRouter
    .route('/signUp')
    .post(Function.signUp)

apiRouter
    .route('/logIn')
    .post(Function.logIn)

apiRouter
    .route('/getUser')
    .get(Function.getAllUsers)

apiRouter
    .route('/getUserDetails')
    .post(Function.getUserDetails)

apiRouter
    .route('/UploadFile')
    .post(Function.UploadFile)

module.exports = apiRouter