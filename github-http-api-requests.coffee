class GithubHttpApi

  constructor: (accessToken) ->
    github = require 'octonode'
    client = github.client accessToken
    async = require 'async'
    helper = require './helper'

    GithubRepos = require './github-api-repos'
    @ghrepos = new GithubRepos(client, async, helper)

    GithubPulls = require './github-api-pulls'
    @ghpulls = new GithubPulls(client, async, helper)

    GithubFilters = require './github-api-filters'
    @ghfilters = new GithubFilters(async)

  fetchReposWithPulls: (organisation, callback) ->
    @ghrepos.fetchAllRepos organisation, (reposNoPulls) =>
      @ghpulls.fetchAllPullsForRepos reposNoPulls, (reposWithPulls) =>
        @ghfilters.filterOnlyReposWithPulls reposWithPulls, (repos) ->
          callback repos

module.exports = GithubHttpApi
