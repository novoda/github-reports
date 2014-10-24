require 'mongo'
include Mongo
require 'sawyer'

class Storage

  def initialize()
    client = MongoClient.new
    db = client['github-reports']
    @repos = db['repos']
    @pulls = db['pulls']
    @comments = db['comments']
  end

  def has_repos(organisation)
    @repos.find({:organisation => organisation}, {:fields => ['_id']}).to_a.size > 0
  end

  def repos(organisation)
    repos = @repos.find({:organisation => organisation})
    repos.to_a.map! do |repo|
      repo['repo']
    end
  end

  def save_repo(organisation_name, repo)
    @repos.insert({:organisation => organisation_name, :repo => repo})
  end

  def pulls(repo)
    pulls = @pulls.find({:repo => repo['full_name']})
    pulls.to_a.map! do |pull|
      pull['pull']
    end
  end

  def save_pull(repo, pull)
    @pulls.insert({:repo => repo[:full_name], :pull => pull})
  end

  def comments(pull)
    c = @comments.find({:pull => pull['url']})
    comments = c.to_a.map do |comment|
      comment['comments']
    end
    comments.flatten
  end

  def has_comments(pull)
    @comments.find({:pull => pull['url']}, {:fields => ['_id']}).to_a.size > 0
  end

  def save_comments(pull, comments)
    @comments.insert({:pull => pull['url'], :comments => comments})
  end

end
