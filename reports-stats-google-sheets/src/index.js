'use strict';

/* exported onInstall, onOpen, createMenu, createAndShowSidebarPrStats, showPrStats, setTimeout */

var stringifier = new QueryStringifier();
var http = new AppsHttp(stringifier);
var reports = new Reports(http);
var geometry = new Geometry();
var spreadsheet = new Spreadsheet(geometry);

var main = new Main(reports, spreadsheet);
var SIDEBAR_PR_STATS_NAME = 'PR_STATS';

function onInstall() {
  onOpen();
}

function onOpen() {
  createMenu();
  createAndShowSidebarPrStats();
}

function createMenu() {
  spreadsheet.createMenu('Github Reports', {
    'Pull Request Stats': 'createAndShowSidebarPrStats'
  });
}

function createAndShowSidebarPrStats() {
  spreadsheet.createSidebar(SIDEBAR_PR_STATS_NAME, 'PR Stats', 'sidebar');
  spreadsheet.showSidebar(SIDEBAR_PR_STATS_NAME);
}

function showPrStats(from, to, repositories, groupBy, withAverage, requestISODate) {
  return main.showPrStats(from, to, repositories, groupBy, withAverage, requestISODate);
}

// polyfill to make the promise library work in non-windowed environments]]
function setTimeout(fn, timeout) {
  Utilities.sleep(timeout);
  fn();
}
