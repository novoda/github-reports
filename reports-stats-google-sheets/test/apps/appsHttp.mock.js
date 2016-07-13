'use strict';

/* exported AppsMockHttp */
function AppsMockHttp() {

  AppsMockHttp.prototype.fetch = jasmine.createSpy('fetch');

  AppsMockHttp.prototype.fetch = jasmine.createSpy('getUrlWithQuery');

}
