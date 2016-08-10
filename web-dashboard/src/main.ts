import {bootstrap} from '@angular/platform-browser-dynamic';
import {enableProdMode} from '@angular/core';
import {HTTP_PROVIDERS, Http} from '@angular/http';
import {environment, AppComponent, appRouterProviders, SystemClock, WeekCalculatorService, ReportsService} from './app/';
import {ReportsMockService} from "./app/reports-mock.service";
import {ReportsServiceClient} from "./app/reports-service-client.service";

if (environment.production) {
  enableProdMode();
}

bootstrap(AppComponent, [
  HTTP_PROVIDERS,
  appRouterProviders,
  SystemClock,
  WeekCalculatorService,
  {
    provide: ReportsService,
    useFactory: (http) => environment.production ? new ReportsService(http) : new ReportsMockService(),
    deps: [Http]
  },
  ReportsServiceClient
]).catch(console.error);
