ACCESS_TOKEN = '***REMOVED***' # Hal's token
TEST_ORGANISATION = 'novoda'
TEST_USERNAME = 'xrigau'

GithubApi = require './github-api'
ghapi = new GithubApi(ACCESS_TOKEN)

ghapi.init TEST_ORGANISATION, (err) ->
  console.log "READY: #{err}"
