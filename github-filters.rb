class Filters

  module RepoFilters
    def pulls_by(user)
      self.select do |repo|
        pulls_from_user = repo['pulls'].select do |pull|
          pull['user']['login'] == user
        end
        pulls_from_user.size > 0
      end
    end
  end

  def initialize(repos)
    @repos = repos
    @repos.extend RepoFilters
  end

  def only()
    @repos
  end

end
