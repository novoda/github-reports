require 'date'
require './github'
require './github-storage'
require './github-filters'

ORGANISATION = 'novoda'
ACCESS_TOKEN = '***REMOVED***' # Hal's token

# TODO: Clear the DB to be able to refetch daily

api = Github.new ACCESS_TOKEN
api.fetch ORGANISATION
filter = Filters.new api

user = 'xrigau'
start_date = '2014-04-01'
end_date = '2014-05-01'

result = filter.repos.get_pulls.by(user).from(start_date).until(end_date)
puts "There are #{result.size} pulls by #{user} between dates #{start_date} and #{end_date}"

result.each do |pull|
  comments = api.comments pull
  puts comments.size
end
