require 'date'
require './github'
require './github-storage'
require './github-filters'

ORGANISATION = 'novoda'
ACCESS_TOKEN = '***REMOVED***' # Hal's token

def filter()
  api = Github.new ACCESS_TOKEN
  api.fetch ORGANISATION

  repos = api.repos ORGANISATION
  repos.each { |repo| repo['pulls'] = api.pulls(repo) }
  Filters.new repos
end

result = filter.only.pulls_by('xrigau')
puts result.size
