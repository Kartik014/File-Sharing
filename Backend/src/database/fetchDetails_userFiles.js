const dotenv = require('dotenv')
dotenv.config({ path: '../config.env' })
const { MongoClient } = require('mongodb')

const dataBase = process.env.DATABASE
const dbName = process.env.DATABASE_NAME
const collectionName = process.env.FILE_COLLECTION_NAME

const client = new MongoClient(dataBase, {
    useNewUrlParser: true,
    useUnifiedTopology: true
})

exports.fetchFileName = async (req, res) => {

    const receiverId = req.body.receiverID

    const fileNames = []

    try {

        await client.connect()

        const db = client.db(dbName)
        const collection = db.collection(collectionName)

        const hasReceiverId = await collection.findOne({ receiverID: receiverId })
        
        if (hasReceiverId.receiverID) {
            console.log("Function Called")
            console.log(receiverId)
            await client.connect()

            const db = client.db(dbName)
            const collection = db.collection(collectionName)

            const cursor = collection.find({ uploaderName: req.body.name, receiverID: receiverId })

            await cursor.forEach(file => {
                fileNames.push(file.fileName)
            })

            return fileNames
        } else {
            return []
        }

    } catch (err) {
        console.error(err)
    } finally {
        client.close()
    }
}

exports.downloadFile = async (req, res) => {

    try {

        await client.connect()

        const db = client.db(dbName)
        const collection = db.collection(collectionName)

        const projection = { uploaderName: 0, fileName: 0, extension: 0, id: 0, _id: 0 }

        const fileData = await collection.findOne({ uploaderName: req.body.name, fileName: req.body.fileName, id: req.body.senderID, receiverID: req.body.receiverID }, { projection })

        return fileData

    } catch (err) {
        console.error(err)
    } finally {
        client.close()
    }
}