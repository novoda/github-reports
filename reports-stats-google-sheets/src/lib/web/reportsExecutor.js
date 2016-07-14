'use strict';

/* exported ReportsExecutor */
function ReportsExecutor() {

  function buildScriptRunner(resolve, reject) {
    return google.script
      .run
      .withSuccessHandler(resolve)
      .withFailureHandler(reject);
  }

  function runScriptAsPromise(method) {
    var args = Array.prototype.slice.call(arguments, 1);
    return new Promise(function(resolve, reject) {
      var scriptRunner = buildScriptRunner(resolve, reject)[method];
      scriptRunner.apply(this, args);
    });
  }

  ReportsExecutor.prototype.showPrStats = function(from, to, repositories, groupBy, withAverage, requestISODate) {
    return runScriptAsPromise('showPrStats', from, to, repositories, groupBy, withAverage, requestISODate);
  };

}
