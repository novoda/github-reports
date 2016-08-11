/* tslint:disable:no-unused-variable */

import {addProviders, inject} from '@angular/core/testing';
import {ReportsService} from './reports.service';
import {Http, BaseRequestOptions, ResponseOptions, Response} from '@angular/http';
import {MockBackend} from '@angular/http/testing/mock_backend';

describe('Service: Reports', () => {

  const ANY_DATE = new Date();

  let mockBackend: MockBackend;
  let reportsService: ReportsService;

  beforeEach(() => {
    addProviders([
      MockBackend,
      BaseRequestOptions,
      {
        provide: Http,
        useFactory: (backend, defaultOptions) => new Http(backend, defaultOptions),
        deps: [MockBackend, BaseRequestOptions]
      },
      ReportsService]);
  });

  beforeEach(inject([MockBackend, ReportsService], (_mockBackend_: MockBackend, _reportsService_: ReportsService) => {
    mockBackend = _mockBackend_;
    reportsService = _reportsService_;
  }));

  it('instantiates the reports service', () => {
    expect(reportsService).toBeTruthy();
  });

  it('converts the returned string into a JSON object', () => {
    let response = new Response(new ResponseOptions({body: '{"some": "string", "one": 1}'}));
    mockBackend.connections.subscribe(connection => connection.mockRespond(response));

    reportsService.getAggregatedStats(ANY_DATE, ANY_DATE)
      .subscribe(value => {
        expect(value).toEqual({
          some: 'string',
          one: 1
        });
      });
  });

});
