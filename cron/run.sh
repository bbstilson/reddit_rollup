# /bin/bash

set -e

cd reddit_rollup
mill rollup.run
echo ""
