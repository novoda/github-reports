'use strict';

/* exported onInstall, onOpen, createMenu, createSidebars, showSidebarPrStats, showPrStats, setTimeout */

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
  createSidebars();
  showSidebarPrStats();
}

function createMenu() {
  spreadsheet.createMenu('Github Reports', {
    'Pull Request Stats': 'showSidebarPrStats'
  });
}

function createSidebars() {
  spreadsheet.createSidebar(SIDEBAR_PR_STATS_NAME, 'PR Stats', 'sidebar');
}

function showSidebarPrStats() {
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
