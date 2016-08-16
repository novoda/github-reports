import { Component, OnInit, OnDestroy } from '@angular/core';
import { SystemClock } from '../system-clock';
import { WeekCalculator } from '../week-calculator.service';
import { ReportsClient } from '../reports/reports-client.service';
import { CompanyStats } from '../reports/company-stats';
import { UserStats } from '../reports/user-stats';
import { Subscription, Observable } from 'rxjs';

@Component({
  selector: 'contributors-vs-slackers-dashboard',
  templateUrl: 'contributors-vs-slackers-dashboard.component.html',
  styleUrls: ['contributors-vs-slackers-dashboard.component.scss']
})
export class ContributorsVsSlackersDashboardComponent implements OnInit, OnDestroy {

  static NUMBER_OF_CONTRIBUTORS = 4;
  static REFRESH_RATE_IN_MILLISECONDS = 30 * 1000;

  public contributors: Array<UserStats>;
  public slackers: Array<UserStats>;
  public subscription: Subscription;

  constructor(private weekCalculator: WeekCalculator,
              private clock: SystemClock,
              private reportsServiceClient: ReportsClient) {
    // noop
  }

  ngOnInit() {
    this.subscription = Observable
      .timer(0, ContributorsVsSlackersDashboardComponent.REFRESH_RATE_IN_MILLISECONDS)
      .map(() => {
        return this.getCompanyStats();
      })
      .switch()
      .subscribe((stats: CompanyStats) => {
        this.contributors = this.pickRandomContributors(stats);
        this.slackers = stats.slackers;
      });
  }

  private getCompanyStats(): Observable<CompanyStats> {
    return this.reportsServiceClient
      .getCompanyStats(
        this.weekCalculator.getLastMonday(),
        this.clock.getDate()
      )
      .retry();
  }

  ngOnDestroy(): void {
    if (!this.subscription.isUnsubscribed) {
      this.subscription.unsubscribe();
    }
  }

  pickRandomContributors(stats: CompanyStats): Array<UserStats> {
    const contributors = this.cloneArray(stats.contributors);

    const numberOfContributors = Math.min(contributors.length, ContributorsVsSlackersDashboardComponent.NUMBER_OF_CONTRIBUTORS);
    const randomContributors: Array<UserStats> = new Array(numberOfContributors);

    for (let index = 0; index < numberOfContributors; index++) {
      randomContributors[index] = this.pickAndRemoveRandomContributor(contributors);
    }

    return randomContributors;
  }

  cloneArray(array: Array<any>) {
    return array.slice(0);
  }

  pickAndRemoveRandomContributor(users: Array<UserStats>): UserStats {
    const usersOrEmpty = users || [];
    const index = Math.floor(Math.random() * usersOrEmpty.length);
    const user = usersOrEmpty[index];
    usersOrEmpty.splice(index, 1);
    return user;
  }

}
