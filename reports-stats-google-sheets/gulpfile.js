'use strict';

let gulp = require('gulp');
let shell = require('gulp-shell');
let eslint = require('gulp-eslint');
let Karma = require('karma').Server;
let inject = require('gulp-inject');
let clean = require('gulp-clean');
let bowerFiles = require('main-bower-files');
let rename = require('gulp-rename');
let flatmap = require('gulp-flatmap');
let connect = require('gulp-connect');
var openURL = require('open');

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

gulp.task('clean:sidebar', function() {
  return gulp.src('build/sidebar', {read: false})
    .pipe(clean());
});

gulp.task('clean:sidebar:dev', function() {
  return gulp.src('.tmp/sidebar', {read: false})
    .pipe(clean());
});

gulp.task('clean:lib', function() {
  return gulp.src(['build/lib', 'build/*.js'], {read: false})
    .pipe(clean());
});

gulp.task('clean:bower', function() {
  return gulp.src('build/bower_components', {read: false})
    .pipe(clean());
});

const wrapTagJs = function(content) {
  return `<script type="text/javascript">
${content}
</script>`;
};

const wrapTagCss = function(content) {
  return `<style type="text/css">
${content}
</style>`;
};

const getContentsOrReferences = function(includeContents) {
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

const getHtmlContents = function(partialName) {
  return function(filePath, file) {
    let fileContents = file.contents.toString('utf8');
    if (filePath.slice(-5) === '.html' && partialName === file.relative) {
      return fileContents;
    }
  };
};

const getDestinationFolder = function(forBuild) {
  let destFolder = forBuild ? 'build' : '.tmp';
  return `${destFolder}/sidebar`;
};

const buildSidebarTask = function(forBuild) {
  let allSourceFiles = [
    'src/sidebar/**/*.css',
    'src/lib/common/**/*.js',
    'src/lib/web/**/*.js',
    '!src/lib/web/**/*.controller.js'
  ];
  let allBowerFiles = bowerFiles();
  let destFolder = getDestinationFolder(forBuild);

  return function() {
    return gulp.src('src/sidebar/**/*.partial.html')
      .pipe(flatmap(function(partialHtmlStream, partialHtmlFile) {
        let partialHtmlFileName = partialHtmlFile.relative;
        const regExp = /^(.*)(?:\.partial\.html)$/g;
        let partialPageName = regExp.exec(partialHtmlFileName)[1];

        return gulp
          .src('src/sidebar/sidebar.template.html', {base: 'src/sidebar'})
          .pipe(inject(gulp.src(allBowerFiles), {
            starttag: '<!-- inject:bower:{{ext}} -->',
            transform: getContentsOrReferences(forBuild)
          }))
          .pipe(inject(gulp.src(allSourceFiles), {
            transform: getContentsOrReferences(forBuild)
          }))
          .pipe(inject(gulp.src('src/sidebar/**/*.partial.html'), {
            transform: getHtmlContents(partialHtmlFileName)
          }))
          .pipe(inject(gulp.src(`src/lib/web/**/${partialPageName}.controller.js`), {
            starttag: '<!-- inject:pageController:js -->',
            transform: getContentsOrReferences(forBuild)
          }))
          .pipe(rename(`sidebar-${partialPageName}.html`));
      }))
      .pipe(gulp.dest(destFolder));
  };
};

gulp.task('build:sidebar:dev', ['test', 'clean:sidebar:dev'], buildSidebarTask(false));

gulp.task('build:sidebar', ['test', 'clean:sidebar'], buildSidebarTask(true));

gulp.task('copy:lib', ['test', 'clean:lib'], function() {
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

gulp.task('bower', ['clean:bower'], function() {
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

gulp.task('build', ['build:sidebar', 'copy:lib', 'bower']);

gulp.task('upload', ['build'], shell.task(['gapps upload']));

gulp.task('build:dev', ['build:sidebar:dev'], function() {
  gulp.src('src/**/*')
    .pipe(connect.reload());
});

gulp.task('start:server', ['build:dev'], function() {
  connect.server({
    root: ['.tmp/sidebar', '.'],
    livereload: true,
    host: '0.0.0.0',
    port: 9000
  });
});

gulp.task('start:client', ['start:server'], function() {
  openURL('http://localhost:9000');
});

gulp.task('watch', function() {
  gulp.watch(['./src/**/*'], ['build:dev']);
});

gulp.task('serve', ['start:client', 'watch'])
