'use strict';

/* exported Sheet */
function Sheet(name, geometry) {

  this.sheet = SpreadsheetApp.getActiveSpreadsheet().getSheetByName(name);

  Sheet.prototype.getValue = function(row, col) {
    return this.sheet.getSheetValues(row, col, 1, 1)[0][0];
  };

  Sheet.prototype.getValueAsDate = function(row, col) {
    var date = this.getValue(row, col);
    return date.toISOString();
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

}
