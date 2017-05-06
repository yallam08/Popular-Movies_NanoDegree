# Popular Movies App

My implementation for "Popular Movies" app, the final project required for Udacity's course [Developing Android Apps](https://www.udacity.com/course/new-android-fundamentals--ud851).

The app conforms the [course rubric](https://review.udacity.com/#!/rubrics/67/view)

## Features:
* The app uses [TheMovieDB api](https://www.themoviedb.org/documentation/api) to fetch all the movies' data.
* Display most popular movies.
* Display highest rated movies.
* User can make a list of favourite movies and display it.
* Display the details of each movie(Title, Poster, Realease Date, Trailers, Reviews,..).

> NOTE: if you want to build the app you have to replace the string `tmdb_api_key` in `strings.xml` with your api key, which you can get by registering on [TMDB website](https://www.themoviedb.org).

## Libraries used:
* [Picasso](http://square.github.io/picasso/) to handle downloading/displaying/caching photos.
