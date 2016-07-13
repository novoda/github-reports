'use strict';

describe('Main', function() {

  var mockHttp, mockReports, mockGeometry, mockDataSheet, mockInputSheet, mockStatsSheet, main;

  beforeEach(function() {
    mockHttp = new AppsMockHttp();
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

        expect(actual[0].length).toBe(6);
        expect(actual[1].length).toBe(3);
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
        var ANY_GROUP_BY = 'MONTH';
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
          if (col == 4) {
            return ANY_GROUP_BY;
          }
          if (col == 5) {
            return ANY_WITH_AVERAGE;
          }
          throw new Error('Unexpected range in getValue');
        });
        mockInputSheet.getColumnValues.and.callFake(function(startRow, col) {
          if (col == 3) {
            return ANY_REPOSITORIES;
          }
          throw new Error('Unexpected range in getColumnValues');
        });

        spyOn(main, 'getPrStats');
        main.updatePrStats();

        expect(main.getPrStats).toHaveBeenCalledWith(
          START_DATE, END_DATE, ANY_REPOSITORIES, ANY_GROUP_BY, ANY_WITH_AVERAGE
        );
      });

      it('should clear the stats sheet', function() {
        main.updatePrStats();

        expect(mockStatsSheet.clear).toHaveBeenCalled();
      });

      it('should flatten the results from getPrStats and set them into the stats sheet', function() {
        main.updatePrStats();

        expect(mockStatsSheet.setValues).toHaveBeenCalledWith(1, 1, 9, 11, jasmine.any(Array));
      });

    });

  });

  describe('updateAll', function() {

    beforeEach(function() {
      spyOn(mockReports, 'getRepositories').and.returnValue([]);
    });

    it('should call all the relevant endpoints', function() {
      main.updateAll();

      expect(mockReports.getRepositories).toHaveBeenCalled();
    });

    it('should set returned data in the data sheet', function() {
      main.updateAll();

      expect(mockDataSheet.setValues).toHaveBeenCalledTimes(1);
      expect(mockDataSheet.setValues).toHaveBeenCalledWith(2, 1, 0, 1, []);
    });

  });

});
