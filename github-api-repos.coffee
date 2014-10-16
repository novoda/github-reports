MAX_REPOS_PER_PAGE = 30

class GithubRepos

  constructor: (@client, @async, @helper) ->
    return

  getReposAtPage: (ghorg, page, callback) ->
    ghorg.repos {type: 'all', page: page, per_page: MAX_REPOS_PER_PAGE}, (err, repos, headers) ->
      callback err, repos

  fetchAllRepos: (organisation, callback) ->
    ghorg = @client.org(organisation)
    @async.mapSeries [1..10], ((page, cb) =>
      @getReposAtPage ghorg, page, (err, repos) ->
        if repos.length < MAX_REPOS_PER_PAGE then err = "Last page found: #{page}"
        cb err, repos
    ), (err, repos) =>
      callback @helper.flatten(repos)

  onlyReposWithPulls: (repos, callback) ->
    @async.filter repos, ((repo, cb) ->
      hasPulls = repo.pulls?.length > 0
      cb hasPulls
    ), (reposWithPullsOnly) ->
      callback reposWithPullsOnly

module.exports = GithubRepos
