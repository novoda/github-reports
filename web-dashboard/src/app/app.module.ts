import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AppComponent } from './app.component';
import { PageNotFoundComponent } from './page-not-found/page-not-found.component';
import { ContributorsVsSlackersDashboardComponent } from './contributors-vs-slackers-dashboard/contributors-vs-slackers-dashboard.component';
import { ContributorComponent } from './contributor/contributor.component';
import { ReportsClient } from './reports/reports-client.service';
import { WeekCalculator } from './week-calculator.service';
import { SystemClock } from './system-clock';
import { routing } from './app.routes';
import { HttpModule } from '@angular/http';
import { reportsServiceProvider } from './reports/reports.service.provider';
import { ContributorsComponent } from './contributors/contributors.component';
import { SlackerComponent } from './slacker/slacker.component';
import { SlackersComponent } from './slackers/slackers.component';
import { ConfigService } from './config.service';
import { TimezoneDetectorService } from './timezone-detector.service';

@NgModule({
  imports: [
    BrowserModule,
    CommonModule,
    FormsModule,
    HttpModule,
    routing
  ],
  declarations: [
    AppComponent,
    PageNotFoundComponent,
    ContributorsVsSlackersDashboardComponent,
    ContributorsComponent,
    SlackersComponent,
    ContributorComponent,
    SlackerComponent
  ],
  providers: [
    SystemClock,
    WeekCalculator,
    ConfigService,
    reportsServiceProvider,
    ReportsClient,
    TimezoneDetectorService
  ],
  entryComponents: [AppComponent],
  bootstrap: [AppComponent]
})
export class AppModule {

}
