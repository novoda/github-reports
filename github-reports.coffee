ghapi = require './github-api'

ghapi.fetchAllRepos (reposNoPulls) ->
  ghapi.fetchAllPulls reposNoPulls, (reposWithPulls) ->
    ghapi.onlyReposWithPulls reposWithPulls, (repos) ->
      console.log "TOTAL REPOS WITH PULLS: #{repos.length}"
