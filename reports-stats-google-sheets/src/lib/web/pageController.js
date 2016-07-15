'use strict';

$(function() {
  var GET_PR_STATS = "Get PR stats!";
  var LOADING = [
    "Loading your stats...",
    "Hold on while we grab your stats...",
    "Hang on tight, stats are coming.",
    "Shipping stats, please prepare docks...",
    "OH MY GOD LOADING SO MUCH GITHUB"
  ];

  var fromInput = $('input#date-from');
  var toInput = $('input#date-to');
  var reposSelect = $('select#select-repos');
  var clearButton = $('input#clear-all-repos');
  var groupBySelect = $('select#select-group-by');
  var withAverageCheck = $('input#check-with-average');
  var getPrButton = $('input#get-pr-stats');
  var allInputs = $('input, select');
  var allRepos = [];

  function initRepositoriesSelect() {
    reports.getRepositories()
      .then(function(repos) {
        repos.forEach(function(repo) {
          var option = $('<option value="' + repo + '">' + repo + '</option>');
          reposSelect.append(option);
          allRepos.push(option);
        });
        reposSelect.chosen();
      });
  }

  function initGetPrStatsButton() {
    getPrButton.val(GET_PR_STATS);
    getPrButton.on('click', function(event) {
      event.preventDefault();
      getPrStats();
    });
  }

  function getPrStats() {
    var from = fromInput.val();
    var to = toInput.val();
    var repositories = reposSelect.val();
    var groupBy = groupBySelect.val();
    var withAverage = withAverageCheck.prop('checked');
    var requestISODate = new Date().toISOString();

    showLoading();
    reportsExecutor.showPrStats(from, to, repositories, groupBy, withAverage, requestISODate)
      .then(showNotLoading, showNotLoading);
  }

  function showLoading() {
    allInputs.prop('disabled', true);
    refreshSelectedRepos();
    getPrButton.val(getRandomLoadingMessage());
  }

  function getRandomLoadingMessage() {
    var randomIndex = Math.floor(Math.random() * LOADING.length);
    return LOADING[randomIndex];
  }

  function showNotLoading() {
    allInputs.prop('disabled', false);
    refreshSelectedRepos();
    getPrButton.val(GET_PR_STATS);
  }

  function initSelectAllButton() {
    $('#select-all-repos').on('click', selectAll);
  }

  function selectAll() {
    allRepos.forEach(function(option) {
      var prev = reposSelect.val() || [];
      reposSelect.val(prev.concat($(option).val()));
    });
    refreshSelectedRepos();
  }

  function initClearAllButton() {
    clearButton.on('click', clearAll);
  }

  function clearAll() {
    reposSelect.val([]);
    refreshSelectedRepos();
  }

  function refreshSelectedRepos() {
    reposSelect.trigger('chosen:updated');
  }

  var queryStringifier = new QueryStringifier();
  var http = new WebHttp(queryStringifier);
  var reports = new Reports(http);
  var reportsExecutor = new ReportsExecutor();

  initRepositoriesSelect();
  initGetPrStatsButton();
  initSelectAllButton();
  initClearAllButton();

});
