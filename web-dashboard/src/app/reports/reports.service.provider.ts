import { ReportsService } from './reports.service';
import { Http } from '@angular/http';
import { environment } from '../environments/environment';
import { ReportsMockService } from './reports-mock.service';

let reportsServiceFactory = (http: Http): any => {
  if (environment.production) {
    return new ReportsService(http);
  }
  return new ReportsMockService();
};

export const reportsServiceProvider = {
  provide: ReportsService,
  useFactory: reportsServiceFactory,
  deps: [Http]
};
