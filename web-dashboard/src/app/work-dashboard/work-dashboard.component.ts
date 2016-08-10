import {Component, OnInit} from "@angular/core";
import {SystemClock} from "../system-clock";
import {WeekCalculatorService} from "../week-calculator.service";
import {WorkUserComponent} from "../work-user/work-user.component";
import {ReportsServiceClient} from "../reports-service-client.service";

@Component({
  moduleId: module.id,
  selector: 'app-work-dashboard',
  templateUrl: 'work-dashboard.component.html',
  styleUrls: ['work-dashboard.component.css'],
  directives: [WorkUserComponent]
})
export class WorkDashboardComponent implements OnInit {

  private stats: any;

  constructor(private weekCalculator: WeekCalculatorService,
              private clock: SystemClock,
              private reportsServiceClient: ReportsServiceClient) {
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
