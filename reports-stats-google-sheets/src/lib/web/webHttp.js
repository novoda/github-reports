'use strict';

/* exported WebHttp */
function WebHttp(queryStringifier) {

  WebHttp.prototype.fetch = function(url, query) {
    return new Promise(function(resolve, reject) {
      var fullUrl = queryStringifier.getUrlWithQuery(url, query);
      $.ajax(fullUrl, {
        crossDomain: true
      }).done(function(data) {
        resolve(data);
      }).fail(function(jqXHR, textStatus, error) {
        reject(error);
      });
    })
  };

}
