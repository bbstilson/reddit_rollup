package db

import reddit.model.Post

class DaoSpec extends DbUnitSpec {
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
