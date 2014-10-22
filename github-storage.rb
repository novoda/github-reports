require 'mongo'
include Mongo
require 'sawyer'

class Storage

  def initialize()
    client = MongoClient.new
    db = client['github-reports']
    @repos = db['repos']
    @pulls = db['pulls']
  end

  def contains(organisation)
    return @repos.count > 0
  end

  def save_repo(organisation_name, repo)
    repo_json = JSON.generate repo
    @repos.insert({:organisation => organisation_name, :repo => repo_json})
  end

  def save_pull(repo, pull)
    pull_json = JSON.generate pull
    @pulls.insert({:repo => repo[:full_name], :pull => pull_json})
  end

  def repos(organisation)
    repos = @repos.find({:organisation => organisation})
    repos.to_a.map! do |repo|
      JSON.parse repo['repo']
    end
  end

  def pulls(repo)
    pulls = @pulls.find({:repo => repo[:full_name]})
    pulls.to_a.map! do |pull|
      JSON.parse pull['pull']
    end
  end

end
