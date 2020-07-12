import db._
import reddit._
import reddit.model.Post
import ses._

import cats.effect._
import sttp.client._

import java.time.{ DayOfWeek, LocalDateTime }

class Rollup(reddit: Reddit, dao: Dao, ses: Ses) {

  def run(implicit backend: SttpBackend[IO, Nothing, NothingT]): IO[ExitCode] = {
    val shouldSendReport = {
      val now = LocalDateTime.now
      // Just going to send a daily roll up for now.
      val isSunday = DayOfWeek.from(now) == DayOfWeek.SUNDAY || true
      val is8am = now.getHour == 8
      isSunday && is8am
    }

    for {
      _ <- dao.createIfNotExists
      _ <- if (shouldSendReport) sendReport else IO.unit
      posts <- reddit.getFrontPage
      n <- dao.upsertPosts(posts)
      _ <- IO(println(s"Upserted $n posts."))
    } yield ExitCode.Success
  }

  private def sendReport: IO[Unit] = {
    for {
      allPosts <- dao.getAllPosts
      topPosts = getTopPostsFromEachSubreddit(allPosts)
      response <- ses.sendRollup(topPosts)
      _ <- IO(println(response))
      _ <- dao.recreate
      _ <- IO(println("recreated the database"))
    } yield ()
  }

  private def getTopPostsFromEachSubreddit(posts: List[Post]): Map[String, List[Post]] = {
    posts
      .groupBy(_.subreddit)
      .map {
        case (subreddit, posts) =>
          subreddit -> posts.sortBy(p => (p.score, p.upvote_ratio)).reverse.take(3)
      }
  }
}
