'use strict';

/* exported MockSheet */
function MockSheet() {

  MockSheet.prototype.getValue = jasmine.createSpy('getValue');

  MockSheet.prototype.getValueAsDate = jasmine.createSpy('getValueAsDate');

  MockSheet.prototype.getColumnValues = jasmine.createSpy('getColumnValues');

  MockSheet.prototype.setValues = jasmine.createSpy('setValues');

  MockSheet.prototype.clear = jasmine.createSpy('clear');

}
