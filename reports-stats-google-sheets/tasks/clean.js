'use strict';

const clean = require('gulp-clean');

module.exports = (gulp, config) => {

  gulp.task('clean:web', function() {
    return gulp
      .src(`${config.build}/web`, {read: false})
      .pipe(clean());
  });

  gulp.task('clean:web:dev', function() {
    return gulp
      .src(`${config.tmp}/web`, {read: false})
      .pipe(clean());
  });

  gulp.task('clean:lib', function() {
    return gulp
      .src([`${config.build}/lib`, `${config.build}/*.js`], {read: false})
      .pipe(clean());
  });

  gulp.task('clean:bower-web-dependencies', function() {
    return gulp
      .src(`${config.build}/bower_components`, {read: false})
      .pipe(clean());
  });

};
