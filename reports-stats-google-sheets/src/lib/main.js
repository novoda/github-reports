'use strict';

/* exported Main */
function Main(reports, geometry, dataSheet, inputSheet, statsSheet) {

  var SHEET_DATA_FIRST_ROW = 2;
  var SHEET_DATA_REPOSITORIES_COLUMN = 1;

  var SHEET_INPUT_FIRST_ROW = 2;
  var SHEET_INPUT_FROM_COLUMN = 1;
  var SHEET_INPUT_TO_COLUMN = 2;
  var SHEET_INPUT_REPOSITORIES_COLUMN = 3;
  var SHEET_INPUT_GROUP_BY_COLUMN = 4;
  var SHEET_INPUT_WITH_AVERAGE_COLUMN = 5;

  var SHEET_STATS_FIRST_ROW = 1;
  var SHEET_STATS_FIRST_COLUMN = 1;

  var STATS_USER_ATTRIBUTE_QTY = 11;

  function setColumnValues(column, array) {
    var matrix = geometry.arrayToColumnRange(array);
    dataSheet.setValues(SHEET_DATA_FIRST_ROW, column, matrix.length, 1, matrix);
  }

  function updateRepositories() {
    setColumnValues(SHEET_DATA_REPOSITORIES_COLUMN, reports.getRepositories());
  }

  function userToLineFn(group) {
    return function(user) {
      return [
        group.name,
        user.username,
        user.mergedPrs,
        user.openedPrs,
        user.otherPeopleCommentsOnUserPrs,
        user.userCommentsOnOtherPeoplePrs,
        user.commentsOnAllPrs,
        user.commentsOnOwnPrs,
        user.averageOtherPeopleCommentsOnUserPrs,
        user.averageUserCommentsOnMergedPrs,
        user.type
      ];
    };
  }

  function groupToLines(group) {
    var userToLine = userToLineFn(group);
    var lines = group.users.map(userToLine);

    if (group.organisationAverage) {
      lines.push(userToLine(group.organisationAverage));
    }

    return lines;
  }

  Main.prototype.getPrStats = function(from, to, repos, groupBy, withAverage) {
    var stats = reports.getPrStats(from, to, repos, groupBy, withAverage);
    return stats.groups.map(groupToLines);
  };

  Main.prototype.updatePrStats = function() {
    var from = inputSheet.getValueAsDate(SHEET_INPUT_FIRST_ROW, SHEET_INPUT_FROM_COLUMN);
    var to = inputSheet.getValueAsDate(SHEET_INPUT_FIRST_ROW, SHEET_INPUT_TO_COLUMN);
    var repos = inputSheet.getColumnValues(SHEET_INPUT_FIRST_ROW, SHEET_INPUT_REPOSITORIES_COLUMN);
    var groupBy = inputSheet.getValue(SHEET_INPUT_FIRST_ROW, SHEET_INPUT_GROUP_BY_COLUMN);
    var withAverage = inputSheet.getValue(SHEET_INPUT_FIRST_ROW, SHEET_INPUT_WITH_AVERAGE_COLUMN);
    
    var stats = this.getPrStats(from, to, repos, groupBy, withAverage);
    var lines = [].concat.apply([], stats);
    statsSheet.clear();
    statsSheet.setValues(SHEET_STATS_FIRST_ROW, SHEET_STATS_FIRST_COLUMN, lines.length, STATS_USER_ATTRIBUTE_QTY, lines);
  };

  Main.prototype.updateAll = function() {
    updateRepositories();
  };

}
