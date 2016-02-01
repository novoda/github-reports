require 'octokit'
require 'json'

class Client

  def initialize(access_token)
    Octokit.auto_paginate = true
    @ghclient = Octokit::Client.new(:access_token => access_token)
  end

  def repos(organisation)
    @ghclient.organization_repositories(organisation).map! do |repo|
      to_hash repo
    end
  end

  def pulls(repo)
    @ghclient.pull_requests(repo[:full_name], :state => 'all').map! do |pull|
      to_hash pull
    end
  end

  def pull(pull)
    pr = @ghclient.pull_request(pull['base']['repo']['full_name'], pull['number'])
    to_hash pr
  end

  def comments(pull)
    @ghclient.pull_comments(pull['base']['repo']['full_name'], pull['number']).map! do |comment|
      to_hash comment
    end
  end

  def to_hash(object)
    hash = object.to_hash
    keys_to_convert = hash.keys.select do |key|
      hash[key].is_a? Sawyer::Resource
    end
    keys_to_convert.each { |key| hash[key] = to_hash(hash[key]) }
    hash
  end

end