'use strict';

var API_REPOS = 'repositories';
var API_STATS_PR = 'stats/pr';
var API_ORGANISATION_USERS = '/users/org';
var API_STATS_AGGREGATED = 'stats/aggregated';

/* exported Reports */
function Reports(http, config) {
  this.http = http;
  this.config = config;
}

function reposApi(config) {
  return config.api + API_REPOS;
}

function statsPrApi(config) {
  return config.api + API_STATS_PR;
}

function statsAggregatedApi(config) {
  return config.api + API_STATS_AGGREGATED;
}

function organisationUsersApi(config) {
  return config.api + API_ORGANISATION_USERS;
}

Reports.prototype.getRepositories = function() {
  return this.http.fetch(reposApi(this.config));
};

Reports.prototype.getPrStats = function(from, to, repos, groupBy, withAverage) {
  return this.http.fetch(statsPrApi(this.config), {
    from: from,
    to: to,
    repos: repos,
    groupBy: groupBy,
    withAverage: withAverage
  });
};

Reports.prototype.getOrganisationUsers = function() {
  return this.http.fetch(organisationUsersApi(this.config));
};

Reports.prototype.getAggregatedUserStats = function(from, to, users) {
  return this.http.fetch(statsAggregatedApi(this.config), {
    from: from,
    to: to,
    users: users
  });
};
