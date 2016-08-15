import { Component, Input } from '@angular/core';
import { UserStats } from '../reports/user-stats';

@Component({
  selector: 'app-slacker',
  templateUrl: 'slacker.component.html',
  styleUrls: ['slacker.component.scss']
})
export class SlackerComponent {

  @Input() user: UserStats;

  constructor() {
  }

}
