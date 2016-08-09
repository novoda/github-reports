/* tslint:disable:no-unused-variable */

import {addProviders, inject} from '@angular/core/testing';
import {WeekCalculatorService} from './week-calculator.service';
import {SystemClock} from './system-clock';

describe('Service: WeekCalculator', () => {

  let clock: SystemClock;
  let weekCalculatorService: WeekCalculatorService;

  beforeEach(() => {
    addProviders([SystemClock, WeekCalculatorService]);
  });

  beforeEach(inject([SystemClock, WeekCalculatorService], (_clock_: SystemClock, _weekCalculatorService_: WeekCalculatorService) => {
    clock = _clock_;
    weekCalculatorService = _weekCalculatorService_;
  }));

  it('should instantiate the week calculator', () => {
    expect(weekCalculatorService).toBeTruthy();
  });

  it('should return a date at midnight', () => {
    let monday: Date = weekCalculatorService.getLastMonday();

    expect(monday.getHours()).toBe(0);
    expect(monday.getMinutes()).toBe(0);
    expect(monday.getSeconds()).toBe(0);
    expect(monday.getMilliseconds()).toBe(0);
  });

  it('should return the same day, month and year, given a Monday', () => {
    let date = new Date(2016, 7, 8);
    spyOn(clock, 'getDate').and.returnValue(date);

    let monday = weekCalculatorService.getLastMonday();

    expect(monday.getFullYear()).toBe(2016);
    expect(monday.getMonth()).toBe(7);
    expect(monday.getDate()).toBe(8);
  });

  it('should return Monday 8 August 2016, given the next day', () => {
    let date = new Date(2016, 7, 9);
    spyOn(clock, 'getDate').and.returnValue(date);

    let monday = weekCalculatorService.getLastMonday();

    expect(monday.getFullYear()).toBe(2016);
    expect(monday.getMonth()).toBe(7);
    expect(monday.getDate()).toBe(8);
  });

  it('should return Monday 8 August 2016, given the next Thursday', () => {
    let date = new Date(2016, 7, 11);
    spyOn(clock, 'getDate').and.returnValue(date);

    let monday = weekCalculatorService.getLastMonday();

    expect(monday.getFullYear()).toBe(2016);
    expect(monday.getMonth()).toBe(7);
    expect(monday.getDate()).toBe(8);
  });

  it('should return Monday 8 August 2016, given the next Sunday', () => {
    let date = new Date(2016, 7, 14);
    spyOn(clock, 'getDate').and.returnValue(date);

    let monday = weekCalculatorService.getLastMonday();

    expect(monday.getFullYear()).toBe(2016);
    expect(monday.getMonth()).toBe(7);
    expect(monday.getDate()).toBe(8);
  });

});
