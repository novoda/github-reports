/* tslint:disable:no-unused-variable */

import {addProviders, inject} from "@angular/core/testing";
import {WorkDashboardComponent} from "./work-dashboard.component";
import {SystemClock} from "../system-clock";
import {WeekCalculatorService} from "../week-calculator.service";
import {ReportsService} from "../reports.service";
import {ReportsMockService} from "../reports-mock.service";
import {ReportsServiceClient} from "../reports-service-client.service";

describe('Component: WorkDashboard', () => {

  let clock: SystemClock;
  let weekCalculator: WeekCalculatorService;
  let reportsServiceClient: ReportsServiceClient;
  let component;

  beforeEach(() => {
    addProviders([SystemClock, WeekCalculatorService, ReportsService]);
  });

  beforeEach(inject([SystemClock, WeekCalculatorService], (_clock_, _weekCalculator_, _reportsService_) => {
    clock = _clock_;
    weekCalculator = _weekCalculator_;
    reportsServiceClient = new ReportsServiceClient(_reportsService_);
  }));

  beforeEach(() => {
    component = new WorkDashboardComponent(weekCalculator, clock, reportsServiceClient);
  });

  it('creates an instance', () => {
    expect(component).toBeTruthy();
  });

});
