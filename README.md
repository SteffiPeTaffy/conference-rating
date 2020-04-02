# Conference-Tracking

## Status

[![Stories in Ready](https://badge.waffle.io/SteffiPeTaffy/conference-rating.png?label=ready&title=Ready)](http://waffle.io/SteffiPeTaffy/conference-rating)


## Development

Install [Leiningen](http://leiningen.org/), if you don't already have it.
Make sure you have a MongoDB running on your machine. You can use docker for that:
```
docker run -p 27017:27017 mongo
```

Development tasks are wrapped in the `./go` script, e.g.:

* `./go serve-backend`, `./go serve-frontend`, `./go serve-styles` launches development servers for the respective parts of the app. Run these in separate shells in parallel for the best development experience. The app listens on  [http://localhost:3000](http://localhost:3000)
* `./go test` runs all tests


## Links

* [Taskboard](https://waffle.io/SteffiPeTaffy/conference-rating)
* [QA Environment](http://conference-rating-qa.herokuapp.com/)
