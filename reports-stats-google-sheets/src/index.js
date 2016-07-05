'use strict';

/* exported onOpen, onInstall, updateData, updatePrStats */

var http = new Http();
var reports = new Reports(http);
var geometry = new Geometry();
var dataSheet = new Sheet('_data', geometry);
var inputSheet = new Sheet('Input', geometry);
var statsSheet = new Sheet('Stats', geometry);

var main = new Main(reports, geometry, dataSheet, inputSheet, statsSheet);

function onOpen() {
  var ui = SpreadsheetApp.getUi();
  ui.createMenu('Github Reports')
    .addItem('Update data', 'updateData')
    .addItem('Update PR stats', 'updatePrStats')
    .addToUi();
}

function onInstall() {
  onOpen();
}

function updateData() {
  main.updateAll();
}

function updatePrStats() {
  main.updatePrStats();
}
