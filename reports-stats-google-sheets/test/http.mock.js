'use strict';

/* exported MockHttp */
function MockHttp() {
  
  MockHttp.prototype.fetch = jasmine.createSpy('fetch');

}
