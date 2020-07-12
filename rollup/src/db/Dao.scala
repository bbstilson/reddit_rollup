package db

import reddit.model.Post

import cats.implicits._
import cats.effect.IO
import doobie._
import doobie.implicits._

class Dao(xa: Transactor[IO]) {

  private lazy val truncate: ConnectionIO[Int] = sql"DROP TABLE IF EXISTS posts".update.run

  private lazy val create: ConnectionIO[Int] = {
    sql"""
    CREATE TABLE posts (
      id TEXT PRIMARY KEY NOT NULL,
      subreddit TEXT NOT NULL,
      selftext TEXT NOT NULL,
      title TEXT NOT NULL,
      subreddit_name_prefixed TEXT NOT NULL,
      upvote_ratio FLOAT NOT NULL,
      score INTEGER NOT NULL,
      selftext_html TEXT NULL,
      thumbnail TEXT NOT NULL,
      permalink TEXT NOT NULL,
      url TEXT NULL
    )
    """.update.run
  }

  def recreate: IO[Int] = (truncate, create).mapN(_ + _).transact(xa)

  private val upsertPostQuery: Update[Post] = {
    val query = """INSERT INTO posts (
      id,
      subreddit,
      selftext,
      title,
      subreddit_name_prefixed,
      upvote_ratio,
      score,
      selftext_html,
      thumbnail,
      permalink,
      url
    )
    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    ON CONFLICT (id)
    DO UPDATE SET
      selftext = excluded.selftext,
      upvote_ratio = excluded.upvote_ratio,
      score = excluded.score,
      selftext_html = excluded.selftext_html
    """
    Update[Post](query, None)
  }

  def upsertPosts(posts: List[Post]): IO[Int] = upsertPostQuery.updateMany(posts).transact(xa)

  def getAllPosts: IO[List[Post]] = sql"select * from posts".query[Post].to[List].transact(xa)
}
