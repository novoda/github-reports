class GithubApi

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

  reposWithPulls: (organisation, callback) ->
    @ghrepos.fetchAllRepos organisation, (reposNoPulls) =>
      @ghpulls.fetchAllPullsForRepos reposNoPulls, (reposWithPulls) =>
        @ghfilters.filterOnlyReposWithPulls reposWithPulls, (repos) ->
          callback repos

  pullsFromUser: (organisation, user, callback) ->
    @reposWithPulls organisation, (repos) =>
      for repo in repos
        @ghfilters.filterOnlyPullsByUser repo, user, callback

module.exports = GithubApi
