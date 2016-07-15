'use strict';

describe('Main', function() {

  var mockHttp, mockReports, mockSpreadsheet, main;

  var ANY_START_DATE = '2016-01-01';
  var ANY_END_DATE = '2016-07-31';
  var ANY_REPOSITORIES = ['all-4', 'merlin'];
  var ANY_GROUP_BY = 'MONTH';
  var ANY_WITH_AVERAGE = true;

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
            expect(actual.length).toBe(9);
          })
          .then(done);
      });

      it('should return an ordered array whose grouped elements have the same first element', function(done) {
        showPrStats()
          .then(function(actual) {
            actual.slice(0, 6).forEach(function(user) {
              expect(user[0]).toBe('2016-05');
            });
            actual.slice(6, 9).forEach(function(user) {
              expect(user[0]).toBe('2016-06');
            });
          })
          .then(done);
      });

      it('should create a new sheet with a valid name', function(done) {
        showPrStats()
          .then(function() {
            expect(mockSpreadsheet.createNewSheet).toHaveBeenCalledWith(jasmine.stringMatching(/^.+$/));
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
            expect(mockSheet.setValues).toHaveBeenCalledWith(2, 1, 9, 11, results);
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

});
