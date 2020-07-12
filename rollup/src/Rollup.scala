import config._
import db._
import reddit._

import cats.effect._
import sttp.client._

import java.time.{ DayOfWeek, LocalDateTime }

class Rollup(config: Config, dao: Dao) {

  def run(implicit backend: SttpBackend[IO, Nothing, NothingT]): IO[ExitCode] = {
    // Send a report every morning at roughly 8am.
    val shouldSendReport = {
      val now = LocalDateTime.now
      val isSunday = DayOfWeek.from(now) == DayOfWeek.SUNDAY
      val is8am = now.getHour == 8
      isSunday && is8am
    }

    for {
      _ <- if (shouldSendReport) sendReport else IO.unit
      posts <- Reddit.getFrontPage(config.reddit)
      n <- dao.upsertPosts(posts)
      _ <- IO(println(s"Upserted $n posts."))
    } yield ExitCode.Success
  }

  private def sendReport: IO[Unit] = {
    for {
      // 1) read all the data out of the database
      // 2) do the processing
      // 3) send email
      // 4) reset database
      _ <- dao.recreate
      _ <- IO(println("recreated the database"))
    } yield ()
  }
}
