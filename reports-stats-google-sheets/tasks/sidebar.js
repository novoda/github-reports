'use strict';

const inject = require('gulp-inject');
const bowerFiles = require('main-bower-files');
const rename = require('gulp-rename');
const flatmap = require('gulp-flatmap');

module.exports = (gulp, config) => {

  const wrapTagJs = (content)  => {
    return `<script type="text/javascript">
${content}
</script>`;
  };

  const wrapTagCss = (content)  => {
    return `<style type="text/css">
${content}
</style>`;
  };

  const getContentsOrReferences = (includeContents) => {
    if (!includeContents) {
      return undefined;
    }
    return function(filePath, file) {
      let fileContents = file.contents.toString('utf8');
      if (filePath.slice(-3) === '.js') {
        return wrapTagJs(fileContents);
      }
      if (filePath.slice(-4) === '.css') {
        return wrapTagCss(fileContents);
      }
    };
  };

  const getHtmlContents = (partialName) => {
    return function(filePath, file) {
      let fileContents = file.contents.toString('utf8');
      if (filePath.slice(-5) === '.html' && partialName === file.relative) {
        return fileContents;
      }
    };
  };

  const getDestinationFolder = function(forBuild) {
    let destFolder = forBuild ? config.build : config.tmp;
    return `${destFolder}/sidebar`;
  };

  const buildSidebarTask = (forBuild) => {
    let allSourceFiles = [
      `${config.src}/sidebar/**/*.css`,
      `${config.src}/lib/common/**/*.js`,
      `!${config.src}/lib/common/reports.js`,
      `${config.tmp}/lib/common/reports.js`,
      `${config.src}/lib/web/**/*.js`,
      `!${config.src}/lib/web/**/*.controller.js`
    ];
    let allBowerFiles = bowerFiles();
    let destFolder = getDestinationFolder(forBuild);

    return function() {
      return gulp.src(`${config.src}/sidebar/**/*.partial.html`)
        .pipe(flatmap(function(partialHtmlStream, partialHtmlFile) {
          let partialHtmlFileName = partialHtmlFile.relative;
          const regExp = /^(.*)(?:\.partial\.html)$/g;
          let partialPageName = regExp.exec(partialHtmlFileName)[1];

          return gulp
            .src(`${config.src}/sidebar/sidebar.template.html`, {base: `${config.src}/sidebar`})
            .pipe(inject(gulp.src(allBowerFiles), {
              starttag: '<!-- inject:bower:{{ext}} -->',
              transform: getContentsOrReferences(forBuild)
            }))
            .pipe(inject(gulp.src(allSourceFiles), {
              transform: getContentsOrReferences(forBuild)
            }))
            .pipe(inject(gulp.src(`${config.src}/sidebar/**/*.partial.html`), {
              transform: getHtmlContents(partialHtmlFileName)
            }))
            .pipe(inject(gulp.src(`${config.src}/lib/web/**/${partialPageName}.controller.js`), {
              starttag: '<!-- inject:pageController:js -->',
              transform: getContentsOrReferences(forBuild)
            }))
            .pipe(rename(`sidebar-${partialPageName}.html`));
        }))
        .pipe(gulp.dest(destFolder));
    };
  };

  gulp.task('build:sidebar:dev', ['test', 'clean:sidebar:dev', 'config'], buildSidebarTask(false));

  gulp.task('build:sidebar', ['test', 'clean:sidebar', 'config'], buildSidebarTask(true));

};
