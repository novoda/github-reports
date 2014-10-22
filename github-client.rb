require 'octokit'
require 'parallel'
require 'json'

class Client

  def initialize(access_token)
    Octokit.auto_paginate = true
    @ghclient = Octokit::Client.new(:access_token => access_token)
  end

  def repos(organisation)
    @ghclient.organization_repositories(organisation).map! do |repo|
      repo.to_hash
    end
  end

  def pulls(repo)
    @ghclient.pull_requests(repo.full_name, :state => 'all').map! do |pull|
      pull.to_hash
    end
  end

end
