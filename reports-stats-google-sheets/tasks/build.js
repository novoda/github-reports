'use strict';

const connect = require('gulp-connect');

module.exports = (gulp, config) => {

  gulp.task('build', ['build:web', 'copy:lib', 'copy:config', 'bower-web-dependencies']);

  gulp.task('build:dev', ['build:web:dev'], () => {
    gulp.src(`${config.src}/**/*`)
      .pipe(connect.reload());
  });

};
