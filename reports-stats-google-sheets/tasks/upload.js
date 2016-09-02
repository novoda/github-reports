'use strict';

const gapps = require('node-google-apps-script');

module.exports = (gulp, config) => {

  gulp.task('upload', ['build'], function() {
    return gapps.upload();
  });

};
