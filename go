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

goal_test() {
  lein cljsbuild test
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
    "

  exit 1
fi