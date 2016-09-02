/* tslint:disable:no-unused-variable */

import { addProviders, async, inject } from '@angular/core/testing';
import { TimezoneDetectorService } from './timezone-detector.service';

describe('Service: TimezoneDetector', () => {
  beforeEach(() => {
    addProviders([TimezoneDetectorService]);
  });

  it('should ...',
    inject([TimezoneDetectorService],
      (service: TimezoneDetectorService) => {
        expect(service).toBeTruthy();
      }));
});
