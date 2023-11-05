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

apiRouter
    .route('/getFiles')
    .post(Function.getFileDetails)

apiRouter
    .route('/downloadFile')
    .post(Function.downloadFile)

apiRouter
    .route('/requestConnection')
    .post(Function.connection_Request)

apiRouter
    .route('/getConnectingNames')
    .post(Function.getConnectingNames)

apiRouter
    .route('/responseConnection')
    .post(Function.connection_Response)

apiRouter
    .route('/getConnectedUsers')
    .post(Function.getConnectedUsers)

module.exports = apiRouter