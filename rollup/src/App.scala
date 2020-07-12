import db._
import config._

import cats.effect._
import doobie.util.transactor.Transactor
import doobie.util.ExecutionContexts
import pureconfig._
import pureconfig.generic.auto._
import pureconfig.module.catseffect.syntax._
import sttp.client._
import sttp.client.asynchttpclient.cats.AsyncHttpClientCatsBackend

object App extends IOApp {

  case class Resources(
    transactor: Transactor[IO],
    httpBackend: SttpBackend[IO, Nothing, NothingT],
    config: Config
  )

  def run(args: List[String]): IO[ExitCode] = buildResources.use(start)

  private def start(resources: Resources): IO[ExitCode] = {
    val dao = new Dao(resources.transactor)
    implicit val backend = resources.httpBackend
    new Rollup(resources.config, dao).run
  }

  private def buildResources: Resource[IO, Resources] = {
    for {
      config <- loadConfig
      ec <- ExecutionContexts.fixedThreadPool[IO](config.database.threadPoolSize)
      blocker <- Blocker[IO]
      transactor <- Database.transactor(config.database, ec, blocker)
      httpBackend <- AsyncHttpClientCatsBackend.resource[IO]()
    } yield Resources(transactor, httpBackend, config)
  }

  private def loadConfig: Resource[IO, Config] = {
    Blocker[IO].flatMap { blocker =>
      Resource.liftF(
        ConfigSource.defaultApplication.loadF[IO, Config](blocker)
      )
    }
  }
}
