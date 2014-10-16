MAX_CONCURRENT_THREADS = 8
MAX_PULLS_PER_PAGE = 30

class GithubPulls

  constructor: (@client, @async, @helper) ->
    return

  getPullsAtPage: (ghrepo, page, callback) ->
    ghrepo.prs {state: 'all', page: page, per_page: MAX_PULLS_PER_PAGE}, (err, pulls, headers) ->
      callback err, pulls

  fetchAllPulls: (repo, callback) ->
    ghrepo = @client.repo(repo.full_name)
    @async.mapSeries [1..20], ((page, cb) =>
      @getPullsAtPage ghrepo, page, (err, pulls) ->
        if pulls.length < MAX_PULLS_PER_PAGE then err = "Last page found: #{page}"
        cb err, pulls
    ), (err, pulls) =>
      callback @helper.flatten(pulls)

  fetchAllPullsForRepos: (repos, callback) ->
    @async.eachLimit repos, MAX_CONCURRENT_THREADS, ((repo, cb) =>
      @fetchAllPulls repo, (pulls) ->
        repo.pulls = pulls
        cb null
    ), (err) ->
      callback repos

  filterByUser: (pulls, user, callback) ->
    @async.filter pulls, ((pull, cb) ->
      isFromUser = pull.user.login is user
      cb isFromUser
    ), (pullsFromUserOnly) ->
      callback pullsFromUserOnly

module.exports = GithubPulls
