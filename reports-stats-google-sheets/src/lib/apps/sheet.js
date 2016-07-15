'use strict';

/* exported Sheet */
function Sheet(sheet, geometry) {

  this.sheet = sheet;

  Sheet.prototype.getValue = function(row, col) {
    return this.sheet.getSheetValues(row, col, 1, 1)[0][0];
  };

  Sheet.prototype.getValueAsDate = function(row, col) {
    var date = this.getValue(row, col);
    if (date instanceof Date) {
      return date.toISOString();
    }                     
    return null;
  };

  Sheet.prototype.getColumnValues = function(startRow, col) {
    var values = this.sheet.getRange(startRow, col, 1000).getValues();
    return geometry.columnRangeToArray(values);
  };

  Sheet.prototype.setValues = function(startRow, startCol, rows, cols, matrix) {
    this.sheet.getRange(startRow, startCol, rows, cols).setValues(matrix);
  };

  Sheet.prototype.clear = function() {
    this.sheet.clear();
  };

  Sheet.prototype.setName = function(name) {
    this.sheet.setName(name);
  };

}
