const bcrypt = require('bcrypt')
const Model = require('./models/Model')
const user_database = require('./database/fetchDetails_users')
const file_database = require('./database/fetchDetails_userFiles')

exports.signUp = async (req, res) => {

    try {
        const hasedPassword = await bcrypt.hash(req.body.password, 10)

        const newUser = new Model.UserModel({
            name: req.body.name,
            email: req.body.email,
            password: hasedPassword
        })

        await newUser.save()

        res.status(200).json({
            status: "user registered successfully",
            userInfo: newUser,
            message: "Account created successsfully"
        })

    } catch (err) {

        res.status(401).json({
            message: err.body
        })

    }
}

exports.logIn = async (req, res) => {

    try {
        const { email, password } = req.body

        const user = await Model.UserModel.findOne({ email })

        if (!user) {
            res.status(400).json({
                status: "Failed",
                message: "User doesn't exist"
            })
        }

        const matchPassword = await bcrypt.compare(password, user.password)

        if (!matchPassword) {
            res.status(402).json({
                status: "Failed",
                message: "Password doesn't match"
            })
        }

        res.status(200).json({
            status: "User logged In successfully",
            message: "LogIn Successful",
            name: user.name
        })

    } catch (err) {
        
        res.status(401).json({
            message: err
        })

    }
}

exports.getAllUsers = async (req, res) => {

    try {
        const userNames = await user_database.fetchAllDetails()

        res.status(200).json({
            userArray: userNames
        })

    } catch (err) {

        res.status(403).json({
            message: err
        })

    }
}

exports.getUserDetails = async (req, res) => {

    try {
        const user = await user_database.getUserInfo(req)

        res.status(200).json({
            status: "Details fetched successfully",
            userArray: user
        })

    } catch (err) {

        res.status(403).json({
            status: err
        })

    }
}

exports.UploadFile = async (req, res) => {

    try {

        const user = await user_database.getUserInfo(req)

        const newFile = new Model.UserFileModel({
            id: user.id,
            uploaderName: req.body.name,
            fileName: req.body.fileName,
            extension: req.body.extension,
            fileData: req.body.fileData
        })

        await newFile.save({ wtimeout: 60000 })

        res.status(200).json({
            status: "file Saved",
            message: user.id
        })
        
    } catch (err) {

        res.status(404).json({
            status: err.message
        })

    }
}

exports.getFileDetails = async(req, res) => {
    console.log("function called")
    console.log(req.body.name)
    try {

        const fileName = await file_database.fetchFileName(req)

        res.status(200).json({
            status: "File Fetched",
            fileArray: fileName
        })

    } catch (err) {

        res.status(405).json({
            status: err
        })

    }
}

exports.downloadFile = async(req, res) => {
    
    try {
        const fileData = await file_database.downloadFile(req)

        console.log("Data:",fileData)
        res.status(200).json({
            status: "File download successful",
            data: fileData
        })

    } catch (err) {

        res.status(405).json({
            status: err
        })

    }
}