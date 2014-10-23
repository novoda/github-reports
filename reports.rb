require 'date'
require './github'
require './github-storage'
require './github-filters'

ORGANISATION = 'novoda'
ACCESS_TOKEN = '748cd4d55bceb80d16d8f722d3012de96cea54e5' # Hal's token

def filter()
  api = Github.new ACCESS_TOKEN
  api.fetch ORGANISATION
  Filters.new api
end

result = filter.repos.with_pulls_by('xrigau')
puts result.size
