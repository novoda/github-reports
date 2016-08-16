/* tslint:disable:no-unused-variable */

import { addProviders, inject } from '@angular/core/testing';
import { ContributorsVsSlackersDashboardComponent } from './contributors-vs-slackers-dashboard.component';
import { SystemClock } from '../system-clock';
import { WeekCalculator } from '../week-calculator.service';
import { ReportsService } from '../reports/reports.service';
import { ReportsClient } from '../reports/reports-client.service';
import { UserStats } from '../reports/user-stats';
import { CompanyStats } from '../reports/company-stats';
import { Observable, Scheduler } from 'rxjs';
import { Http, BaseRequestOptions } from '@angular/http';
import { MockBackend } from '@angular/http/testing/mock_backend';

describe('Component: ContributorsVsSlackersDashboard', () => {

  let clock: SystemClock;
  let weekCalculator: WeekCalculator;
  let reportsService: ReportsService;
  let reportsServiceClient: ReportsClient;

  let component: ContributorsVsSlackersDashboardComponent;

  beforeEach(() => {
    addProviders([
      SystemClock,
      WeekCalculator,
      MockBackend,
      BaseRequestOptions,
      {
        provide: Http,
        useFactory: (backend, defaultOptions) => new Http(backend, defaultOptions),
        deps: [MockBackend, BaseRequestOptions]
      },
      ReportsService]);
  });

  beforeEach(inject([SystemClock, WeekCalculator, ReportsService],
    (_clock_, _weekCalculator_, _reportsService_) => {
      clock = _clock_;
      weekCalculator = _weekCalculator_;
      reportsService = _reportsService_;
      reportsServiceClient = new ReportsClient(_reportsService_);
    }));

  beforeEach(() => {
    component = new ContributorsVsSlackersDashboardComponent(weekCalculator, clock, reportsServiceClient);
  });

  let contributors;

  const newUserStats = (username: string): UserStats => {
    return new UserStats(username, null, null, null);
  };

  beforeEach(() => {
    contributors = [
      newUserStats('banana'),
      newUserStats('joe'),
      newUserStats('trinity'),
      newUserStats('bud'),
      newUserStats('boss'),
      newUserStats('goku')
    ];
  });

  it('creates an instance', () => {
    expect(component).toBeTruthy();
  });

  describe('cloneArray', () => {

    it('clones an array', () => {
      const array = [1, 2, 3];

      const actualCloned = component.cloneArray(array);

      expect(actualCloned).toEqual(array);
    });

    it('creates a copy of the input array', () => {
      const array = [1, 2, 3];

      const actualCloned = component.cloneArray(array);

      expect(actualCloned).not.toBe(array);
    });

  });

  describe('pickAndRemoveRandomContributor', () => {

    it('returns an element that existed in the input array', () => {
      const copyOfContributors = contributors.slice(0);

      const oneUserStats = component.pickAndRemoveRandomContributor(copyOfContributors);

      expect(contributors.indexOf(oneUserStats)).toBeGreaterThan(-1);
    });

    it('modifies the input array be removing the returned element', () => {
      const oneUserStats = component.pickAndRemoveRandomContributor(contributors);

      expect(contributors.indexOf(oneUserStats)).toBe(-1);
    });

    it('decreases the size of the input array by 1', () => {
      component.pickAndRemoveRandomContributor(contributors);

      expect(contributors.length).toBe(5);
    });

    it('returns undefined if the array has no elements', () => {
      const actual = component.pickAndRemoveRandomContributor([]);

      expect(actual).toBeUndefined();
    });

    it('returns undefined if the array is undefined', () => {
      const actual = component.pickAndRemoveRandomContributor(undefined);

      expect(actual).toBeUndefined();
    });

  });

  describe('pickRandomContributors', () => {

    const slackers = [
      newUserStats('blundell'),
      newUserStats('xavi'),
      newUserStats('frapontillo'),
      newUserStats('takecare')
    ];

    let companyStats: CompanyStats;

    beforeEach(() => {
      companyStats = new CompanyStats(contributors, slackers);
    });

    it('does not alter the input company stats', () => {
      component.pickRandomContributors(companyStats);

      expect(companyStats.contributors.length).toBe(6);
    });

    it('returns 5 random contributors', () => {
      const randomContributors = component.pickRandomContributors(companyStats);

      expect(randomContributors.length).toBe(5);
    });

    it('returns all contributors if they are less than 5', () => {
      const statsWithFewContributors = new CompanyStats(
        [newUserStats('contributor-1'), newUserStats('contributor-2')],
        slackers
      );

      const randomContributors = component.pickRandomContributors(statsWithFewContributors);

      expect(randomContributors.length).toBe(2);
    });

    it('returns an empty array if there are no contributors', () => {
      const statsWithFewContributors = new CompanyStats([], slackers);

      const randomContributors = component.pickRandomContributors(statsWithFewContributors);

      expect(randomContributors).toEqual([]);
    });

  });

  describe('ngOnInit', () => {

    it('subscribes to the service', () => {
      component.ngOnInit();

      expect(component.subscription).toBeTruthy();
    });


    // TODO: temporarily deactivated due to TestScheduler lack of doc (https://github.com/ReactiveX/rxjs/issues/1791)
    xit('gets the company stats', () => {
      component.ngOnInit();

      Scheduler.async.flush();

      expect(reportsService.getAggregatedStats).toHaveBeenCalled();
    });

    xit('refreshes statistics every 30 seconds', () => {
      // TODO: add tests wrt refresh of statistics (https://github.com/ReactiveX/rxjs/issues/1791)
    });

    // TODO: fix refresh test with proper scheduelr (https://github.com/ReactiveX/rxjs/issues/1791)
    xit('retries the get statistics operation when it fails', () => {
      const failTimes = 3;
      let failCounter = 0;

      spyOn(reportsService, 'getAggregatedStats').and.callFake((): Observable<any> => {
        if (failCounter < failTimes) {
          failCounter += 1;
          return Observable.throw(new Error('Some network error'));
        }

        return Observable.from([{
          'usersStats': {
            'tasomaniac': {
              'assignedProjectsStats': {},
              'assignedProjectsContributions': 0,
              'externalRepositoriesStats': {},
              'externalRepositoriesContributions': 0
            }
          }
        }]);
      });

      component.ngOnInit();

      component.subscription
        .add(() => {
          expect(reportsService.getAggregatedStats).toHaveBeenCalledTimes(failTimes + 1);
        });
    });

    it('sets contributors and slackers', () => {
      component.ngOnInit();

      component.subscription
        .add(() => {
          expect(component.contributors).toBeDefined();
          expect(component.slackers).toBeDefined();
        });
    });

  });

  describe('ngOnDestroy', () => {

    it('unsubscribes from the service', () => {
      component.ngOnInit();

      component.ngOnDestroy();

      expect(component.subscription.isUnsubscribed).toBe(true);
    });

    it('does not unsubscribe from the service if it was already unsubscribed', () => {
      component.ngOnInit();
      component.subscription.unsubscribe();

      spyOn(component.subscription, 'unsubscribe');
      component.ngOnDestroy();
      expect(component.subscription.unsubscribe).not.toHaveBeenCalled();
    });

  });

});
