require 'date'
require './github'
require './github-pulls-filters'
require './github-comments-filters'


# TODO: Clear the DB to be able to refetch daily


ACCESS_TOKEN = '748cd4d55bceb80d16d8f722d3012de96cea54e5' # Hal's token
DROP_BEFORE_RUNNING = false # If set to true, drops all the tables before executing


api = Github.new ACCESS_TOKEN
pulls = PullsFilters.new api
comments = CommentsFilters.new api

organisation = 'novoda'
user = 'xrigau'
start_date = '2014-08-01'
end_date = '2014-10-31'


if (DROP_BEFORE_RUNNING)
  api.drop_all
end


prs = pulls.in(organisation).from(start_date).until(end_date)
merged_prs = pulls.full_data_for(prs).merged_by(user)
puts "User #{user} merged #{merged_prs.size} PRs between #{start_date} and #{end_date} in the #{organisation} organisation"

prs = pulls.in(organisation).by(user).from(start_date).until(end_date)
puts "User #{user} created #{prs.size} PRs between #{start_date} and #{end_date} in the #{organisation} organisation"

prs = pulls.in(organisation).by(user)
prs_comments = comments.in_all(prs).exclude_by(user).from(start_date).until(end_date)
puts "People wrote #{prs_comments.size} comments in #{user}'s PRs between #{start_date} and #{end_date} in the #{organisation} organisation"

prs = pulls.in(organisation).exclude_by(user)
prs_comments = comments.in_all(prs).by(user).from(start_date).until(end_date)
puts "User #{user} wrote #{prs_comments.size} comments in other people PRs between #{start_date} and #{end_date} in the #{organisation} organisation"

prs = pulls.in(organisation).from(start_date).until(end_date)
prs_comments = comments.in_all(prs).by(user).from(start_date).until(end_date)
puts "User #{user} commented on all PR's #{prs_comments.size} times between #{start_date} and #{end_date} in the #{organisation} organisation"
