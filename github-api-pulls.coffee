MAX_PULLS_PER_PAGE = 100
MAX_PAGES_TO_CHECK = 5

class GithubPulls

  constructor: (@client, @async, @helper) ->
    return

  getPullsAtPage: (ghrepo, page, callback) ->
    ghrepo.prs {state: 'all', page: page, per_page: MAX_PULLS_PER_PAGE}, (err, pulls, headers) ->
      # if headers?.link then console.log "FOUND THE LINK FOR REPO #{ghrepo.name} - #{headers?.link}"
      callback err, pulls

  fetchAllPulls: (repo, callback) ->
    ghrepo = @client.repo(repo.full_name)
    @async.mapSeries [1..MAX_PAGES_TO_CHECK], ((page, cb) =>
      @getPullsAtPage ghrepo, page, (err, pulls) ->
        if pulls?.length < MAX_PULLS_PER_PAGE then err = "Last page found: #{page}"
        cb err, pulls
    ), (err, pulls) =>
      callback @helper.flatten(pulls)

  fetchAllPullsForRepos: (repos, callback) ->
    @async.each repos, ((repo, cb) =>
      @fetchAllPulls repo, (pulls) ->
        repo.pulls = pulls
        cb(null)
    ), (err) ->
      callback repos

module.exports = GithubPulls
