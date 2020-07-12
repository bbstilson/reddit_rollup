package reddit.model

// This was helpful since reddit doesn't want to provide return type definitions 🙄
// https://praw.readthedocs.io/en/latest/code_overview/models/submission.html?highlight=Submission
case class Post(
  id: String,
  subreddit: String,
  title: String,
  subreddit_name_prefixed: String,
  upvote_ratio: Float,
  score: Int,
  thumbnail: String,
  permalink: String,
  url: String
)
