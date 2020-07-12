# Reddit Rollup

## Problem

I wanted to stop going on reddit, but I do actually find useful things on there sometimes. Paramountly, memes.

## Solution

This app takes a snapshot of my personal frontpage and saves the posts to a sqlite database. At the end of a week, it pulls all the posts from week, does some heuristics to choose the most "important" (again, memes) posts from each subreddit, then sends me an email.

I run it every hour on a [Raspberry Pi 4](https://www.raspberrypi.org/products/raspberry-pi-4-model-b/) ubuntu server.

## Running

After creating a [script app](https://github.com/reddit-archive/reddit/wiki/OAuth2-Quick-Start-Example#first-steps), One must export the following envvars:

```bash
USERNAME # your reddit username
PASSWORD # your reddit password
CLIENT_ID # your app client id
CLIENT_SECRET # your app secret
```

Then:

```bash
mill rollup.run
```
