/* tslint:disable:no-unused-variable */

import {addProviders, inject} from '@angular/core/testing';
import {WorkDashboardComponent} from './work-dashboard.component';
import {SystemClock} from '../system-clock';
import {WeekCalculator} from '../week-calculator.service';
import {ReportsService} from '../reports/reports.service';
import {ReportsClient} from '../reports/reports-client.service';

describe('Component: WorkDashboard', () => {

  let clock: SystemClock;
  let weekCalculator: WeekCalculator;
  let reportsServiceClient: ReportsClient;
  let component;

  beforeEach(() => {
    addProviders([SystemClock, WeekCalculator, ReportsService]);
  });

  beforeEach(inject([SystemClock, WeekCalculator], (_clock_, _weekCalculator_, _reportsService_) => {
    clock = _clock_;
    weekCalculator = _weekCalculator_;
    reportsServiceClient = new ReportsClient(_reportsService_);
  }));

  beforeEach(() => {
    component = new WorkDashboardComponent(weekCalculator, clock, reportsServiceClient);
  });

  it('creates an instance', () => {
    expect(component).toBeTruthy();
  });

});
