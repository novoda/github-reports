import { Component, Input } from '@angular/core';
import { UserStats } from '../reports/user-stats';

@Component({
  selector: 'app-contributor',
  templateUrl: 'contributor.component.html',
  styleUrls: ['contributor.component.scss']
})
export class ContributorComponent {

  @Input() user: UserStats;

  constructor() {
  }

}
