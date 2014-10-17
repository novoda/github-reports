class GithubApi

  constructor: (accessToken) ->
    GithubHttpApi = require './github-http-api-requests'
    @ghrequest = new GithubHttpApi accessToken

    GithubStorage = require './github-storage'
    @ghstorage = new GithubStorage 'github_db'

  reposWithPullsFromApi: (organisation, callback) =>
    @ghrequest.fetchReposWithPulls organisation, (repos) ->
      callback repos

  init: (organisation, callback) ->
    @ghstorage.hasData organisation, (result) =>
      if result then callback null
      else
        @reposWithPullsFromApi organisation, (repos) =>
          @ghstorage.storeAll organisation, repos, (err) ->
            callback err

module.exports = GithubApi
