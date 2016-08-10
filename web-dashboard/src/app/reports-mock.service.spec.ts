/* tslint:disable:no-unused-variable */

import { addProviders, async, inject } from '@angular/core/testing';
import { ReportsMockService } from './reports-mock.service';

describe('Service: ReportsMock', () => {
  beforeEach(() => {
    addProviders([ReportsMockService]);
  });

  it('should instantiate the mock service',
    inject([ReportsMockService],
      (service: ReportsMockService) => {
        expect(service).toBeTruthy();
      }));
});
