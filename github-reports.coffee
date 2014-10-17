ACCESS_TOKEN = '***REMOVED***' # Hal's token
TEST_ORGANISATION = 'novoda'
TEST_USERNAME = 'xrigau'

GithubApi = require './github-api'
ghapi = new GithubApi(ACCESS_TOKEN)

ghapi.reposWithPulls TEST_ORGANISATION, (repos) ->
  console.log "TOTAL REPOS WITH PULLS: #{repos.length}"

ghapi.pullsFromUser TEST_ORGANISATION, TEST_USERNAME, (repo, pulls) ->
  console.log "TOTAL PULLS FROM #{TEST_USERNAME} in #{repo.name}: #{pulls.length}"
