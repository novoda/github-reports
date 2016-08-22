const gulp = require('gulp');
const del = require('del');
const shell = require('gulp-shell');
const zip = require('gulp-zip');
const pkg = require('./package.json');

const ng = (args) => {
  return shell.task(`./node_modules/.bin/ng ${args}`);
};

gulp.task('test', ng('test --watch false'));

gulp.task('test:watch', ng('test'));

gulp.task('clean', () => {
  return del(['./dist']);
});

gulp.task('build', ['clean'], ng('build'));

gulp.task('shipit', ['build'], () => {
  return gulp.src('./dist/*')
    .pipe(zip(`${pkg.name}-v${pkg.version}.zip`))
    .pipe(gulp.dest('./zip'));
});
