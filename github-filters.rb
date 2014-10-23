module PullsFilters

  def by(user)
    Pulls.new self.select { |pull| pull['user']['login'] == user }
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

module ReposFilters

  def get_pulls()
    Pulls.new self.map { |repo| repo['pulls'] }.flatten
  end

end

class Repos < Array
  include ReposFilters
end

class Pulls < Array
  include PullsFilters
end

class Filters

  def initialize(api)
    ghrepos = api.repos ORGANISATION
    ghrepos.each { |repo| repo['pulls'] = Pulls.new api.pulls(repo) }
    @repos = Repos.new ghrepos
  end

  def repos()
    @repos
  end

end
