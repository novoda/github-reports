'use strict';

/* exported MockSheet */
function MockSheet() {

  return jasmine.createSpyObj('mockSheet', ['getValue', 'getValueAsDate', 'getColumnValues', 'setValues', 'clear']);

}
