import {Component, OnInit, Input} from '@angular/core';

@Component({
  moduleId: module.id,
  selector: 'work-user',
  templateUrl: 'work-user.component.html',
  styleUrls: ['work-user.component.css']
})
export class WorkUserComponent {

  constructor() { }

  @Input() user: any;

}
