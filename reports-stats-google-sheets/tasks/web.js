'use strict';

const path = require('path');
const inject = require('gulp-inject');
const bowerFiles = require('main-bower-files');
const rename = require('gulp-rename');
const flatmap = require('gulp-flatmap');

module.exports = (gulp, config) => {

  const isJsFile = (filePath) => {
    return filePath.slice(-3) === '.js';
  };

  const wrapTagJs = (content) => {
    return `<script type="text/javascript">
${content}
</script>`;
  };

  const isCssFile = (filePath) => {
    return filePath.slice(-4) === '.css';
  };

  const wrapTagCss = (content) => {
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
      if (isJsFile(filePath)) {
        return wrapTagJs(fileContents);
      }
      if (isCssFile(filePath)) {
        return wrapTagCss(fileContents);
      }
    };
  };

  const getHtmlContents = (filePath, file) => {
    return file.contents.toString('utf8');
  };

  const getDestinationFolder = function(forBuild) {
    let destFolder = forBuild ? config.build : config.tmp;
    return `${destFolder}/web`;
  };

  const buildWebTask = (forBuild) => {
    let allSourceFiles = [
      // all styles
      `${config.src}/web/**/*.css`,
      // all scripts BUT tests and controllers
      `${config.src}/web/**/*.js`,
      `!${config.src}/web/**/*.spec.js`,
      `!${config.src}/web/**/*.controller.js`,
      // all shared files BUT tests
      `${config.src}/shared/**/*.js`,
      `!${config.src}/shared/**/*.spec.js`,
      // exclude and re-include the compiled ones
      `!${config.src}/shared/config/config.js`,
      `${config.tmp}/shared/config/config.js`
    ];
    let allBowerFiles = bowerFiles();
    let destFolder = getDestinationFolder(forBuild);

    return function() {
      return gulp.src(`${config.src}/web/**/*.partial.html`)
        .pipe(flatmap(function(partialHtmlStream, partialHtmlFile) {
          const partialHtmlFilePath = partialHtmlFile.relative;
          const partialHtmlFileName = path.basename(partialHtmlFilePath);
          const regExp = /^(.*)-(.*)(?:\.partial\.html)$/g;
          const matches = regExp.exec(partialHtmlFileName);

          const partialDirectory = path.dirname(partialHtmlFile.path);
          const templatePageName = matches[1];
          const partialPageName = matches[2];

          return gulp
            .src(`${config.src}/web/**/${templatePageName}.template.html`, {base: `${config.src}/web`})
            .pipe(inject(gulp.src(allBowerFiles), {
              starttag: '<!-- inject:bower:{{ext}} -->',
              transform: getContentsOrReferences(forBuild)
            }))
            .pipe(inject(gulp.src(allSourceFiles), {
              transform: getContentsOrReferences(forBuild)
            }))
            .pipe(inject(partialHtmlStream, {
              transform: getHtmlContents
            }))
            .pipe(inject(gulp.src(`${partialDirectory}/${templatePageName}-${partialPageName}.controller.js`), {
              starttag: '<!-- inject:controller:js -->',
              transform: getContentsOrReferences(forBuild)
            }))
            .pipe(rename(`${templatePageName}-${partialPageName}.html`));
        }))
        .pipe(gulp.dest(destFolder));
    };
  };

  gulp.task('build:web:dev', ['test', 'clean:web:dev', 'config'], buildWebTask(false));

  gulp.task('build:web', ['test', 'clean:web', 'config'], buildWebTask(true));

};
