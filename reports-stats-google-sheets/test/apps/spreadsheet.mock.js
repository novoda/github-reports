'use strict';

/* exported MockSpreadsheet */
function MockSpreadsheet() {

  return jasmine.createSpyObj('mockSpreadsheet', [
    'getNumberOfSheets',
    'createNewSheet',
    'getSheetByName',
    'createSidebar',
    'showSidebar',
    'createMenu',
    'showAlert'
  ]);

}
