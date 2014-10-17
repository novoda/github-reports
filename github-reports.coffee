ACCESS_TOKEN = '748cd4d55bceb80d16d8f722d3012de96cea54e5' # Hal's token
TEST_ORGANISATION = 'novoda'
TEST_USERNAME = 'xrigau'

GithubApi = require './github-api'
ghapi = new GithubApi(ACCESS_TOKEN)

ghapi.init TEST_ORGANISATION, (err) ->
  console.log "READY: #{err}"
