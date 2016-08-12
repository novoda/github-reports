import { Component, Input } from '@angular/core';
import { UserStats } from '../reports/user-stats';

@Component({
  selector: 'app-contributors',
  templateUrl: 'contributors.component.html',
  styleUrls: ['contributors.component.scss']
})
export class ContributorsComponent {

  @Input() contributors: Array<UserStats>;

  constructor() {
  }

}
