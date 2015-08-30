#!/usr/bin/env bash

set -e

goal_serve-backend() {
  lein run
}

goal_serve-frontend() {
  lein figwheel
}

goal_serve-styles() {
  lein less auto
}

goal_check-codestyle() {
  lein kibit
}


goal_test() {
  lein test && lein cljsbuild test
}

if type -t "goal_$1" &>/dev/null; then
  goal_$1 ${@:2}
else
  echo "usage: $0 <goal>
goal:
    serve-backend      -- start backend server
    serve-frontend     -- start cljs autocompiler and figwheel
    serve-styles       -- start css autocompiler
    test               -- run tests
    check-codestyle    -- run code style recommendations
    "

  exit 1
fi