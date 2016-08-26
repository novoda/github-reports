'use strict';

const connect = require('gulp-connect');

module.exports = (gulp, config) => {

  gulp.task('build', ['build:sidebar', 'copy:lib', 'copy:config', 'bower-server-dependencies']);

  gulp.task('build:dev', ['build:sidebar:dev'], () => {
    gulp.src('src/**/*')
      .pipe(connect.reload());
  });

};
