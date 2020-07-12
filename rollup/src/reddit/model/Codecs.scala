package reddit.model

import io.circe.generic.semiauto._

object Codecs {
  implicit val tokenResponseDecoder = deriveDecoder[TokenResponse]
  implicit val postDecoder = deriveDecoder[Post]
  implicit val postWrapperDecoder = deriveDecoder[PostWrapper]
  implicit val pageDataDecoder = deriveDecoder[PageData]
  implicit val pageDecoder = deriveDecoder[Page]
}
