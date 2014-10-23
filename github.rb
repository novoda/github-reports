require 'parallel'
require './github-client'

class Github

  def initialize(access_token)
    @client = Client.new access_token
    @storage = Storage.new
  end

  def fetch(organisation)
    if @storage.contains organisation
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

  def save_repo(organisation, repo)
    @storage.save_repo organisation, repo
  end

  def save_all_pulls(repo, pulls)
    Parallel.each pulls do |pull|
      save_pull repo, pull
    end
  end

  def save_pull(repo, pull)
    @storage.save_pull repo, pull
  end

  def repos(organisation)
    @storage.repos organisation
  end

  def pulls(repo)
    @storage.pulls repo
  end

end
