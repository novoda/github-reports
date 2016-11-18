'use strict';

describe('Main', function() {

  var mockHttp, mockReports, mockSpreadsheet, main;

  beforeEach(function() {
    mockHttp = new AppsMockHttp();
    mockReports = new Reports(mockHttp);
    mockSpreadsheet = new MockSpreadsheet();

    main = new Main(mockReports, mockSpreadsheet);
  });

  var mockResolvesPromise = function(mock, value) {
    mock.and.returnValue(Promise.resolve(value));
  };

  var spyOnAndReturnPromise = function(object, methodName, resolvedValue) {
    mockResolvesPromise(spyOn(object, methodName), resolvedValue);
  };

  describe('showPrStats', function() {

    var ANY_START_DATE = '2016-01-01';
    var ANY_END_DATE = '2016-07-31';
    var ANY_REPOSITORIES = ['all-4', 'merlin'];
    var ANY_GROUP_BY = 'MONTH';
    var ANY_WITH_AVERAGE = true;

    var mockSheet;

    // setup mockSpreadsheet
    beforeEach(function() {
      mockSheet = new MockSheet();
      mockSpreadsheet.createNewSheet.and.returnValue(mockSheet);
    });

    var showPrStatsWithGroupBy = function(groupBy) {
      return main.showPrStats(ANY_START_DATE, ANY_END_DATE, ANY_REPOSITORIES, groupBy, ANY_WITH_AVERAGE);
    };

    var showPrStats = function() {
      return showPrStatsWithGroupBy(ANY_GROUP_BY);
    };

    describe('on success', function() {

      // setup mockReports
      beforeEach(function() {
        var ANY_USER_STATS = {
          mergedPrs: 1234,
          openedPrs: 1221,
          otherPeopleCommentsOnUserPrs: 1232,
          userCommentsOnOtherPeoplePrs: 16524,
          commentsOnAllPrs: 1222,
          commentsOnOwnPrs: 12442,
          averageOtherPeopleCommentsOnUserPrs: 111,
          averageUserCommentsOnMergedPrs: 111
        };

        var createAnyUser = function(id, username, type) {
          return _.assign({
            id: id,
            username: username,
            type: type
          }, ANY_USER_STATS);
        };

        var ANY_USER_1 = createAnyUser(1, 'frapontillo', 'ORGANISATION');
        var ANY_USER_2 = createAnyUser(2, 'takecare', 'ORGANISATION');
        var ANY_USER_3 = createAnyUser(3, 'blundell', 'ORGANISATION');
        var ANY_USER_4 = createAnyUser(4, 'xrigau', 'ORGANISATION');
        var ANY_USER_5 = createAnyUser(5, 'somerandomeperson', 'ORGANISATION');

        var group1 = {
          name: '2016-05',
          users: [ANY_USER_1, ANY_USER_2, ANY_USER_3, ANY_USER_4, ANY_USER_5],
          externalAverage: createAnyUser(-3, 'EXTERNAL', 'EXTERNAL'),
          organisationAverage: createAnyUser(-2, 'ORGANISATION', 'ORGANISATION'),
          assignedAverage: createAnyUser(-1, 'ASSIGNED', 'ASSIGNED')
        };

        var group2 = {
          name: '2016-06',
          users: [ANY_USER_1, ANY_USER_2],
          externalAverage: createAnyUser(-3, 'EXTERNAL', 'EXTERNAL'),
          organisationAverage: createAnyUser(-2, 'ORGANISATION', 'ORGANISATION'),
          assignedAverage: createAnyUser(-1, 'ASSIGNED', 'ASSIGNED')
        };

        var statsFromHttp = {groups: [group1, group2]};
        spyOnAndReturnPromise(mockReports, 'getPrStats', statsFromHttp);
      });

      it('should get the stats from the reports library', function(done) {
        showPrStats().then(done);

        expect(mockReports.getPrStats)
          .toHaveBeenCalledWith(ANY_START_DATE, ANY_END_DATE, ANY_REPOSITORIES, ANY_GROUP_BY, ANY_WITH_AVERAGE);
      });

      it('should concatenate each group into a single array', function(done) {
        showPrStats()
          .then(function(actual) {
            expect(actual.length).toBe(13);
          })
          .then(done);
      });

      it('should return an ordered array whose grouped elements have the same first element', function(done) {
        showPrStats()
          .then(function(actual) {
            actual.slice(0, 8).forEach(function(user) {
              expect(user[0]).toBe('2016-05');
            });
            actual.slice(8, 13).forEach(function(user) {
              expect(user[0]).toBe('2016-06');
            });
          })
          .then(done);
      });

      it('should create a new sheet with a valid name', function(done) {
        showPrStats()
          .then(function() {
            expect(mockSpreadsheet.createNewSheet).toHaveBeenCalledWith(jasmine.stringMatching(/^PR Stats.+$/));
          })
          .then(done);
      });

      it('should clear the stats sheet', function(done) {
        showPrStats()
          .then(function() {
            expect(mockSheet.clear).toHaveBeenCalled();
          })
          .then(done);
      });

      it('should set the header into the stats sheet', function(done) {
        showPrStats()
          .then(function() {
            expect(mockSheet.setValues).toHaveBeenCalledWith(1, 1, 1, 11, jasmine.any(Array));
          })
          .then(done);
      });

      it('should make the header line bold', function(done) {
        showPrStats()
          .then(function() {
            expect(mockSheet.setBold).toHaveBeenCalledWith(1, 1, 1, 11);
          })
          .then(done);
      });

      it('should make the first two columns bold', function(done) {
        showPrStats()
          .then(function() {
            expect(mockSheet.setBold).toHaveBeenCalledWith(2, 1, 13, 2);
          })
          .then(done);
      });

      it('should align the header cells to center and middle', function(done) {
        showPrStats()
          .then(function() {
            expect(mockSheet.alignToCenterMiddle).toHaveBeenCalledWith(1, 1, 1, 11);
          })
          .then(done);
      });

      it('should make the header text wrap', function(done) {
        showPrStats()
          .then(function() {
            expect(mockSheet.setWrap).toHaveBeenCalledWith(1, 1, 1, 11, true);
          })
          .then(done);
      });

      it('should freeze the first row and the first two columns', function(done) {
        showPrStats()
          .then(function() {
            expect(mockSheet.setFrozenRows).toHaveBeenCalledWith(1);
            expect(mockSheet.setFrozenColumns).toHaveBeenCalledWith(2);
          })
          .then(done);
      });

      it('should set the bottom border for every group', function(done) {
        showPrStats()
          .then(function() {
            expect(mockSheet.setBottomBorder).toHaveBeenCalledWith(9, 1, 1, 11);
            expect(mockSheet.setBottomBorder).toHaveBeenCalledWith(14, 1, 1, 11);
          })
          .then(done);
      });

      it('should set a background for all averages in every group', function(done) {
        showPrStats()
          .then(function() {
            expect(mockSheet.setBackground).toHaveBeenCalledWith(7, 1, 3, 11, jasmine.any(String));
            expect(mockSheet.setBackground).toHaveBeenCalledWith(12, 1, 3, 11, jasmine.any(String));
          })
          .then(done);
      });

      var groupInHeaderTester = function(group) {
        return {
          asymmetricMatch: function(actual) {
            return actual[0][0] === group;
          }
        };
      };

      it('should set the group header as "Year-Month" into the stats sheet', function(done) {
        showPrStatsWithGroupBy('MONTH')
          .then(function() {
            expect(mockSheet.setValues).toHaveBeenCalledWith(1, 1, 1, 11, groupInHeaderTester('Year-Month'));
          })
          .then(done);
      });

      it('should set the group header as "Year-Week" into the stats sheet', function(done) {
        showPrStatsWithGroupBy('WEEK')
          .then(function() {
            expect(mockSheet.setValues).toHaveBeenCalledWith(1, 1, 1, 11, groupInHeaderTester('Year-Week'));
          })
          .then(done);
      });

      it('should set an empty group header as into the stats sheet', function(done) {
        showPrStatsWithGroupBy('ANYTHING_ELSE')
          .then(function() {
            expect(mockSheet.setValues).toHaveBeenCalledWith(1, 1, 1, 11, groupInHeaderTester(''));
          })
          .then(done);
      });

      it('should set the results into the stats sheet', function(done) {
        showPrStats()
          .then(function(results) {
            expect(mockSheet.setValues).toHaveBeenCalledWith(2, 1, 13, 11, results);
          })
          .then(done);
      });

      it('should not show an error alert', function(done) {
        showPrStats()
          .then(function() {
            expect(mockSpreadsheet.showAlert).not.toHaveBeenCalled();
          })
          .then(done);
      });

    });

    describe('on failure', function() {

      // setup mockReports
      beforeEach(function() {
        spyOn(mockReports, 'getPrStats').and.returnValue(Promise.reject(new Error('lol you failed')));
      });

      it('should show an alert when the stats generation fails', function(done) {
        showPrStats()
          .then(function() {
            expect(mockSpreadsheet.showAlert).toHaveBeenCalled();
          })
          .then(done);
      });

    });

  });

  describe('showAggregatedStats', function() {

    var ANY_START_DATE = '2016-01-01';
    var ANY_END_DATE = '2016-07-31';
    var ANY_USERS = ['frapontillo', 'takecare', 'slacker'];

    var mockSheet;

    // setup mockSpreadsheet
    beforeEach(function() {
      mockSheet = new MockSheet();
      mockSpreadsheet.createNewSheet.and.returnValue(mockSheet);
    });

    var showAggregatedStats = function() {
      return main.showAggregatedStats(ANY_START_DATE, ANY_END_DATE, ANY_USERS);
    };

    describe('on success', function() {

      beforeEach(function() {
        var statsFromHttp = {
          'usersStats': {
            'frapontillo': {
              'assignedProjectsStats': {
                'R & D: Scheduled': 386
              },
              'assignedProjectsContributions': 386,
              'externalRepositoriesStats': {
                'sqlite-provider': 11,
                'all-4-android-tv-feature': 20
              },
              'externalRepositoriesContributions': 31
            },
            'takecare': {
              'assignedProjectsStats': {
                'R & D: Scheduled': 338,
                'All-4': 2
              },
              'assignedProjectsContributions': 340,
              'externalRepositoriesStats': {},
              'externalRepositoriesContributions': 0
            },
            'slacker': {
              'assignedProjectsStats': {
              },
              'assignedProjectsContributions': 0,
              'externalRepositoriesStats': {
                'merlin': 1
              },
              'externalRepositoriesContributions': 1
            }
          }
        };
        spyOnAndReturnPromise(mockReports, 'getAggregatedUserStats', statsFromHttp);
      });

      it('should get the stats from the reports library', function(done) {
        showAggregatedStats().then(done);

        expect(mockReports.getAggregatedUserStats)
          .toHaveBeenCalledWith(ANY_START_DATE, ANY_END_DATE, ANY_USERS);
      });

      it('should convert every user into an array element', function(done) {
        showAggregatedStats()
          .then(function(actual) {
            expect(actual.length).toBe(3);
          })
          .then(done);
      });

      it('should return items with the username as first element', function(done) {
        showAggregatedStats()
          .then(function(actual) {
            expect(actual[0][0]).toBe('frapontillo');
            expect(actual[1][0]).toBe('takecare');
            expect(actual[2][0]).toBe('slacker');
          })
          .then(done);
      });

      it('should return an item with one-line description of projects as second element if one project was contributed to', function(done) {
        showAggregatedStats()
          .then(function(actual) {
            expect(actual[0][1]).toBe('R & D: Scheduled (386)');
          })
          .then(done);
      });

      it('should return an item with multiple lines of description of projects as second element if multiple projects were contributed to', function(done) {
        showAggregatedStats()
          .then(function(actual) {
            expect(actual[1][1]).toBe(
              'R & D: Scheduled (338)\n' +
              'All-4 (2)'
            );
          })
          .then(done);
      });

      it('should return an item with an empty line as description of projects as second element if no project was contributed to', function(done) {
        showAggregatedStats()
          .then(function(actual) {
            expect(actual[2][1]).toBe('');
          })
          .then(done);
      });

      it('should return items with the projects contributions count as third element', function(done) {
        showAggregatedStats()
          .then(function(actual) {
            expect(actual[0][2]).toBe(386);
            expect(actual[1][2]).toBe(340);
            expect(actual[2][2]).toBe(0);
          })
          .then(done);
      });

      it('should return an item with one-line description of repositories as fourth element if one repository were contributed to', function(done) {
        showAggregatedStats()
          .then(function(actual) {
            expect(actual[2][3]).toBe('merlin (1)');
          })
          .then(done);
      });

      it('should return an item with multiple lines of description of repositories as fourth element if multiple repositories were contributed to', function(done) {
        showAggregatedStats()
          .then(function(actual) {
            expect(actual[0][3]).toBe(
              'sqlite-provider (11)\n' +
              'all-4-android-tv-feature (20)'
            );
          })
          .then(done);
      });

      it('should return an item with an empty line as description of repositories as fourth element if no repository was contributed to', function(done) {
        showAggregatedStats()
          .then(function(actual) {
            expect(actual[1][3]).toBe('');
          })
          .then(done);
      });

      it('should return items with the repositories contributions count as fifth element', function(done) {
        showAggregatedStats()
          .then(function(actual) {
            expect(actual[0][4]).toBe(31);
            expect(actual[1][4]).toBe(0);
            expect(actual[2][4]).toBe(1);
          })
          .then(done);
      });

      it('should create a new sheet with a valid name', function(done) {
        showAggregatedStats()
          .then(function() {
            expect(mockSpreadsheet.createNewSheet).toHaveBeenCalledWith(jasmine.stringMatching(/^Aggregated Stats.+$/));
          })
          .then(done);
      });

      it('should clear the stats sheet', function(done) {
        showAggregatedStats()
          .then(function() {
            expect(mockSheet.clear).toHaveBeenCalled();
          })
          .then(done);
      });

      it('should set the header into the stats sheet', function(done) {
        showAggregatedStats()
          .then(function() {
            expect(mockSheet.setValues).toHaveBeenCalledWith(1, 1, 2, 5, jasmine.any(Array));
          })
          .then(done);
      });

      it('should make the header lines bold', function(done) {
        showAggregatedStats()
          .then(function() {
            expect(mockSheet.setBold).toHaveBeenCalledWith(1, 1, 2, 5);
          })
          .then(done);
      });

      it('should make the first column bold', function(done) {
        showAggregatedStats()
          .then(function() {
            expect(mockSheet.setBold).toHaveBeenCalledWith(3, 1, 3, 1);
          })
          .then(done);
      });

      it('should align the header cells to center and middle', function(done) {
        showAggregatedStats()
          .then(function() {
            expect(mockSheet.alignToCenterMiddle).toHaveBeenCalledWith(1, 1, 2, 5);
          })
          .then(done);
      });

      it('should align the data cells to middle', function(done) {
        showAggregatedStats()
          .then(function() {
            expect(mockSheet.alignToMiddle).toHaveBeenCalledWith(3, 1, 3, 5);
          })
          .then(done);
      });

      it('should merge the assigned and non-assigned header cells', function(done) {
        showAggregatedStats()
          .then(function() {
            expect(mockSheet.mergeRange).toHaveBeenCalledWith(1, 2, 1, 2);
            expect(mockSheet.mergeRange).toHaveBeenCalledWith(1, 4, 1, 2);
          })
          .then(done);
      });

      it('should make the header text wrap', function(done) {
        showAggregatedStats()
          .then(function() {
            expect(mockSheet.setWrap).toHaveBeenCalledWith(1, 1, 2, 5, true);
          })
          .then(done);
      });

      it('should freeze the first two rows and the first column', function(done) {
        showAggregatedStats()
          .then(function() {
            expect(mockSheet.setFrozenRows).toHaveBeenCalledWith(2);
            expect(mockSheet.setFrozenColumns).toHaveBeenCalledWith(1);
          })
          .then(done);
      });

      it('should set the results into the stats sheet', function(done) {
        showAggregatedStats()
          .then(function(results) {
            expect(mockSheet.setValues).toHaveBeenCalledWith(3, 1, 3, 5, results);
          })
          .then(done);
      });

      it('should not show an error alert', function(done) {
        showAggregatedStats()
          .then(function() {
            expect(mockSpreadsheet.showAlert).not.toHaveBeenCalled();
          })
          .then(done);
      });

    });

    describe('on failure', function() {

      // setup mockReports
      beforeEach(function() {
        spyOn(mockReports, 'getAggregatedUserStats').and.returnValue(Promise.reject(new Error('lol you failed')));
      });

      it('should show an alert when the stats generation fails', function(done) {
        showAggregatedStats()
          .then(function() {
            expect(mockSpreadsheet.showAlert).toHaveBeenCalled();
          })
          .then(done);
      });

    });

  });

});
