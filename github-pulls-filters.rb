require 'parallel'

class Pulls < Array

  #
  # Returns all PRs made by the given user
  #
  def by(user)
    Pulls.new self.select { |pull| pull['user']['login'].downcase == user.downcase }
  end

  #
  # Returns all PRs made by other users (not by the given user)
  #
  def exclude_by(user)
    Pulls.new self.select { |pull| pull['user']['login'].downcase != user.downcase }
  end

  #
  # Returns all PRs created after the given date
  #
  def from(date)
    from_time = Time.parse(date)
    Pulls.new self.select { |pull| pull['created_at'] >= from_time }
  end

  #
  # Returns all PRs created before the given date
  #
  def until(date)
    until_time = Time.parse(date)
    Pulls.new self.select { |pull| pull['created_at'] <= until_time }
  end

end

class FullDataPulls < Pulls

  #
  # Returns all PRs merged by the given user
  #
  def merged_by(user)
    FullDataPulls.new self.select { |pull| pull['merged'] and pull['merged_by']['login'].downcase == user.downcase }
  end

end

class PullsFilters

  def initialize(api)
    @api = api
  end

  #
  # Ensures all the data exists locally before returning all the PRs for all the repos in the given organisation
  #
  def in(organisation)
    @api.fetch organisation # Ensure we've got all the data cached
    pulls_in_all @api.repos(organisation)
  end

  #
  # Returns a list of all PRs in all the given repos
  #
  def pulls_in_all(repos)
    pulls = Parallel.map repos do |repo|
      pulls_in(repo)
    end
    Pulls.new pulls.flatten
  end

  #
  # Returns a list of all PRs in the given repo
  #
  def pulls_in(repo)
    @api.pulls repo
  end

  #
  # Returns the full data for the given list of PRs
  #
  def full_data_for(pulls)
    full_data_pulls = Parallel.map pulls do |pull|
      @api.pull pull
    end
    FullDataPulls.new full_data_pulls
  end

end
