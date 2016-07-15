'use strict';

/* exported Main */
function Main(reports, spreadsheet) {

  var SHEET_STATS_HEADER_ROW = 1;
  var SHEET_STATS_FIRST_ROW = 2;
  var SHEET_STATS_FIRST_COLUMN = 1;

  var STATS_USER_ATTRIBUTE_QTY = 11;

  function groupToLines(group) {
    var userToLine = userToLineFn(group);
    var lines = group.users.map(userToLine);

    if (group.organisationAverage) {
      lines.push(userToLine(group.organisationAverage));
    }

    return lines;
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

  function buildStatsHeader(groupBy) {
    var groupHeader = '';
    if (groupBy === 'MONTH') {
      groupHeader = 'Year-Month';
    } else if (groupBy === 'WEEK') {
      groupHeader = 'Year-Week';
    }

    return [
      groupHeader,
      'Username',
      'Merged PRs',
      'Opened PRs',
      'Other people comments on user\'s PRs',
      'User comments on other people\'s PRs',
      'Comments on all PRs',
      'Comments on own PRs',
      'Average of other people\'s comments on users\'s PRs',
      'Average of user comments on merged PRs',
      'User type'
    ];
  }

  function buildNewSheetName(requestISODate) {
    var requestDate = new Date(requestISODate);
    return 'PR Stats ' +
      requestDate.getFullYear() + '-' + padTens(requestDate.getMonth()) + '-' + padTens(requestDate.getDay()) + ' ' +
      padTens(requestDate.getHours()) + ':' + padTens(requestDate.getMinutes()) + ':' + padTens(requestDate.getSeconds());
  }

  function padTens(value) {
    return leftPad('00', value);
  }

  function leftPad(mask, value) {
    var str = value.toString();
    return mask.substring(0, mask.length - str.length) + str;
  }

  Main.prototype.showPrStats = function(from, to, repos, groupBy, withAverage, requestISODate) {
    return reports.getPrStats(from, to, repos, groupBy, withAverage)
      .then(function(stats) {
        return stats.groups.map(groupToLines);
      })
      .then(function(stats) {
        return [].concat.apply([], stats);
      })
      .then(function(lines) {
        var statsSheet = spreadsheet.createNewSheet(buildNewSheetName(requestISODate));
        statsSheet.clear();
        var header = [buildStatsHeader(groupBy)];
        statsSheet.setValues(
          SHEET_STATS_HEADER_ROW,
          SHEET_STATS_FIRST_COLUMN,
          header.length,
          STATS_USER_ATTRIBUTE_QTY,
          header
        );
        statsSheet.setValues(
          SHEET_STATS_FIRST_ROW,
          SHEET_STATS_FIRST_COLUMN,
          lines.length,
          STATS_USER_ATTRIBUTE_QTY,
          lines
        );
        return lines;
      })['catch'](
      // awful hax because Google Apps Script uses the stupidest AST parser or whatevs
      // see https://github.com/stefanpenner/es6-promise#usage-in-ie9
      function(error) {
        spreadsheet.showAlert('Error', 'There was an error while executing the request:\n' + error);
      });
  };

}
