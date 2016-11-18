'use strict';

/* exported Spreadsheet */
function Spreadsheet(geometry) {
  this.geometry = geometry;
  this.spreadsheet = SpreadsheetApp.getActiveSpreadsheet();
  this.ui = SpreadsheetApp.getUi();
  this.sidebars = {};
}

Spreadsheet.prototype.getNumberOfSheets = function() {
  return this.spreadsheet.getNumSheets();
};

Spreadsheet.prototype.createNewSheet = function(name) {
  var sheetIndex = this.getNumberOfSheets();
  var newSheet = this.spreadsheet.insertSheet(sheetIndex);
  newSheet.setName(name);
  return new Sheet(newSheet, this.geometry);
};

Spreadsheet.prototype.getSheetByName = function(name) {
  var googleSheet = this.spreadsheet.getSheetByName(name);
  return new Sheet(googleSheet, this.geometry);
};

Spreadsheet.prototype.createSidebar = function(name, title, htmlFile) {
  this.sidebars[name] = HtmlService
    .createHtmlOutputFromFile(htmlFile)
    .setTitle(title)
    .setSandboxMode(HtmlService.SandboxMode.IFRAME);
  return name;
};

Spreadsheet.prototype.showSidebar = function(name) {
  var sidebar = this.sidebars[name];
  if (!sidebar) {
    throw new Error('No sidebar with name "' + name + '" exists.');
  }
  this.ui.showSidebar(sidebar);
};

Spreadsheet.prototype.createMenu = function(name, config) {
  var menu = this.ui.createMenu(name);
  Object.keys(config).forEach(function(key) {
    menu.addItem(key, config[key])
  });
  menu.addToUi();
};

Spreadsheet.prototype.showAlert = function(title, message) {
  this.ui.alert(title, message, this.ui.ButtonSet.OK);
};
