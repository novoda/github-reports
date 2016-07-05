'use strict';

/* exported Http */
function Http() {

  Http.prototype.fetch = function(url, params) {
    var response = UrlFetchApp.fetch(url, params || {});
    var json = response.getContentText();
    return JSON.parse(json);
  };

}
