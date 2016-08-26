'use strict';

const bowerFiles = require('main-bower-files');

module.exports = (gulp, config) => {

  gulp.task('bower-web-dependencies', ['clean:bower-web-dependencies'], () => {
    return gulp
      .src(bowerFiles({
        overrides: {
          jquery: {
            ignore: true
          }
        }
      }), {
        base: './bower_components'
      })
      .pipe(gulp.dest(`${config.build}/bower_components`))
  });

};
