'use strict';

describe('Geometry', function() {

  var geometry;

  beforeEach(function() {
    geometry = new Geometry();
  });

  describe('arrayToColumnRange', function() {

    it('should transform an array into a matrix with a single column', function() {
      var array = [1, 2, 3, 4, 5];

      var actual = geometry.arrayToColumnRange(array);

      expect(actual).toEqual([[1], [2], [3], [4], [5]]);
    });

    it('should transform an empty array in an empty matrix', function() {
      var array = [];

      var actual = geometry.arrayToColumnRange(array);

      expect(actual).toEqual([]);
    });

    it('should transform a null array in an empty matrix', function() {
      var array = null;

      var actual = geometry.arrayToColumnRange(array);

      expect(actual).toEqual([]);
    });

    it('should transform an undefined array in an empty matrix', function() {
      var array = undefined;

      var actual = geometry.arrayToColumnRange(array);

      expect(actual).toEqual([]);
    });

  });

  describe('columnRangeToArray', function() {

    it('should flatten a column array', function() {
      var column = [[1], [2], [3]];

      var actual = geometry.columnRangeToArray(column);

      expect(actual).toEqual([1, 2, 3]);
    });

    it('should not alter an empty column array', function() {
      var column = [];

      var actual = geometry.columnRangeToArray(column);

      expect(actual).toEqual([]);
    });

    it('should return an empty array for a null column', function() {
      var column = null;

      var actual = geometry.columnRangeToArray(column);

      expect(actual).toEqual([]);
    });

    it('should return an empty array for an undefined column', function() {
      var column = undefined;

      var actual = geometry.columnRangeToArray(column);

      expect(actual).toEqual([]);
    });

    it('should not include empty elements in a column', function() {
      var column = [[1], [], [2], [], []];

      var actual = geometry.columnRangeToArray(column);

      expect(actual).toEqual([1, 2]);
    });

    it('should not include empty strings in a column', function() {
      var column = [[1], [''], [2], [''], ['']];

      var actual = geometry.columnRangeToArray(column);

      expect(actual).toEqual([1, 2]);
    });

  });

});
