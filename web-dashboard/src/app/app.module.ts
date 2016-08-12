import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AppComponent } from './app.component';
import { PageNotFoundComponent } from './page-not-found/page-not-found.component';
import { WorkDashboardComponent } from './work-dashboard/work-dashboard.component';
import { WorkUserComponent } from './work-user/work-user.component';
import { ReportsClient } from './reports/reports-client.service';
import { WeekCalculator } from './week-calculator.service';
import { SystemClock } from './system-clock';
import { routing } from './app.routes';
import { HttpModule } from '@angular/http';
import { reportsServiceProvider } from './reports/reports.service.provider';

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
    WorkDashboardComponent,
    WorkUserComponent
  ],
  providers: [
    SystemClock,
    WeekCalculator,
    reportsServiceProvider,
    ReportsClient
  ],
  entryComponents: [AppComponent],
  bootstrap: [AppComponent]
})
export class AppModule {

}
