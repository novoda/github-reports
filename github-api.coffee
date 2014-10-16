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

  reposWithPulls: (organisation, callback) ->
    @ghrepos.fetchAllRepos organisation, (reposNoPulls) =>
      @ghpulls.fetchAllPullsForRepos reposNoPulls, (reposWithPulls) =>
        @ghrepos.onlyReposWithPulls reposWithPulls, (repos) ->
          callback repos

  pullsFromUser: (organisation, user, callback) ->
    @reposWithPulls organisation, (repos) =>
      for repo in repos
        @ghpulls.filterByUser repo.pulls, user, callback

module.exports = GithubApi
