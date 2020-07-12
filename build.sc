import $ivy.`io.github.davidgregory084::mill-tpolecat:0.1.3`

import mill._
import mill.scalalib._
import io.github.davidgregory084.TpolecatModule

object rollup extends ScalaModule with TpolecatModule {
  def scalaVersion = "2.13.2"

  def mainClass = Some("App")

  def ivyDeps =
    Agg(
      // http
      ivy"com.softwaremill.sttp.client::circe:2.2.1",
      ivy"com.softwaremill.sttp.client::async-http-client-backend-cats:2.2.1",
      ivy"io.circe::circe-generic:0.14.0-M1",
      // database
      ivy"org.xerial:sqlite-jdbc:3.32.3",
      ivy"org.tpolecat::doobie-core:0.8.8",
      ivy"org.tpolecat::doobie-hikari:0.8.8",
      // config
      ivy"com.github.pureconfig::pureconfig:0.13.0",
      ivy"com.github.pureconfig::pureconfig-cats-effect:0.13.0"
    )
}
