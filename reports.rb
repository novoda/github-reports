require 'date'
require './github'
require './github-storage'

ORGANISATION = 'novoda'
ACCESS_TOKEN = '***REMOVED***' # Hal's token

api = Github.new ACCESS_TOKEN
api.fetch ORGANISATION

repos = api.repos ORGANISATION
puts "I've got #{repos.size}"
