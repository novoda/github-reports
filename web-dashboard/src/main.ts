import {bootstrap} from '@angular/platform-browser-dynamic';
import {enableProdMode} from '@angular/core';
import {HTTP_PROVIDERS} from '@angular/http';
import {environment, AppComponent, appRouterProviders, SystemClock, WeekCalculatorService, ReportsService} from './app/';
import {ReportsMockService} from "./app/reports-mock.service";

if (environment.production) {
  enableProdMode();
}

bootstrap(AppComponent, [
  HTTP_PROVIDERS,
  appRouterProviders,
  SystemClock,
  WeekCalculatorService,
  ReportsService,
  ReportsMockService
]).catch(console.error);
