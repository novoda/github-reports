import {Component, OnInit} from "@angular/core";
import {ReportsService} from "../reports.service";
import {SystemClock} from "../system-clock";
import {WeekCalculatorService} from "../week-calculator.service";
import {ReportsMockService} from "../reports-mock.service";
import {WorkUserComponent} from "../work-user/work-user.component";

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
              private reportsService: ReportsMockService) {
    // noop
  }

  ngOnInit() {

    this.reportsService.getAggregatedStats()
      .subscribe(stats => {
      this.stats = stats;
    });
    /*this.reportsService.getAggregatedStats(
      this.weekCalculator.getLastMonday(),
      this.clock.getDate()
    ).subscribe(stats => {
      this.stats = stats;
    });*/
  }

}
