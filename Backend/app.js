const express = require('express')
const app = express()
const apiRouter = require('./src/Routers/Router')
const cors = require('cors')

app.use(express.json({ limit: '150mb' }))
app.use(cors())
app.use("/api", apiRouter)

module.exports = app