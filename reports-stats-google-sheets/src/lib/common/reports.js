'use strict';

var API_BASE = 'https://t6lqw400oe.execute-api.us-east-1.amazonaws.com/api/';
var API_REPOS = 'repositories';
var API_STATS_PR = 'stats/pr';
var API_ORGANISATION_USERS = '/users/org';
var API_STATS_AGGREGATED = 'stats/aggregated';

/* exported Reports */
function Reports(http) {
  this.http = http;
}

function reposApi() {
  return API_BASE + API_REPOS;
}

function statsPrApi() {
  return API_BASE + API_STATS_PR;
}

function statsAggregatedApi() {
  return API_BASE + API_STATS_AGGREGATED;
}

function organisationUsersApi() {
  return API_BASE + API_ORGANISATION_USERS;
}

Reports.prototype.getRepositories = function() {
  return this.http.fetch(reposApi());
};

Reports.prototype.getPrStats = function(from, to, repos, groupBy, withAverage) {
  return this.http.fetch(statsPrApi(), {
    from: from,
    to: to,
    repos: repos,
    groupBy: groupBy,
    withAverage: withAverage
  });
};

Reports.prototype.getOrganisationUsers = function() {
  return this.http.fetch(organisationUsersApi());
};

Reports.prototype.getAggregatedUserStats = function(from, to, users) {
  return this.http.fetch(statsAggregatedApi(), {
    from: from,
    to: to,
    users: users
  });
};
