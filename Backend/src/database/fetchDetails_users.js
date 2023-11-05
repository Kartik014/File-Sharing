const dotenv = require('dotenv')
dotenv.config({ path: '../config.env' })
const { MongoClient } = require('mongodb')
const Model = require('./../models/Model')

const dataBase = process.env.DATABASE
const dbName = process.env.DATABASE_NAME
const userCollectionName = process.env.USER_COLLECTION_NAME
const connectionCollectionName = process.env.CONNECTION_COLLECTION_NAME

const client = new MongoClient(dataBase, {
    useNewUrlParser: true,
    useUnifiedTopology: true
})

exports.fetchAllDetails = async (req, res) => {

    const userNames = []

    try {

        await client.connect()

        const db = client.db(dbName)
        const collection = db.collection(userCollectionName)

        const cursor = collection.find({})

        await cursor.forEach(user => {
            userNames.push(user.name)
        })

        return userNames

    } catch (err) {
        console.error(err)
    } finally {
        client.close()
    }
}

exports.getUserInfo = async (req, res) => {

    try {

        await client.connect()
        console.log("USER NAME: ", req.body.name)

        const db = client.db(dbName)
        const collection = db.collection(userCollectionName)

        const projection = { _id: 0, password: 0 }

        const user = await collection.findOne({ name: req.body.name }, { projection })

        return user

    } catch (err) {
        console.error(err)
    } finally {
        client.close()
    }
}

exports.getConnectingUsers = async (req, res) => {

    const userNames = []

    try {

        await client.connect()

        const db = client.db(dbName)
        const collection = db.collection(connectionCollectionName)

        const cursor = collection.find({ receiverID: req.body.receiverID })

        await cursor.forEach(user => {
            userNames.push(user.requestSenderName)
        })

        return userNames

    } catch (err) {

        console.log(err)
    }
}

exports.getConnectionInfo = async (req, res) => {

    try {

        await client.connect()

        const db = client.db(dbName)
        const connectionCollection = db.collection(connectionCollectionName)

        const connection = await connectionCollection.findOne({ senderID: req.body.senderID, receiverID: req.body.receiverID })

        const filter = { _id: connection._id }

        if (req.body.connectionStatus == "Accepted") {

            if (connection) {

                if (connection.connectionStatus === "Pending") {
                    const updateQuery = {
                        $set: { connectionStatus: req.body.connectionStatus },
                    }
                    await connectionCollection.updateOne(filter, updateQuery)
                }

                const receiver = await Model.UserModel.findOne({ id: req.body.receiverID });
                if (receiver) {
                    receiver.connections.push(req.body.senderID);
                    await receiver.save();
                    if (receiver.connections.includes(req.body.senderID)) {
                        const sender = await Model.UserModel.findOne({ id: req.body.senderID });
                        sender.connections.push(req.body.receiverID);
                        await sender.save();
                    }
                }

                await connectionCollection.deleteOne(filter)
                return req.body.connectionStatus

            } else {
                console.error("Connection not found!!.")
                return null

            }
        } else if (req.body.connectionStatus == "Rejected") {

            await connectionCollection.deleteOne(filter)
            return req.body.connectionStatus

        } else {

            console.error("Connection not found.")
            return null

        }

    } catch (err) {
        console.error(err)
    } finally {
        client.close()
    }
}

exports.getConnectedUsers = async (req, res) => {

    try {

        await client.connect()

        const db = client.db(dbName)
        const collection = db.collection(userCollectionName)

        const user = await collection.findOne({ id: req.body.id })

        const connectedUsersIDs = await user.connections

        return connectedUsersIDs

    } catch (err) {
        console.log(err)
    } finally {
        client.close()
    }
}