module PullsFilters

  def by(user)
    Pulls.new self.select { |pull| pull['user']['login'] == user }
  end

end

module ReposFilters

  def with_pulls_by(user)
    Repos.new self.select { |repo| repo['pulls'].by(user).size > 0 }
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
