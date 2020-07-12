package reddit.model

case class TokenResponse(
  access_token: String,
  scope: String,
  token_type: String,
  refresh_token: Option[String]
)
