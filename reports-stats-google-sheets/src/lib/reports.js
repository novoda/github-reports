'use strict';

/* exported Reports */
function Reports(http) {

  var API_BASE = 'http://private-96ce1-githubreportsapi.apiary-mock.com/';
  var API_PROJECTS = 'projects';
  var API_REPOS = 'repositories';
  var API_USERS_TEAM = 'users/team';
  var API_STATS_PR = 'stats/pr';

  function reposApi() {
    return API_BASE + API_REPOS;
  }

  function projectsApi() {
    return API_BASE + API_PROJECTS;
  }

  function usersTeamApi() {
    return API_BASE + API_USERS_TEAM;
  }

  function statsPrApi() {
    return API_BASE + API_STATS_PR;
  }

  Reports.prototype.getProjects = function() {
    return http.fetch(projectsApi());
  };

  Reports.prototype.getRepositories = function() {
    return http.fetch(reposApi());
  };

  Reports.prototype.getUsersTeam = function() {
    return http.fetch(usersTeamApi());
  };

  Reports.prototype.getPrStats = function(from, to, repos, projects, filter, groupBy, withAverage) {
    return http.fetch(statsPrApi(), {
      from: from,
      to: to,
      repos: repos,
      projects: projects,
      filter: filter,
      groupBy: groupBy,
      withAverage: withAverage
    });
  };

}
