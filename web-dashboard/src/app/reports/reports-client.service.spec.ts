/* tslint:disable:no-unused-variable */

import {addProviders, inject} from '@angular/core/testing';
import {ReportsClient} from './reports-client.service';
import {ReportsService} from './reports.service';
import {Http, BaseRequestOptions} from '@angular/http';
import {MockBackend} from '@angular/http/testing/mock_backend';

describe('Service: ReportsClient', () => {
  beforeEach(() => {
    addProviders([
      MockBackend,
      BaseRequestOptions,
      {
        provide: Http,
        useFactory: (backend, defaultOptions) => new Http(backend, defaultOptions),
        deps: [MockBackend, BaseRequestOptions]
      },
      ReportsService,
      ReportsClient]);
  });

  it('should instantiate the service client',
    inject([ReportsClient],
      (service: ReportsClient) => {
        expect(service).toBeTruthy();
      }));
});
