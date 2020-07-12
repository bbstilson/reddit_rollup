package config

case class DatabaseConfig(
  driver: String,
  url: String,
  user: String,
  password: String,
  threadPoolSize: Int
)

case class RedditConfig(
  username: String,
  password: String,
  clientId: String,
  clientSecret: String
)

case class SesConfig(email: String)

case class Config(
  reddit: RedditConfig,
  database: DatabaseConfig,
  ses: SesConfig
)
