'use strict';

const inject = require('gulp-inject');

module.exports = (gulp, config) => {

  gulp.task('config', () => {
    return gulp.src([`${config.src}/lib/common/reports.js`], {base: `${config.src}/`})
      .pipe(inject(gulp.src(`${config.src}/config.json`), {
        starttag: '<!-- inject:api -->',
        transform: (filePath, file) => {
          const fileContents = file.contents.toString('utf8');
          const appConfig = JSON.parse(fileContents);
          return `var API_BASE = '${appConfig.api}';`;
        }
      }))
      .pipe(gulp.dest(config.tmp));
  });

};
