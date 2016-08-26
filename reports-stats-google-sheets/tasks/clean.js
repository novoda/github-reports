'use strict';

const clean = require('gulp-clean');

module.exports = (gulp, config) => {

  gulp.task('clean:sidebar', function() {
    return gulp
      .src(`${config.build}/sidebar`, {read: false})
      .pipe(clean());
  });

  gulp.task('clean:sidebar:dev', function() {
    return gulp
      .src(`${config.tmp}/sidebar`, {read: false})
      .pipe(clean());
  });

  gulp.task('clean:lib', function() {
    return gulp
      .src([`${config.build}/lib`, `${config.build}/*.js`], {read: false})
      .pipe(clean());
  });

  gulp.task('clean:bower-server-dependencies', function() {
    return gulp
      .src(`${config.build}/bower_components`, {read: false})
      .pipe(clean());
  });

};
