/* tslint:disable:no-unused-variable */

import {addProviders, inject} from "@angular/core/testing";
import {ReportsServiceClient} from "./reports-service-client.service";
import {ReportsService} from "./reports.service";
import {Http, BaseRequestOptions} from "@angular/http";
import {MockBackend} from "@angular/http/testing/mock_backend";

describe('Service: ReportsServiceClient', () => {
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
      ReportsServiceClient]);
  });

  it('should instantiate the service client',
    inject([ReportsServiceClient],
      (service: ReportsServiceClient) => {
        expect(service).toBeTruthy();
      }));
});
