'use strict';

/* exported onOpen, onInstall, updateData, updatePrStats, createMenu, showSidebar */

var http = new AppsHttp();
var reports = new Reports(http);
var geometry = new Geometry();
var dataSheet = new Sheet('_data', geometry);
var inputSheet = new Sheet('Input', geometry);
var statsSheet = new Sheet('Stats', geometry);

var main = new Main(reports, geometry, dataSheet, inputSheet, statsSheet);

var ui = SpreadsheetApp.getUi();
var sidebar = HtmlService.createHtmlOutputFromFile('sidebar')
  .setTitle('Github Reports')
  .setSandboxMode(HtmlService.SandboxMode.IFRAME);

function onOpen() {
  createMenu();
  showSidebar();
}

function createMenu() {
  ui.createMenu('Github Reports')
    .addItem('Update data', 'updateData')
    .addItem('Update PR stats', 'updatePrStats')
    .addItem('Show sidebar', 'showSidebar')
    .addToUi();
}

function showSidebar() {
  ui.showSidebar(sidebar);
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
