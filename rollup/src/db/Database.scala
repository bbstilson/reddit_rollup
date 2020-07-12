package db

import config.DatabaseConfig

import cats.effect._
import doobie.hikari._

import scala.concurrent.ExecutionContext

object Database {

  def transactor(config: DatabaseConfig, executionContext: ExecutionContext, blocker: Blocker)(
    implicit contextShift: ContextShift[IO]
  ): Resource[IO, HikariTransactor[IO]] = {
    HikariTransactor.newHikariTransactor[IO](
      driverClassName = config.driver,
      url = config.url,
      user = config.user,
      pass = config.password,
      connectEC = executionContext,
      blocker = blocker
    )
  }
}
