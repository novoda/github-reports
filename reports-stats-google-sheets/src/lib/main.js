'use strict';

/* exported Main */
function Main(reports, geometry, dataSheet, inputSheet, statsSheet) {

  function setColumnValues(column, array) {
    var matrix = geometry.arrayToColumnRange(array);
    dataSheet.setValues(2, column, matrix.length, 1, matrix);
  }

  function updateRepositories() {
    setColumnValues(1, reports.getRepositories());
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
    var from = inputSheet.getValueAsDate(2, 1);
    var to = inputSheet.getValueAsDate(2, 2);
    var repos = inputSheet.getColumnValues(2, 3);
    var groupBy = inputSheet.getValue(2, 4);
    var withAverage = inputSheet.getValue(2, 5);
    
    var stats = this.getPrStats(from, to, repos, groupBy, withAverage);
    var lines = [].concat.apply([], stats);
    statsSheet.clear();
    statsSheet.setValues(1, 1, lines.length, 11, lines);
  };

  Main.prototype.updateAll = function() {
    updateRepositories();
  };

}
