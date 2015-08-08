#! /bin/sh

mongod --dbpath data/db &
lein run &
lein less auto &
lein figwheel
