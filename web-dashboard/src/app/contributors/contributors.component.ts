import { Component, Input, OnInit, OnDestroy, ElementRef } from '@angular/core';
import { UserStats } from '../reports/user-stats';
import { Subscription, Observable } from 'rxjs';

@Component({
  selector: 'app-contributors',
  templateUrl: 'contributors.component.html',
  styleUrls: ['contributors.component.scss']
})
export class ContributorsComponent implements OnInit, OnDestroy {

  static ROTATE_RATE_IN_MILLISECONDS = 3 * 1000;

  private CONTRIBUTOR_POSITION = '--contributor-position';
  private CONTRIBUTOR_QTY = '--contributor-qty';

  animation: Subscription;
  position: number;
  element;

  @Input() contributors: Array<UserStats>;

  constructor(element: ElementRef) {
    this.element = element;
    this.position = 1;
  }

  ngOnInit(): void {
    this.animation = Observable
      .timer(ContributorsComponent.ROTATE_RATE_IN_MILLISECONDS, ContributorsComponent.ROTATE_RATE_IN_MILLISECONDS)
      .subscribe(time => {
        const visibleContributorQuantity = this.getVisibleContributorQuantity();
        console.log(visibleContributorQuantity);
        if (this.contributors.length - this.position >= visibleContributorQuantity) {
          this.position += 1;
        } else {
          this.position = 1;
        }
        this.setContributorPosition(this.position);
      });
  }

  private getVisibleContributorQuantity(): number {
    return parseInt(window.getComputedStyle(this.element.nativeElement, null).getPropertyValue(this.CONTRIBUTOR_QTY), 10);
  }

  private setContributorPosition(position: number) {
    this.element.nativeElement.style.setProperty(this.CONTRIBUTOR_POSITION, position);
  }

  ngOnDestroy(): void {
    if (!this.animation.isUnsubscribed) {
      this.animation.unsubscribe();
    }
  }

}
