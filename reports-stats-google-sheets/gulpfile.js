'use strict';

var gulp = require('gulp');
var shell = require('gulp-shell');
var eslint = require('gulp-eslint');
var Karma = require('karma').Server;
var inject = require('gulp-inject');
var clean = require('gulp-clean');
var bowerFiles = require('main-bower-files');
var rename = require('gulp-rename');
var flatmap = require('gulp-flatmap');

gulp.task('lint', function() {
  return gulp.src(['src/**/*.js'])
    .pipe(eslint())
    .pipe(eslint.format())
    .pipe(eslint.failAfterError());
});

gulp.task('karma', function(done) {
  new Karma({
    configFile: __dirname + '/karma.conf.js',
    singleRun: true
  }, done).start();
});

gulp.task('test', ['lint', 'karma']);

gulp.task('clean-sidebar', function() {
  return gulp.src('build/sidebar', {read: false})
    .pipe(clean());
});

gulp.task('clean-lib', function() {
  return gulp.src(['build/lib', 'build/*.js'], {read: false})
    .pipe(clean());
});

gulp.task('clean-bower', function() {
  return gulp.src('build/bower_components', {read: false})
    .pipe(clean());
});

var wrapTagJs = function(content) {
  return `<script type="text/javascript">
${content}
</script>`;
};

var wrapTagCss = function(content) {
  return `<style type="text/css">
${content}
</style>`;
};

var transformContents = function(partialName) {
  return function(filePath, file) {
    let fileContents = file.contents.toString('utf8');
    if (filePath.slice(-3) === '.js') {
      return wrapTagJs(fileContents);
    }
    if (filePath.slice(-4) === '.css') {
      return wrapTagCss(fileContents);
    }
    if (filePath.slice(-5) === '.html' && partialName === file.relative) {
      return fileContents;
    }
  };
};

gulp.task('build-sidebar', ['test', 'clean-sidebar'], function() {
  var allSourceFiles = [
    'src/sidebar/**/*.css',
    'src/lib/common/**/*.js',
    'src/lib/web/**/*.js',
    '!src/lib/web/**/*.controller.js',
    'src/sidebar/**/*.partial.html'
  ];
  var allBowerFiles = bowerFiles();

  return gulp.src('src/sidebar/**/*.partial.html')
    .pipe(flatmap(function(partialHtmlStream, partialHtmlFile) {
      var partialHtmlFileName = partialHtmlFile.relative;
      const regExp = /^(.*)(?:\.partial\.html)$/g;
      var partialPageName = regExp.exec(partialHtmlFileName)[1];

      return gulp
        .src('src/sidebar/sidebar.template.html', {base: './src/sidebar'})
        .pipe(inject(gulp.src(allBowerFiles), {
          starttag: '<!-- inject:bower:{{ext}} -->',
          transform: transformContents()
        }))
        .pipe(inject(gulp.src(allSourceFiles), {
          transform: transformContents(partialHtmlFileName)
        }))
        .pipe(inject(gulp.src(`src/lib/web/**/${partialPageName}.controller.js`), {
          starttag: '<!-- inject:pageController:js -->',
          transform: transformContents()
        }))
        .pipe(rename(`sidebar-${partialPageName}.html`));
    }))
    .pipe(gulp.dest('build/sidebar'));
});

gulp.task('copy-lib', ['test', 'clean-lib'], function() {
  return gulp
    .src([
      'src/*.js',
      'src/lib/apps/**/*.js',
      'src/lib/common/**/*.js'
    ], {
      base: './src'
    })
    .pipe(gulp.dest('build/'));
});

gulp.task('bower', ['clean-bower'], function() {
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
    .pipe(gulp.dest('build/bower_components'))
});

gulp.task('build', ['build-sidebar', 'copy-lib', 'bower']);

gulp.task('upload', ['build'], shell.task(['gapps upload']));
