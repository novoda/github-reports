'use strict';

/* exported Reports */
function Reports(http) {

  var API_BASE = 'https://t6lqw400oe.execute-api.us-east-1.amazonaws.com/api/';
  var API_REPOS = 'repositories';
  var API_STATS_PR = 'stats/pr';

  function reposApi() {
    return API_BASE + API_REPOS;
  }

  function statsPrApi() {
    return API_BASE + API_STATS_PR;
  }

  Reports.prototype.getRepositories = function() {
    return http.fetch(reposApi());
  };

  Reports.prototype.getPrStats = function(from, to, repos, groupBy, withAverage) {
    return http.fetch(statsPrApi(), {
      from: from,
      to: to,
      repos: repos,
      groupBy: groupBy,
      withAverage: withAverage
    });
  };

}