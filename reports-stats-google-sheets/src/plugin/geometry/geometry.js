'use strict';

/* exported Geometry */
function Geometry() {
  // empty constructor
}

Geometry.prototype.arrayToColumnRange = function(array) {
  var column = [];
  (array || []).forEach(function(element) {
    column.push([element]);
  });
  return column;
};

Geometry.prototype.columnRangeToArray = function(column) {
  return []
    .concat.apply([], column)
    .filter(function(element) {
      return element;
    });
};
