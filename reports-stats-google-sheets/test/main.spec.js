'use strict';

describe('Main', function() {

  var mockHttp, mockReports, mockGeometry, mockDataSheet, mockInputSheet, mockStatsSheet, main;

  beforeEach(function() {
    mockHttp = new MockHttp();
    mockReports = new Reports(mockHttp);
    mockDataSheet = new MockSheet();
    mockInputSheet = new MockSheet();
    mockStatsSheet = new MockSheet();
    mockGeometry = new Geometry();

    main = new Main(mockReports, mockGeometry, mockDataSheet, mockInputSheet, mockStatsSheet);
  });

  describe('PR stats', function() {

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

      var ANY_USER_1 = createAnyUser(1, 'frapontillo', 'FILTER');
      var ANY_USER_2 = createAnyUser(2, 'takecare', 'FILTER');
      var ANY_USER_3 = createAnyUser(3, 'blundell', 'ASSIGNED');
      var ANY_USER_4 = createAnyUser(4, 'xrigau', 'TEAM');
      var ANY_USER_5 = createAnyUser(5, 'somerandomeperson', 'EXTERNAL');

      var group1 = {
        name: '2016-05',
        users: [ANY_USER_1, ANY_USER_2, ANY_USER_3, ANY_USER_4, ANY_USER_5],
        externalAverage: createAnyUser(-4, 'EXTERNAL', 'EXTERNAL'),
        teamAverage: createAnyUser(-3, 'TEAM', 'TEAM'),
        assignedAverage: createAnyUser(-2, 'ASSIGNED', 'ASSIGNED'),
        filterAverage: createAnyUser(-1, 'FILTER', 'FILTER')
      };

      var group2 = {
        name: '2016-06',
        users: [ANY_USER_1, ANY_USER_2],
        externalAverage: createAnyUser(-4, 'EXTERNAL', 'EXTERNAL'),
        teamAverage: createAnyUser(-3, 'TEAM', 'TEAM'),
        assignedAverage: createAnyUser(-2, 'ASSIGNED', 'ASSIGNED'),
        filterAverage: createAnyUser(-1, 'FILTER', 'FILTER')
      };

      var statsFromHttp = {groups: [group1, group2]};
      spyOn(mockReports, 'getPrStats').and.returnValue(statsFromHttp);
    });

    describe('getPrStats', function() {

      it('should get the stats from the reports library', function() {
        main.getPrStats();

        expect(mockReports.getPrStats).toHaveBeenCalled();
      });

      it('should transform each group into an array', function() {
        var actual = main.getPrStats();

        expect(actual.length).toBe(2);
        expect(actual[0] instanceof Array).toBe(true);
        expect(actual[1] instanceof Array).toBe(true);
      });

      it('should transform each group into an array with the right number of elements', function() {
        var actual = main.getPrStats();

        expect(actual[0].length).toBe(9);
        expect(actual[1].length).toBe(6);
      });

      it('should transform each group into an array of arrays having the same first element', function() {
        var actual = main.getPrStats();

        actual[0].forEach(function(user) {
          expect(user[0]).toBe('2016-05');
        });
        actual[1].forEach(function(user) {
          expect(user[0]).toBe('2016-06');
        });
      });

    });

    describe('updatePrStats', function() {

      it('should call getPrStats with data from the input sheet', function() {
        var START_DATE = '2016-01-01';
        var END_DATE = '2016-07-31';
        var ANY_REPOSITORIES = ['all-4', 'merlin'];
        var ANY_PROJECTS = ['pt', 'oddschecker'];
        var ANY_FILTER = ['frapontillo', 'takecare'];
        var ANY_GROUP_BY = 'month';
        var ANY_WITH_AVERAGE = 'TRUE';

        mockInputSheet.getValueAsDate.and.callFake(function(row, col) {
          if (col == 1) {
            return START_DATE;
          }
          if (col == 2) {
            return END_DATE;
          }
          throw new Error('Unexpected range in getValue');
        });
        mockInputSheet.getValue.and.callFake(function(row, col) {
          if (col == 6) {
            return ANY_GROUP_BY;
          }
          if (col == 7) {
            return ANY_WITH_AVERAGE;
          }
          throw new Error('Unexpected range in getValue');
        });
        mockInputSheet.getColumnValues.and.callFake(function(startRow, col) {
          if (col == 3) {
            return ANY_REPOSITORIES;
          }
          if (col == 4) {
            return ANY_PROJECTS;
          }
          if (col == 5) {
            return ANY_FILTER;
          }
          throw new Error('Unexpected range in getColumnValues');
        });

        spyOn(main, 'getPrStats');
        main.updatePrStats();

        expect(main.getPrStats).toHaveBeenCalledWith(
          START_DATE, END_DATE, ANY_REPOSITORIES, ANY_PROJECTS, ANY_FILTER, ANY_GROUP_BY, ANY_WITH_AVERAGE
        );
      });

      it('should clear the stats sheet', function() {
        main.updatePrStats();

        expect(mockStatsSheet.clear).toHaveBeenCalled();
      });

      it('should flatten the results from getPrStats and set them into the stats sheet', function() {
        main.updatePrStats();

        expect(mockStatsSheet.setValues).toHaveBeenCalledWith(1, 1, 9 + 6, 11, jasmine.any(Array));
      });

    });

  });

  describe('updateAll', function() {

    beforeEach(function() {
      spyOn(mockReports, 'getProjects').and.returnValue([]);
      spyOn(mockReports, 'getRepositories').and.returnValue([]);
      spyOn(mockReports, 'getUsersTeam').and.returnValue([]);
    });

    it('should call all the relevant endpoints', function() {
      main.updateAll();

      expect(mockReports.getProjects).toHaveBeenCalled();
      expect(mockReports.getRepositories).toHaveBeenCalled();
      expect(mockReports.getUsersTeam).toHaveBeenCalled();
    });

    it('should set returned data in the data sheet', function() {
      main.updateAll();

      expect(mockDataSheet.setValues).toHaveBeenCalledTimes(3);
      expect(mockDataSheet.setValues).toHaveBeenCalledWith(2, 1, 0, 1, []);
      expect(mockDataSheet.setValues).toHaveBeenCalledWith(2, 2, 0, 1, []);
      expect(mockDataSheet.setValues).toHaveBeenCalledWith(2, 3, 0, 1, []);
    });

  });

});
