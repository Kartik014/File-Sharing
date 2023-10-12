const dotenv = require('dotenv')
dotenv.config({ path: '../config.env' })
const { MongoClient } = require('mongodb')

const dataBase = process.env.DATABASE
const dbName = process.env.DATABASE_NAME
const collectionName = process.env.USER_COLLECTION_NAME

const client = new MongoClient(dataBase, {
    useNewUrlParser: true,
    useUnifiedTopology: true
})

exports.fetchAllDetails = async (req, res) => {

    const userNames = []

    try {

        await client.connect()

        const db = client.db(dbName)
        const collection = db.collection(collectionName)

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
        const collection = db.collection(collectionName)

        const projection = { _id: 0, password: 0 }

        const user = await collection.findOne({ name: req.body.name }, { projection })

        return user

    } catch (err) {
        console.error(err)
    } finally {
        client.close()
    }
}