import {Component, Input} from '@angular/core';

@Component({
  moduleId: module.id,
  selector: 'work-user',
  templateUrl: 'work-user.component.html',
  styleUrls: ['work-user.component.css']
})
export class WorkUserComponent {

  constructor() { }

  @Input() user: any;

  getLevel(user: any): string {
    const total = user.assignedCount + user.externalCount;
    return total > 100 ? 'good' : 'bad';
  }

}
