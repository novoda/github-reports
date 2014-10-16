ACCESS_TOKEN = '***REMOVED***' # Hal's token
ORGANISATION = 'novoda'

GithubApi = require './github-api'
ghapi = new GithubApi(ACCESS_TOKEN, ORGANISATION)

# Datastore = require 'nedb'
# db = {}
# db.repos = new Datastore()

ghapi.reposWithPulls ORGANISATION, (repos) ->
  console.log "TOTAL REPOS WITH PULLS: #{repos.length}"
