import { Component, OnInit } from '@angular/core';
import { SystemClock } from '../system-clock';
import { WeekCalculator } from '../week-calculator.service';
import { ReportsClient } from '../reports/reports-client.service';
import { CompanyStats } from '../reports/company-stats';
import { UserStats } from '../reports/user-stats';

@Component({
  selector: 'contributors-vs-slackers-dashboard',
  templateUrl: 'contributors-vs-slackers-dashboard.component.html',
  styleUrls: ['contributors-vs-slackers-dashboard.component.scss']
})
export class ContributorsVsSlackersDashboardComponent implements OnInit {

  static NUMBER_OF_CONTRIBUTORS = 5;

  private contributors: Array<UserStats>;
  private slackers: Array<UserStats>;

  constructor(private weekCalculator: WeekCalculator,
              private clock: SystemClock,
              private reportsServiceClient: ReportsClient) {
    // noop
  }

  ngOnInit() {
    this.reportsServiceClient
      .getCompanyStats(
        this.weekCalculator.getLastMonday(),
        this.clock.getDate()
      )
      .subscribe((stats: CompanyStats) => {
        this.contributors = this.pickRandomContributors(stats);
        this.slackers = stats.slackers;
      });
  }

  private pickRandomContributors(stats: CompanyStats): Array<UserStats> {
    let contributors = this.cloneArray(stats.contributors);

    let randomContributors: Array<UserStats> = new Array(ContributorsVsSlackersDashboardComponent.NUMBER_OF_CONTRIBUTORS);
    for (let index = 0; index < ContributorsVsSlackersDashboardComponent.NUMBER_OF_CONTRIBUTORS; index++) {
      randomContributors[index] = this.pickAndRemoveRandomContributor(contributors);
    }

    return randomContributors;
  }

  private cloneArray(array: Array<any>) {
    return array.slice(0);
  }

  private pickAndRemoveRandomContributor(users: Array<UserStats>): UserStats {
    const index = Math.floor(Math.random() * users.length);
    const user = users[index];
    users.splice(index, 1);
    return user;
  }

}
