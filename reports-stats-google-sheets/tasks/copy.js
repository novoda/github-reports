'use strict';

module.exports = (gulp, config) => {

  gulp.task('copy:lib', ['test', 'clean:lib'], () => {
    return gulp
      .src([
        `${config.src}/*.js`,
        `${config.src}/lib/apps/**/*.js`,
        `${config.src}/lib/common/**/*.js`,
        `!${config.src}/lib/common/reports.js`
      ], {
        base: `./${config.src}`
      })
      .pipe(gulp.dest(`${config.build}/`));
  });

  gulp.task('copy:config', ['config'], () => {
    return gulp
      .src([`${config.tmp}/lib/common/reports.js`], {
        base: `${config.tmp}`
      })
      .pipe(gulp.dest(`${config.build}/`));
  });

};
