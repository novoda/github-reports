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

user = 'xrigau'
start_date = '2014-10-13'
end_date = '2014-10-14'

result = filter.repos.with_pulls_by(user).from(start_date).until(end_date)
puts "There are #{result.size} repos by #{user} between dates #{start_date} and #{end_date}"
