'use strict';

/* exported onInstall, onOpen, createMenu, createAndShowSidebarPrStats, createAndShowSidebarAggregatedStats, showPrStats, setTimeout */

var SIDEBAR_PR_STATS_NAME = 'PR_STATS';
var SIDEBAR_AGGREGATED_STATS_NAME = 'AGGREGATED_STATS';

function onInstall() {
  onOpen();
}

function onOpen() {
  var main = buildMain_();
  createMenu(main);
  createAndShowSidebarPrStats(main);
}

function createMenu(main) {
  main.spreadsheet.createMenu('Github Reports', {
    'Pull Request Stats': 'createAndShowSidebarPrStats',
    'Aggregated User Stats': 'createAndShowSidebarAggregatedStats'
  });
}

function createAndShowSidebarPrStats(main) {
  main = buildMain_(main);
  main.spreadsheet.createSidebar(SIDEBAR_PR_STATS_NAME, 'PR Stats', 'sidebar-statsPr');
  main.spreadsheet.showSidebar(SIDEBAR_PR_STATS_NAME);
}

function createAndShowSidebarAggregatedStats(main) {
  main = buildMain_(main);
  main.spreadsheet.createSidebar(SIDEBAR_AGGREGATED_STATS_NAME, 'Aggregated Stats', 'sidebar-statsAggregated');
  main.spreadsheet.showSidebar(SIDEBAR_AGGREGATED_STATS_NAME);
}

function showPrStats(from, to, repositories, groupBy, withAverage, requestISODate) {
  var main = buildMain_();
  return main.showPrStats(from, to, repositories, groupBy, withAverage, requestISODate);
}

function buildMain_(main) {
  if (main) {
    return main;
  }

  var stringifier = new QueryStringifier();
  var http = new AppsHttp(stringifier);
  var reports = new Reports(http);
  var geometry = new Geometry();
  var spreadsheet = new Spreadsheet(geometry);

  main = new Main(reports, spreadsheet);

  return main;
}

// polyfill to make the promise library work in non-windowed environments
function setTimeout(fn, timeout) {
  Utilities.sleep(timeout);
  fn();
}
