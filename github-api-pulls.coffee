MAX_CONCURRENT_THREADS = 8

class GithubPulls

  constructor: (@client, @async) ->
    return

  getPulls: (ghrepo, callback) ->
    ghrepo.prs {state: 'all'}, (err, pulls, body, headers) ->
      callback err, pulls

  fetchAllPulls: (repos, callback) ->
    @async.eachLimit repos, MAX_CONCURRENT_THREADS, ((repo, cb) =>
      ghrepo = @client.repo(repo.full_name)
      @getPulls ghrepo, (err, pulls) ->
        repo.pulls = pulls
        cb err
    ), (err) ->
      callback repos

module.exports = GithubPulls
