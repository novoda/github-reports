'use strict';

/* exported Main */
function Main(reports, geometry, dataSheet, inputSheet, statsSheet) {

  function setColumnValues(column, array) {
    var matrix = geometry.arrayToColumnRange(array);
    dataSheet.setValues(2, column, matrix.length, 1, matrix);
  }

  function updateUsersTeam() {
    setColumnValues(1, reports.getUsersTeam());
  }

  function updateProjects() {
    setColumnValues(2, reports.getProjects());
  }

  function updateRepositories() {
    setColumnValues(3, reports.getRepositories());
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

    lines.push(userToLine(group.externalAverage));
    lines.push(userToLine(group.teamAverage));
    lines.push(userToLine(group.assignedAverage));
    lines.push(userToLine(group.filterAverage));

    return lines;
  }

  Main.prototype.getPrStats = function(from, to, repos, projects, filter, groupBy, withAverage) {
    var stats = reports.getPrStats(from, to, repos, projects, filter, groupBy, withAverage);
    return stats.groups.map(groupToLines);
  };

  Main.prototype.updatePrStats = function() {
    var from = inputSheet.getValueAsDate(2, 1);
    var to = inputSheet.getValueAsDate(2, 2);
    var repos = inputSheet.getColumnValues(2, 3);
    var projects = inputSheet.getColumnValues(2, 4);
    var filter = inputSheet.getColumnValues(2, 5);
    var groupBy = inputSheet.getValue(2, 6);
    var withAverage = inputSheet.getValue(2, 7);
    
    var stats = this.getPrStats(from, to, repos, projects, filter, groupBy, withAverage);
    var lines = [].concat.apply([], stats);
    statsSheet.clear();
    statsSheet.setValues(1, 1, lines.length, 11, lines);
  };

  Main.prototype.updateAll = function() {
    updateUsersTeam();
    updateProjects();
    updateRepositories();
  };

}
