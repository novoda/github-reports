import {Component, Input} from '@angular/core';

@Component({
  selector: 'work-user',
  templateUrl: 'work-user.component.html',
  styleUrls: ['work-user.component.scss']
})
export class WorkUserComponent {

  @Input() user: any;

  constructor() {
  }

  getLevel(user: any): string {
    const total = user.assignedCount + user.externalCount;
    return total > 0 ? 'good' : 'bad';
  }

}
