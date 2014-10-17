class GithubApi

  constructor: (accessToken) ->
    GithubHttpApi = require './github-http-api-requests'
    @ghrequest = new GithubHttpApi(accessToken)
    Datastore = require 'nedb'
    db = {}
    db.repos = new Datastore(filename: 'github_db/repos.db', autoload: true)

  reposWithPulls: (organisation, callback) ->
    @ghrequest.fetchReposWithPulls organisation, (repos) ->


  # pullsFromUser: (organisation, user, callback) ->
  #   @reposWithPulls organisation, (repos) =>
  #     for repo in repos
  #       @ghfilters.filterOnlyPullsByUser repo, user, callback

module.exports = GithubApi
