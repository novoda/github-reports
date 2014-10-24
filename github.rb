require 'parallel'
require './github-client'

class Github

  def initialize(access_token)
    @client = Client.new access_token
    @storage = Storage.new
  end

  def fetch(organisation)
    if @storage.has_organisation organisation
      puts "I've got the data already"
      return
    end
    puts "Downloading data..."

    repos = @client.repos organisation
    Parallel.each repos do |repo|
      save_repo organisation, repo
      pulls = @client.pulls repo
      save_all_pulls repo, pulls
    end
    puts "Data downloaded!"
  end

  def repos(organisation)
    @storage.repos organisation
  end

  def save_repo(organisation, repo)
    @storage.save_repo organisation, repo
  end

  def pulls(repo)
    @storage.pulls repo
  end

  def save_all_pulls(repo, pulls)
    Parallel.each pulls do |pull|
      save_pull repo, pull
    end
  end

  def save_pull(repo, pull)
    @storage.save_pull repo, pull
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
