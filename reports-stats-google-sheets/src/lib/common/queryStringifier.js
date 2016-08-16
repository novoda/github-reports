'use strict';

/* exported QueryStringifier */
function QueryStringifier() {
  // empty constructor
}

QueryStringifier.prototype.getUrlWithQuery = function(url, query) {
  var parts = Object
    .keys(query || {})
    .map(function(key) {
      var original = query[key];
      var value = original;

      if (original instanceof Date) {
        value = original.toISOString();
      } else if (original instanceof Array) {
        value = '[' + stringifyArray(original) + ']';
      }

      return key + '=' + value;
    })
    .join('&');
  if (parts) {
    parts = '?' + parts;
  }
  return url + parts;
};

function stringifyArray(values) {
  return values
    .map(function(value) {
      if (typeof value === 'string') {
        return '"' + value + '"';
      }
      return value;
    })
    .join(',');
}
