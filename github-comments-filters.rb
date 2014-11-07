require 'parallel'

#
# Perform filters upon comments
#
class Comments < Array

  #
  # Returns all comments by the given user
  #
  def by(user)
    Comments.new self.select { |comment| comment['user']['login'].downcase == user.downcase }
  end

  #
  # Returns all comments by other users (not by the given user)
  #
  def exclude_by(user)
    Comments.new self.select { |comment| comment['user']['login'].downcase != user.downcase }
  end

  #
  # Returns all comments after the given date
  #
  def from(date)
    from_time = Time.parse(date)
    Comments.new self.select { |comment| comment['created_at'] >= from_time }
  end

  #
  # Returns all comments before the given date
  #
  def until(date)
    until_time = Time.parse(date)
    Comments.new self.select { |comment| comment['created_at'] <= until_time }
  end

end

class CommentsFilters

  def initialize(api)
    @api = api
  end

  def in_all(pulls)
    comments = Parallel.map pulls do |pull|
      comments_in(pull)
    end
    Comments.new comments.flatten
  end

  def comments_in(pull)
    @api.comments pull
  end
  
end
