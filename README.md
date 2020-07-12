# Reddit Rollup

## Problem

I wanted to stop going on reddit, but I do actually find useful things on there sometimes. Paramountly, memes.

## Solution

This app takes a snapshot of my personal frontpage and saves the posts to a sqlite database. At the end of a week, it pulls all the posts from week, does some heuristics to choose the most "important" (again, memes) posts from each subreddit, then sends me an email.

I run it every hour on a [Raspberry Pi 4](https://www.raspberrypi.org/products/raspberry-pi-4-model-b/) ubuntu server.

## Running

If you have not done so already:

1) Install [mill](http://www.lihaoyi.com/mill/#installation).
2) Create a Reddit [script app](https://github.com/reddit-archive/reddit/wiki/OAuth2-Quick-Start-Example#first-steps).
3) Verify your email on [SES](https://docs.aws.amazon.com/ses/latest/DeveloperGuide/quick-start.html).
4) Ensure you have sqlite3: `sqlite3 --version`.

Finally, export the following variables:

```bash
REDDIT_USERNAME # your reddit username
REDDIT_PASSWORD # your reddit password
CLIENT_ID # your app client id
CLIENT_SECRET # your app secret
MY_EMAIL # your email that you verified in SES.
```

Then:

```bash
mill rollup.run
```

## Testing

Testing is pretty light, but to run them:

```bash
mill rollup.test
```
