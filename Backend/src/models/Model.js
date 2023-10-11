const mongoose = require('mongoose')

const userSchema = new mongoose.Schema({
    id: {
        type: Number,
        default: Date.now()
    },
    name: {
        type: String,
        required: true
    },
    email: {
        type: String,
        required: true
    },
    password: {
        type: String,
        required: true
    }
});

exports.UserModel = mongoose.model('UserInfo', userSchema, 'UserInfo')

const UserFileSchema = new mongoose.Schema({
    id: {
        type: Number
    },
    uploaderName: {
        type: String,
        required: true
    },
    fileName: {
        type: String,
        required: true
    },
    extension: {
        type: String,
        required: true
    },
    fileData: {
        type: String,
        required: true
    }
});

exports.UserFileModel = mongoose.model('UserFiles', UserFileSchema, 'UserFiles')