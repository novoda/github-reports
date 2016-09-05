import { Component, OnInit, OnDestroy } from '@angular/core';
import { SystemClock } from '../system-clock';
import { WeekCalculator } from '../week-calculator.service';
import { ReportsClient } from '../reports/reports-client.service';
import { CompanyStats } from '../reports/company-stats';
import { UserStats } from '../reports/user-stats';
import { Subscription, Observable } from 'rxjs';
import { OnErrorIgnoreOperator } from '../shared/OnErrorIgnoreOperator';
import { TimezoneDetectorService } from '../timezone-detector.service';

@Component({
  selector: 'contributors-vs-slackers-dashboard',
  templateUrl: 'contributors-vs-slackers-dashboard.component.html',
  styleUrls: ['contributors-vs-slackers-dashboard.component.scss']
})
export class ContributorsVsSlackersDashboardComponent implements OnInit, OnDestroy {

  static NUMBER_OF_CONTRIBUTORS = 5;
  static REFRESH_RATE_IN_MILLISECONDS = 30 * 1000;

  public contributors: Array<UserStats>;
  public slackers: Array<UserStats>;
  public subscription: Subscription;

  constructor(private weekCalculator: WeekCalculator,
              private clock: SystemClock,
              private reportsServiceClient: ReportsClient,
              private timezoneDetector: TimezoneDetectorService) {
    // noop
  }

  ngOnInit() {
    this.subscription = Observable
      .timer(0, ContributorsVsSlackersDashboardComponent.REFRESH_RATE_IN_MILLISECONDS)
      .switchMap(() => {
        return this.getCompanyStats();
      })
      .subscribe((stats: CompanyStats) => {
        this.contributors = stats.contributors;
        this.slackers = stats.slackers;
      });
  }

  private getCompanyStats(): Observable<CompanyStats> {
    return this.reportsServiceClient
      .getCompanyStats(
        this.weekCalculator.getLastMonday(),
        this.clock.getDate(),
        this.timezoneDetector.getTimezone()
      )
      .lift(new OnErrorIgnoreOperator<CompanyStats>());
  }

  ngOnDestroy(): void {
    if (!this.subscription.isUnsubscribed) {
      this.subscription.unsubscribe();
    }
  }

}
