'use strict';

/* exported Sheet */
function Sheet(sheet, geometry) {
  this.sheet = sheet;
  this.geometry = geometry;
}

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
  return this.geometry.columnRangeToArray(values);
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

Sheet.prototype.setBold = function(startRow, startCol, rows, cols) {
  var cells = this.sheet.getRange(startRow, startCol, rows, cols);
  cells.setFontWeight('bold');
};

Sheet.prototype.alignToCenterMiddle = function(startRow, startCol, rows, cols) {
  var cells = this.sheet.getRange(startRow, startCol, rows, cols);
  cells.setHorizontalAlignment('center');
  cells.setVerticalAlignment('middle');
};

Sheet.prototype.setRowHeight = function(row, height) {
  this.sheet.setRowHeight(row, height);
};

Sheet.prototype.setWrap = function(startRow, startCol, rows, cols, wrap) {
  var cells = this.sheet.getRange(startRow, startCol, rows, cols);
  cells.setWrap(wrap);
};

Sheet.prototype.setFrozenRows = function(rows) {
  this.sheet.setFrozenRows(rows);
};

Sheet.prototype.setFrozenColumns = function(columns) {
  this.sheet.setFrozenColumns(columns);
};

Sheet.prototype.setBottomBorder = function(startRow, startCol, rows, cols) {
  var cells = this.sheet.getRange(startRow, startCol, rows, cols);
  cells.setBorder(false, false, true, false, false, false);
};

Sheet.prototype.setBackground = function(startRow, startCol, rows, cols, color) {
  var cells = this.sheet.getRange(startRow, startCol, rows, cols);
  cells.setBackground(color);
};
