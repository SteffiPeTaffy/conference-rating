#!/usr/bin/env bash
BASEDIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
set -x
set -e

goal_serve-backend() {
  lein run -- --ssl-redirect-disabled
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

goal_unit-test() {
    lein cljsbuild test && lein test
}

goal_functional-test() {
    lein test :functional
}

goal_test() {
  goal_unit-test && goal_functional-test
}

goal_build-uberjar() {
  lein uberjar
}

goal_deploy-uberjar() {
  APPNAME=$1
  shift
  OPTIONS=$@

  JARFILE=${BASEDIR}/target/conference-rating.jar
  heroku plugins:install https://github.com/heroku/heroku-deploy
  cd ~
  heroku deploy:jar --jar $JARFILE --app $APPNAME --options "$OPTIONS"
  cd -
}

if type -t "goal_$1" &>/dev/null; then
  goal_$1 ${@:2}
else
  echo "usage: $0 <goal>
goal:
    serve-backend                          -- start backend server
    serve-frontend                         -- start cljs autocompiler and figwheel
    serve-styles                           -- start css autocompiler
    test                                   -- run tests
    check-codestyle                        -- run code style recommendations
    build-uberjar                          -- build self contained jar file
    deploy-uberjar <appname> <options>     -- deploys the uberjar to heroku with the given app name and options like --environment and --okta-active
    "

  exit 1
fi