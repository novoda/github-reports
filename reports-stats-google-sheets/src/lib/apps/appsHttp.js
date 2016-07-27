'use strict';

/* exported AppsHttp */
function AppsHttp(queryStringifier) {
  this.queryStringifier = queryStringifier;
}

AppsHttp.prototype.fetch = function(url, query) {
  var encodedUrl = encodeURI(this.queryStringifier.getUrlWithQuery(url, query));
  try {
    var response = UrlFetchApp.fetch(encodedUrl);
    var json = response.getContentText();
    return Promise.resolve(JSON.parse(json));
  } catch (error) {
    return Promise.reject(error);
  }
};
