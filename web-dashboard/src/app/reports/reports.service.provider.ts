import { ReportsService } from './reports.service';
import { Http } from '@angular/http';
import { environment } from '../environments/environment';
import { ReportsMockService } from './reports-mock.service';
import { ConfigService } from '../config.service';

let reportsServiceFactory = (http: Http, configService: ConfigService): any => {
  if (environment.production) {
    return new ReportsService(http, configService);
  }
  return new ReportsMockService();
};

export const reportsServiceProvider = {
  provide: ReportsService,
  useFactory: reportsServiceFactory,
  deps: [Http, ConfigService]
};
