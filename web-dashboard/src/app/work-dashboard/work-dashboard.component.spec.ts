/* tslint:disable:no-unused-variable */

import {addProviders, inject} from "@angular/core/testing";
import {WorkDashboardComponent} from "./work-dashboard.component";
import {SystemClock} from "../system-clock";
import {WeekCalculatorService} from "../week-calculator.service";
import {ReportsService} from "../reports.service";
import {ReportsMockService} from "../reports-mock.service";

describe('Component: WorkDashboard', () => {

  let clock: SystemClock;
  let weekCalculator: WeekCalculatorService;
  let reportsService: ReportsMockService;
  let component;

  beforeEach(() => {
    addProviders([SystemClock, WeekCalculatorService]);
  });

  beforeEach(inject([SystemClock, WeekCalculatorService], (_clock_, _weekCalculator_) => {
    clock = _clock_;
    weekCalculator = _weekCalculator_;
    reportsService = new ReportsMockService();
  }));

  beforeEach(() => {
    component = new WorkDashboardComponent(weekCalculator, clock, reportsService);
  });

  it('creates an instance', () => {
    expect(component).toBeTruthy();
  });

});
