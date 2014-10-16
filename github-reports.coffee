ACCESS_TOKEN = '748cd4d55bceb80d16d8f722d3012de96cea54e5' # Hal's token
TEST_ORGANISATION = 'novoda'
TEST_USERNAME = 'xrigau'

GithubApi = require './github-api'
ghapi = new GithubApi(ACCESS_TOKEN)

# Datastore = require 'nedb'
# db = {}
# db.repos = new Datastore()

ghapi.reposWithPulls TEST_ORGANISATION, (repos) ->
  console.log "TOTAL REPOS WITH PULLS: #{repos.length}"

ghapi.pullsFromUser TEST_ORGANISATION, TEST_USERNAME, (pulls) ->
  console.log "TOTAL PULLS FROM #{TEST_USERNAME}: #{pulls.length}"
