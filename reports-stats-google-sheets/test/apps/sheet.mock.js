'use strict';

/* exported MockSheet */
function MockSheet() {

  return jasmine.createSpyObj('mockSheet', [
    'getValue',
    'getValueAsDate',
    'getColumnValues',
    'setValues',
    'clear',
    'setName',
    'setBold',
    'alignToCenterMiddle',
    'setRowHeight',
    'setWrap',
    'setFrozenRows',
    'setFrozenColumns',
    'setBottomBorder',
    'setBackground'
  ]);

}
