'use strict';

/* exported AppsMockHttp */
function AppsMockHttp() {

  return jasmine.createSpyObj('appsMockHttp', ['fetch', 'getUrlWithQuery']);

}
