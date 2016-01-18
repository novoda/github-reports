require 'mongo'
include Mongo
require 'sawyer'

class Storage

  def initialize()
    client = MongoClient.new
    db = client['github-reports']
    @repos = db['repos']
    @pulls = db['pulls']
    @single_pulls = db['single_pulls']
    @comments = db['comments']
  end

  def has_repos(organisation)
    @repos.find({:organisation => organisation}, {:fields => ['_id']}).to_a.size > 0
  end

  def repos(organisation)
    r = @repos.find({:organisation => organisation}).to_a
    repos = r.map! { |repo| repo['repos'] }
    repos.flatten
  end

  def save_repos(organisation_name, repos)
    @repos.insert({:organisation => organisation_name, :repos => repos})
  end

  def pulls(repo)
    p = @pulls.find({:repo => repo['full_name']}).to_a
    pulls = p.map! { |pull| pull['pulls'] }
    pulls.flatten
  end

  def save_pulls(repo, pulls)
    @pulls.insert({:repo => repo[:full_name], :pulls => pulls})
  end

  def pull(pull)
    p = @single_pulls.find({:url => pull['url']}).to_a
    p[0]['pull']
  end

  def has_pull(pull)
    @single_pulls.find({:url => pull['url']}, {:fields => ['_id']}).to_a.size > 0
  end

  def save_pull(pull)
    @single_pulls.insert({:url => pull[:url], :pull => pull})
  end

  def comments(pull)
    c = @comments.find({:pull => pull['url']}).to_a
    comments = c.map { |comment| comment['comments'] }
    comments.flatten
  end

  def has_comments(pull)
    @comments.find({:pull => pull['url']}, {:fields => ['_id']}).to_a.size > 0
  end

  def save_comments(pull, comments)
    @comments.insert({:pull => pull['url'], :comments => comments})
  end

  def drop_repos
    @repos.drop
  end

  def drop_pulls
    @pulls.drop
  end

  def drop_single_pulls
    @single_pulls.drop
  end

  def drop_comments
    @comments.drop
  end

end
