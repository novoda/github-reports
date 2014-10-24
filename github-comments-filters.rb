require 'parallel'

class Comments < Array

  def by(user)
    Comments.new self.select { |comment| comment['user']['login'].downcase == user.downcase }
  end

  def exclude_by(user)
    Comments.new self.select { |comment| comment['user']['login'].downcase != user.downcase }
  end

  def from(date)
    from_time = Time.parse(date)
    Comments.new self.select { |comment| comment['created_at'] >= from_time }
  end

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
