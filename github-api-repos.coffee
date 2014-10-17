MAX_REPOS_PER_PAGE = 30
MAX_PAGES_TO_CHECK = 10

class GithubRepos

  constructor: (@client, @async, @helper) ->
    return

  getReposAtPage: (ghorg, page, callback) ->
    ghorg.repos {type: 'private', page: page, per_page: MAX_REPOS_PER_PAGE}, (err, repos, headers) ->
      callback err, repos

  fetchAllRepos: (organisation, callback) ->
    ghorg = @client.org(organisation)
    @async.mapSeries [1..MAX_PAGES_TO_CHECK], ((page, cb) =>
      @getReposAtPage ghorg, page, (err, repos) ->
        if repos?.length < MAX_REPOS_PER_PAGE then err = "Last page found: #{page}"
        cb err, repos
    ), (err, repos) =>
      callback @helper.flatten(repos)

module.exports = GithubRepos
