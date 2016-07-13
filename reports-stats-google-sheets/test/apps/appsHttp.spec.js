'use strict';

describe('AppsHttp', function() {

  var http, anyUrl;

  beforeEach(function() {
    http = new AppsHttp();
    anyUrl = 'https://api.novoda.com';
  });

  describe('getUrlWithQuery', function() {

    it('should not change the URL with empty query', function() {
      var result = http.getUrlWithQuery(anyUrl, {});

      expect(result).toEqual(anyUrl);
    });

    it('should not change the URL with null query', function() {
      var result = http.getUrlWithQuery(anyUrl, null);

      expect(result).toEqual(anyUrl);
    });

    it('should not change the URL with undefined query', function() {
      var result = http.getUrlWithQuery(anyUrl, undefined);

      expect(result).toEqual(anyUrl);
    });

    it('should add basic strings to the url', function() {
      var result = http.getUrlWithQuery(anyUrl, {
        some: 'string',
        another: 'string'
      });

      var expected = anyUrl + '?some=string&another=string';
      expect(result).toEqual(expected);
    });

    it('should add a date to the url', function() {
      var result = http.getUrlWithQuery(anyUrl, {
        someDate: new Date(Date.UTC(2016, 0, 1, 0, 0, 0, 0))
      });

      var expected = anyUrl + '?someDate=2016-01-01T00:00:00.000Z';
      expect(result).toEqual(expected);
    });

    it('should add a number to the url', function() {
      var result = http.getUrlWithQuery(anyUrl, {
        someNumber: 42
      });

      var expected = anyUrl + '?someNumber=42';
      expect(result).toEqual(expected);
    });

    it('should add a number array to the url', function() {
      var result = http.getUrlWithQuery(anyUrl, {
        someNumberArray: [4, 8, 15, 16, 23, 42]
      });

      var expected = anyUrl + '?someNumberArray=[4,8,15,16,23,42]';
      expect(result).toEqual(expected);
    });

    it('should add a string array to the url', function() {
      var result = http.getUrlWithQuery(anyUrl, {
        someStringArray: ['see', 'you', 'in', 'another', 'life', 'brotha']
      });

      var expected = anyUrl + '?someStringArray=["see","you","in","another","life","brotha"]';
      expect(result).toEqual(expected);
    });

    it('should add a mixed number and string array to the url', function() {
      var result = http.getUrlWithQuery(anyUrl, {
        someMixedArray: ['answer', 'is', 42, '!']
      });

      var expected = anyUrl + '?someMixedArray=["answer","is",42,"!"]';
      expect(result).toEqual(expected);
    });

  });

});
