package reddit

import config._
import reddit.model._
import reddit.model.Codecs._

import cats.effect._
import sttp.client._
import sttp.client.circe._

object Reddit {

  def getFrontPage(
    cfg: RedditConfig
  )(implicit
    backend: SttpBackend[IO, Nothing, NothingT]
  ): IO[List[Post]] = {
    val headers = Map(
      "user-agent" -> s"rollup by ${cfg.username}",
      "accept" -> "application/json"
    )

    def request(authToken: String) = {
      basicRequest
        .get(uri"https://oauth.reddit.com/best")
        .auth
        .bearer(authToken)
        .headers(headers)
        .response(asJson[Page])
    }

    for {
      authToken <- getAuthToken(cfg, headers)
      resp <- request(authToken).send().map(_.body)
      posts <- resp.fold(IO.raiseError, page => IO(page.data.children.map(_.data)))
    } yield posts

  }

  private def getAuthToken(cfg: RedditConfig, headers: Map[String, String])(implicit
    backend: SttpBackend[IO, Nothing, NothingT]
  ): IO[String] = {

    /**
      * 2FA was disabled because it doesn't work with this type of API access. Here's how to do it:
      * https://www.reddit.com/r/redditdev/comments/7ue4z8/enabling_2_factor_auth_for_my_account_breaks/dtjn532
      *
      * Obviously, this requires programatic access to bitwarden, and I don't wanna do that right now.
      */
    val body = Map(
      "grant_type" -> "password",
      "username" -> cfg.username,
      "password" -> cfg.password
    )

    val request = basicRequest
      .post(uri"https://www.reddit.com/api/v1/access_token")
      .body(body)
      .auth
      .basic(cfg.clientId, cfg.clientSecret)
      .headers(headers)
      .response(asJson[TokenResponse])

    for {
      resp <- request.send().map(_.body)
      accessToken <- resp.fold(IO.raiseError, tokenResp => IO(tokenResp.access_token))
    } yield accessToken
  }
}
