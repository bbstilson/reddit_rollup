package db

import config.Config
import reddit.model.Post

import cats.effect._
import doobie._
import doobie.util.ExecutionContexts
import doobie.scalatest._
import org.scalatest.BeforeAndAfter
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers._
import pureconfig._
import pureconfig.generic.auto._

import java.nio.file.{ Files, Paths }

class DaoSpec extends AnyFlatSpec with should.Matchers with BeforeAndAfter with IOChecker {

  // We need a ContextShift[IO] before we can construct a Transactor[IO]. The passed ExecutionContext
  // is where nonblocking operations will be executed. For testing here we're using a synchronous EC.
  implicit val cs = IO.contextShift(ExecutionContexts.synchronous)

  val dbConfig = ConfigSource.resources("test.conf").loadOrThrow[Config].database

  val transactor = Transactor.fromDriverManager[IO](
    dbConfig.driver,
    dbConfig.url,
    dbConfig.user,
    dbConfig.password,
    Blocker.liftExecutionContext(ExecutionContexts.synchronous) // just for testing
  )

  val dao = new Dao(transactor)

  before {
    dao.recreate.unsafeRunSync
  }

  after {
    val testDb = Paths.get(System.getProperty("user.dir") + "/" + dbConfig.url.split(":").last)
    Files.delete(testDb)
  }

  "upsertPosts" should "update data if the record already exists" in {
    val p1 = Post(
      "id",
      "subreddit",
      "selftext",
      "title",
      "subreddit_name_prefixed",
      0.5f,
      10,
      Some("selftext_html"),
      "thumbnail",
      "permalink",
      "url"
    )
    val p2 = p1.copy(
      upvote_ratio = 0.9f,
      score = 20,
      selftext_html = Some("wee woo"),
      selftext = "myselftext"
    )

    dao.upsertPosts(List(p1)).unsafeRunSync // insert
    dao.upsertPosts(List(p2)).unsafeRunSync // upsert

    val posts = dao.getAllPosts.unsafeRunSync
    posts.size shouldBe 1
    posts.head shouldBe p2
  }
}
