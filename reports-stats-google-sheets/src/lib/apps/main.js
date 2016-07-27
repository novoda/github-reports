'use strict';

/* exported Main */
function Main(reports, spreadsheet) {
  this.reports = reports;
  this.spreadsheet = spreadsheet;
}

var SHEET_STATS_HEADER_ROW = 1;
var SHEET_STATS_FIRST_ROW = 2;
var SHEET_STATS_FIRST_COLUMN = 1;

var STATS_USER_ATTRIBUTE_QTY = 11;

var AVERAGE_ROW_BACKGROUND_COLOR = '#8dc5db';

function groupToLines(group) {
  var userToLine = userToLineFn(group);
  var lines = group.users.map(userToLine);

  if (group.organisationAverage) {
    lines.push(userToLine(group.organisationAverage));
  }
  if (group.assignedAverage) {
    lines.push(userToLine(group.assignedAverage));
  }
  if (group.externalAverage) {
    lines.push(userToLine(group.externalAverage));
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

function setHeader(statsSheet, groupBy) {
  var header = [buildStatsHeader(groupBy)];
  statsSheet.setValues(
    SHEET_STATS_HEADER_ROW,
    SHEET_STATS_FIRST_COLUMN,
    header.length,
    STATS_USER_ATTRIBUTE_QTY,
    header
  );
  statsSheet.setBold(
    SHEET_STATS_HEADER_ROW,
    SHEET_STATS_FIRST_COLUMN,
    header.length,
    STATS_USER_ATTRIBUTE_QTY
  );
  statsSheet.alignToCenterMiddle(
    SHEET_STATS_HEADER_ROW,
    SHEET_STATS_FIRST_COLUMN,
    header.length,
    STATS_USER_ATTRIBUTE_QTY
  );
  statsSheet.setWrap(
    SHEET_STATS_HEADER_ROW,
    SHEET_STATS_FIRST_COLUMN,
    header.length,
    STATS_USER_ATTRIBUTE_QTY,
    true
  );
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

function freezeRowAndColumn(statsSheet) {
  statsSheet.setFrozenRows(1);
  statsSheet.setFrozenColumns(2);
}

function setStats(statsSheet, lines) {
  statsSheet.setValues(
    SHEET_STATS_FIRST_ROW,
    SHEET_STATS_FIRST_COLUMN,
    lines.length,
    STATS_USER_ATTRIBUTE_QTY,
    lines
  );
  statsSheet.setBold(
    SHEET_STATS_FIRST_ROW,
    SHEET_STATS_FIRST_COLUMN,
    lines.length,
    SHEET_STATS_FIRST_COLUMN + 1
  );
}

function setBorderToEndOfGroups(statsSheet, stats) {
  var rowAccumulator = SHEET_STATS_FIRST_ROW - 1;
  stats.groups.forEach(function(group) {
    rowAccumulator += group.users.length + countAverages(group);
    statsSheet.setBottomBorder(
      rowAccumulator,
      SHEET_STATS_FIRST_COLUMN,
      1,
      STATS_USER_ATTRIBUTE_QTY
    );
  });
}

function setBackgroundToGroupAverages(statsSheet, stats) {
  var rowAccumulator = SHEET_STATS_FIRST_ROW - 1;
  stats.groups.forEach(function(group) {
    var averages = countAverages(group);
    if (averages === 0) {
      return;
    }
    var firstRow = rowAccumulator + group.users.length + 1;
    statsSheet.setBackground(
      firstRow,
      SHEET_STATS_FIRST_COLUMN,
      averages,
      STATS_USER_ATTRIBUTE_QTY,
      AVERAGE_ROW_BACKGROUND_COLOR
    );
    rowAccumulator += group.users.length + averages;
  });
}

function countAverages(group) {
  var count = 0;
  if (group.organisationAverage) {
    count += 1;
  }
  if (group.assignedAverage) {
    count += 1;
  }
  if (group.externalAverage) {
    count += 1;
  }
  return count;
}

Main.prototype.showPrStats = function(from, to, repos, groupBy, withAverage, requestISODate) {
  var self = this;
  return this.reports.getPrStats(from, to, repos, groupBy, withAverage)
    .then(function(stats) {
      return {
        stats: stats,
        lines: stats.groups.map(groupToLines)
      };
    })
    .then(function(stats) {
      return {
        stats: stats.stats,
        lines: [].concat.apply([], stats.lines)
      };
    })
    .then(function(stats) {
      var statsSheet = self.spreadsheet.createNewSheet(buildNewSheetName(requestISODate));
      statsSheet.clear();
      setHeader(statsSheet, groupBy);
      freezeRowAndColumn(statsSheet);
      setStats(statsSheet, stats.lines);
      setBorderToEndOfGroups(statsSheet, stats.stats);
      setBackgroundToGroupAverages(statsSheet, stats.stats);
      return stats.lines;
    })['catch'](
    // awful hax because Google Apps Script uses the stupidest AST parser or whatevs
    // see https://github.com/stefanpenner/es6-promise#usage-in-ie9
    function(error) {
      self.spreadsheet.showAlert('Error', 'There was an error while executing the request:\n' + error);
    });
};
