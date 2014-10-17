class GithubHttpApi

  constructor: (accessToken) ->
    github = require 'octonode'
    client = github.client accessToken
    helper = require './helper'
    async = require 'async'

    GithubRepos = require './github-api-repos'
    @ghrepos = new GithubRepos(client, async, helper)

    GithubPulls = require './github-api-pulls'
    @ghpulls = new GithubPulls(client, async, helper)

    GithubFilters = require './github-api-filters'
    @ghfilters = new GithubFilters(async)

  fetchReposWithPulls: (organisation, callback) ->
    @ghrepos.fetchAllRepos organisation, (reposNoPulls) =>
      console.log "Got all these repos in TOTAL: #{reposNoPulls.length}"
      @ghfilters.filterOnlyReposWithIssuesNoForks reposNoPulls, (reposWithIssues) =>
        console.log "Got all these repos in with issues: #{reposWithIssues.length}"
        @ghpulls.fetchAllPullsForRepos reposWithIssues, (reposWithPulls) =>
          console.log "And now I've also got all the Pulls for the repos"
          @ghfilters.filterOnlyReposWithPulls reposWithPulls, (repos) ->
            console.log "Finally, only #{repos.length} Repos with Pulls"
            callback repos

module.exports = GithubHttpApi
