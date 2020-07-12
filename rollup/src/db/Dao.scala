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
      title TEXT NOT NULL,
      subreddit_name_prefixed TEXT NOT NULL,
      upvote_ratio FLOAT NOT NULL,
      score INTEGER NOT NULL,
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
      title,
      subreddit_name_prefixed,
      upvote_ratio,
      score,
      thumbnail,
      permalink,
      url
    )
    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
    ON CONFLICT (id)
    DO UPDATE SET
      upvote_ratio = excluded.upvote_ratio,
      score = excluded.score
    """
    Update[Post](query, None)
  }

  def upsertPosts(posts: List[Post]): IO[Int] = upsertPostQuery.updateMany(posts).transact(xa)

  def getAllPosts: IO[List[Post]] = sql"select * from posts".query[Post].to[List].transact(xa)
}
