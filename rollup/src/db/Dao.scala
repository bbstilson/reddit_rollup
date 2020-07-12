package db

import reddit.model.Post

import cats.implicits._
import cats.effect.IO
import doobie._
import doobie.implicits._
import fs2.Stream

class Dao(xa: Transactor[IO]) {

  private lazy val truncate: ConnectionIO[Int] = sql"DROP TABLE IF EXISTS posts".update.run

  private lazy val create: ConnectionIO[Int] = {
    sql"""
    CREATE TABLE posts (
      id INTEGER PRIMARY KEY AUTOINCREMENT,
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

  private val insertPostQuery: Update[Post] = {
    val query = """INSERT INTO posts (
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
    ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"""
    Update[Post](query, None)
  }

  def insertPosts(posts: List[Post]): Stream[IO, Unit] =
    insertPostQuery.updateManyWithGeneratedKeys("id")(posts).transact(xa)

  def getAllPosts: IO[List[Post]] = sql"select * from posts".query[Post].to[List].transact(xa)
}
