'use strict';

/* exported AppsHttp */
function AppsHttp(queryStringifier) {

  AppsHttp.prototype.fetch = function(url, query) {
    var encodedUrl = encodeURI(queryStringifier.getUrlWithQuery(url, query));
    var response = UrlFetchApp.fetch(encodedUrl);
    var json = response.getContentText();
    return Promise.resolve(JSON.parse(json));
  };

}
