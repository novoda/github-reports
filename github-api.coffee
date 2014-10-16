MAX_REPOS_PER_PAGE = 30
MAX_CONCURRENT_THREADS = 8

class GithubApi

  constructor: (accessToken) ->
    github = require 'octonode'
    client = github.client accessToken
    async = require 'async'

    GithubRepos = require './github-api-repos'
    @ghrepos = new GithubRepos(client, async)

    GithubPulls = require './github-api-pulls'
    @ghpulls = new GithubPulls(client, async)

  reposWithPulls: (organisation, callback) ->
    @ghrepos.fetchAllRepos organisation, (reposNoPulls) =>
      @ghpulls.fetchAllPulls reposNoPulls, (reposWithPulls) =>
        @ghrepos.onlyReposWithPulls reposWithPulls, (repos) ->
          callback repos

module.exports = GithubApi
