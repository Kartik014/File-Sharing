const dotenv = require('dotenv')
const mongoose = require('mongoose')
dotenv.config({path: './config.env'})

const app = require('./app')

mongoose.connect(process.env.DATABASE, {
    useNewUrlParser: true,
}).then(() => {
    console.log("Database connected successfully")
})

const port = process.env.PORT || 8000

app.listen(port, () => {
    console.log(`Sever is running at port ${port}`);
})