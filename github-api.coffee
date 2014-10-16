ACCESS_TOKEN = '***REMOVED***' # Hal's token
MAX_REPOS_PER_PAGE = 30
MAX_CONCURRENT_THREADS = 8

async = require 'async'
github = require 'octonode'
client = github.client ACCESS_TOKEN
organisation = client.org 'novoda'

getReposAtPage = (page, callback) ->
  organisation.repos {type: 'all', page: page, per_page: MAX_REPOS_PER_PAGE}, (err, repos, headers) ->
    callback err, repos

exports.fetchAllRepos = (callback) ->
  async.mapSeries [1..10], ((page, cb) ->
    getReposAtPage page, (err, repos) ->
      if repos.length < MAX_REPOS_PER_PAGE then err = "Last page found: #{page}"
      cb err, repos
  ), (err, repos) ->
    callback flatten(repos)


getPulls = (repo, callback) ->
  client.repo(repo.full_name).prs {state: 'all'}, (err, pulls, body, headers) ->
    callback err, pulls

exports.fetchAllPulls = (repos, callback) ->
  async.eachLimit repos, MAX_CONCURRENT_THREADS, ((repo, cb) ->
    getPulls repo, (err, pulls) ->
      repo.pulls = pulls
      cb err
  ), (err) ->
    callback repos


exports.onlyReposWithPulls = (repos, callback) ->
  async.filter repos, ((repo, cb) ->
    hasPulls = repo.pulls?.length > 0
    cb hasPulls
  ), (reposWithPullsOnly) ->
    callback reposWithPullsOnly


flatten = (array) ->
  flattened = []
  for element in array
    if element instanceof Array
      flattened = flattened.concat flatten element
    else
      flattened.push element
  flattened
