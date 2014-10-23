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

  def pulls_in(repo)
    repo['pulls']
  end

  def filter(array)
    array.size > 0
  end

  def remove_repos_with_no_pulls(repos)
    Repos.new repos.select { |repo| filter pulls_in(repo) }
  end

  def with_pulls_by(user)
    filtered = Repos.new self.each { |repo| repo['pulls'] = pulls_in(repo).by(user) }
    remove_repos_with_no_pulls filtered
  end

  def from(date)
    filtered = Repos.new self.each { |repo| repo['pulls'] = pulls_in(repo).from(date) }
    remove_repos_with_no_pulls filtered
  end

  def until(date)
    filtered = Repos.new self.each { |repo| repo['pulls'] = pulls_in(repo).until(date) }
    remove_repos_with_no_pulls filtered
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
