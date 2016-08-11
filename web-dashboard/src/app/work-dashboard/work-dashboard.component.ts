import {Component, OnInit} from '@angular/core';
import {SystemClock} from '../system-clock';
import {WeekCalculator} from '../week-calculator.service';
import {ReportsClient} from '../reports-client.service';

@Component({
  selector: 'app-work-dashboard',
  templateUrl: 'work-dashboard.component.html',
  styleUrls: ['work-dashboard.component.scss']
})
export class WorkDashboardComponent implements OnInit {

  private stats: any;

  constructor(private weekCalculator: WeekCalculator,
              private clock: SystemClock,
              private reportsServiceClient: ReportsClient) {
    // noop
  }

  ngOnInit() {
    this.reportsServiceClient
      .getAggregatedStats(
        this.weekCalculator.getLastMonday(),
        this.clock.getDate()
      )
      .subscribe(stats => {
        this.stats = stats;
      });
  }

}
