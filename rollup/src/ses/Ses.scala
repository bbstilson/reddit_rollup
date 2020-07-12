package ses

import config.SesConfig
import reddit.model.Post

import cats.effect.IO
import software.amazon.awssdk.services.ses.model._
import software.amazon.awssdk.services.ses.SesClient

import scala.jdk.CollectionConverters._

class Ses(config: SesConfig) {
  import Ses._

  private val ses = SesClient.create

  def sendRollup(postsBySubreddit: Map[String, List[Post]]): IO[SendEmailResponse] =
    IO(ses.sendEmail(mkRequest(postsBySubreddit)))

  private def mkRequest(postsBySubreddit: Map[String, List[Post]]): SendEmailRequest = {
    val destination = Destination.builder.toAddresses(List(config.email).asJava).build
    val subjectContent = Content.builder.charset(CHARSET).data(SUBJECT).build
    val htmlContent = Content.builder.charset(CHARSET).data(mkBody(postsBySubreddit)).build
    val body = Body.builder.html(htmlContent).build
    val message = Message.builder.subject(subjectContent).body(body).build

    SendEmailRequest.builder
      .source(config.email)
      .destination(destination)
      .message(message)
      .replyToAddresses(config.email)
      .build
  }

  private def mkBody(postsBySubreddit: Map[String, List[Post]]): String = {
    postsBySubreddit.toList
      .sortBy { case (subreddit, _) => subreddit.toLowerCase }
      .map {
        case (subreddit, posts) =>
          val postsHtml = posts.map { post =>
            s"""
               |<h3><a href="https://old.reddit.com/${post.permalink}">${post.title}</a> - ${post.score}</h3>
            """.stripMargin
          }.mkString

          s"""
             |<h2>${subreddit}</h2>
             |$postsHtml
        """.stripMargin
      }
      .mkString
  }
}

object Ses {
  private[ses] val SUBJECT = "Reddit Rollup"
  private[ses] val CHARSET = "UTF-8"
}
