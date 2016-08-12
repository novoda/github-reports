import { Component, Input } from '@angular/core';
import { UserStats } from '../reports/user-stats';

@Component({
  selector: 'app-slackers',
  templateUrl: 'slackers.component.html',
  styleUrls: ['slackers.component.scss']
})
export class SlackersComponent {

  @Input() slackers: Array<UserStats>;

  constructor() {
  }

}
