require 'parallel'

class Pulls < Array

  def by(user)
    Pulls.new self.select { |pull| pull['user']['login'].downcase == user.downcase }
  end

  def exclude_by(user)
    Pulls.new self.select { |pull| pull['user']['login'].downcase != user.downcase }
  end

  def from(date)
    from_time = Time.parse(date)
    Pulls.new self.select { |pull| pull['created_at'] >= from_time }
  end

  def until(date)
    until_time = Time.parse(date)
    Pulls.new self.select { |pull| pull['created_at'] <= until_time }
  end

end

class PullsFilters

  def initialize(api)
    @api = api
  end

  def in(organisation)
    @api.fetch organisation # Ensure we've got all the data cached
    pulls_in_all @api.repos(organisation)
  end

  def pulls_in_all(repos)
    pulls = Parallel.map repos do |repo|
      pulls_in(repo)
    end
    Pulls.new pulls.flatten
  end

  def pulls_in(repo)
    @api.pulls repo
  end

end
