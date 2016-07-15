'use strict';

var gulp = require('gulp');
var shell = require('gulp-shell');
var eslint = require('gulp-eslint');
var Karma = require('karma').Server;
var inject = require('gulp-inject');
var clean = require('gulp-clean');
var bowerFiles = require('main-bower-files');

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

var transformContentsJs = function(filePath, file) {
  return wrapTagJs(file.contents.toString('utf8'));
};

var wrapTagCss = function(content) {
  return `<style type="text/css">
${content}
</style>`;
};

var transformContentsCss = function(filePath, file) {
  return wrapTagCss(file.contents.toString('utf8'));
};

gulp.task('build-sidebar', ['test', 'clean-sidebar'], function() {
  return gulp
    .src('src/sidebar/sidebar.html', {base: './src/sidebar'})
    .pipe(inject(gulp.src('src/lib/common/**/*.js'), {
      starttag: '<!-- inject:lib/common:{{ext}} -->',
      transform: transformContentsJs
    }))
    .pipe(inject(gulp.src('src/lib/web/**/*.js'), {
      starttag: '<!-- inject:lib/web:{{ext}} -->',
      transform: transformContentsJs
    }))
    .pipe(inject(gulp.src('src/sidebar/**/*.css'), {
      starttag: '<!-- inject:css -->',
      transform: transformContentsCss
    }))
    .pipe(inject(gulp.src(bowerFiles()), {
      starttag: '<!-- inject:bower:{{ext}} -->',
      transform: transformContentsJs
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
     .src(bowerFiles(), {
       base: './bower_components'
     })
     .pipe(gulp.dest('build/bower_components'))
});

gulp.task('build', ['build-sidebar', 'copy-lib', 'bower']);

gulp.task('upload', ['build'], shell.task(['gapps upload']));
