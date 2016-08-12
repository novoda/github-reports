import {Component, Input, OnChanges, SimpleChanges, HostBinding} from '@angular/core';

@Component({
  selector: 'work-user',
  templateUrl: 'work-user.component.html',
  styleUrls: ['work-user.component.scss']
})
export class WorkUserComponent implements OnChanges {

  @Input() user: any;

  @HostBinding('class.bad') isBad: boolean = false;

  constructor() {
  }

  ngOnChanges(changes: SimpleChanges): void {
    const total = this.user.assignedCount + this.user.externalCount;
    this.isBad = total <= 0;
  }

}
