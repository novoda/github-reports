class GithubFilters

  constructor: (@async) ->
    return

  filterOnlyReposWithPulls: (repos, callback) ->
    @async.filter repos, ((repo, cb) ->
      hasPulls = repo.pulls?.length > 0
      cb hasPulls
    ), (reposWithPullsOnly) ->
      callback reposWithPullsOnly

  filterOnlyPullsByUser: (repo, user, callback) ->
    @async.filter repo.pulls, ((pull, cb) ->
      isFromUser = pull.user.login is user
      cb isFromUser
    ), (pullsFromUserOnly) ->
      callback repo, pullsFromUserOnly

module.exports = GithubFilters
