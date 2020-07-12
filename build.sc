import $ivy.`io.github.davidgregory084::mill-tpolecat:0.1.3`

import mill._
import mill.scalalib._
import io.github.davidgregory084.TpolecatModule

object rollup extends ScalaModule with TpolecatModule {
  def scalaVersion = "2.13.2"

  def mainClass = Some("App")

  val DoobieVersion = "0.9.0"
  val PureConfigVersion = "0.13.0"
  val SttpVersion = "2.2.1"

  def ivyDeps =
    Agg(
      // http
      ivy"com.softwaremill.sttp.client::circe:$SttpVersion",
      ivy"com.softwaremill.sttp.client::async-http-client-backend-cats:$SttpVersion",
      ivy"io.circe::circe-generic:0.14.0-M1",
      // database
      ivy"org.xerial:sqlite-jdbc:3.32.3",
      ivy"org.tpolecat::doobie-core:$DoobieVersion",
      ivy"org.tpolecat::doobie-hikari:$DoobieVersion",
      // ses
      ivy"software.amazon.awssdk:ses:2.13.23",
      // config
      ivy"com.github.pureconfig::pureconfig:$PureConfigVersion",
      ivy"com.github.pureconfig::pureconfig-cats-effect:$PureConfigVersion",
      // logging
      ivy"org.slf4j:slf4j-simple:1.7.30"
    )

  object test extends Tests {

    def testFrameworks = Seq("org.scalatest.tools.Framework")

    def ivyDeps =
      Agg(
        ivy"org.scalatest::scalatest:3.2.0",
        ivy"org.tpolecat::doobie-scalatest:$DoobieVersion"
      )
  }
}
