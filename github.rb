require 'parallel'
require './github-client'

class Github

  def initialize(access_token)
    @client = Client.new access_token
    @storage = Storage.new
  end

  def fetch(organisation)
    if @storage.has_repos organisation
      return
    end

    repos = @client.repos organisation
    save_repos organisation, repos
    Parallel.each repos do |repo|
      pulls = @client.pulls repo
      save_pulls repo, pulls
    end
  end

  def repos(organisation)
    @storage.repos organisation
  end

  def save_repos(organisation, repos)
    @storage.save_repos organisation, repos
  end

  def pulls(repo)
    @storage.pulls repo
  end

  def save_pulls(repo, pulls)
    @storage.save_pulls repo, pulls
  end

  def pull(pull)
    if not @storage.has_pull pull
      save_pull @client.pull(pull)
    end
    return @storage.pull pull
  end

  def save_pull(pull)
    @storage.save_pull pull
  end

  def comments(pull)
    if not @storage.has_comments pull
      save_comments pull, @client.comments(pull)
    end
    return @storage.comments pull
  end

  def save_comments(pull, comments)
    @storage.save_comments pull, comments
  end

end
