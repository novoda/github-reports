class GithubStorage

  constructor: (path) ->
    Datastore = require 'nedb'
    @db = {}
    # @db.repos = new Datastore(filename: "#{path}/repos.db", autoload: true)
    @db.timestamp = new Datastore(filename: "#{path}/timestamp.db", autoload: true)

  reposWithPulls: (callback) =>
    @db.repos.find {}, (err, repos) ->
      callback repos

  storeAll: (organisation, repos, callback) =>
    # @db.repos.insert repos, (err) =>
    #   if err then callback err
    #   else
    @db.timestamp.insert {organisation: organisation, timestamp: new Date()}, (err) ->
      callback err

  hasData: (organisation, callback) =>
    @db.timestamp.find {organisation: organisation}, (err, timestamp) ->
      callback timestamp[0]?.timestamp?

module.exports = GithubStorage
