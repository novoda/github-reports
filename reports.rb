require 'date'
require './github'
require './github-storage'

ORGANISATION = 'novoda'
ACCESS_TOKEN = '748cd4d55bceb80d16d8f722d3012de96cea54e5' # Hal's token

api = Github.new ACCESS_TOKEN
api.fetch ORGANISATION

repos = api.repos ORGANISATION
puts "I've got #{repos.size}"
