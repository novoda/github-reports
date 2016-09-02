'use strict';

$(function() {
  var GET_AGGREGATED_STATS = 'Get aggregated stats!';
  var LOADING = [
    'Loading your stats...',
    'Hold on while we grab your stats...',
    'Hang on tight, stats are coming.',
    'Shipping stats, please prepare docks...',
    'OH MY GOD LOADING SO MUCH GITHUB'
  ];

  var fromInput = $('input#date-from');
  var toInput = $('input#date-to');
  var usersSelect = $('select#select-users');
  var selectAllButton = $('#select-all-users');
  var clearButton = $('input#clear-all-users');
  var getAggregatedButton = $('input#get-aggregated-stats');
  var allInputs = $('input, select');
  var allUsers = [];

  function initUsersSelect() {
    reports.getOrganisationUsers()
      .then(function(users) {
        users.forEach(function(user) {
          var option = $('<option value="' + user + '">' + user + '</option>');
          usersSelect.append(option);
          allUsers.push(option);
        });
        usersSelect.chosen();
      });
  }

  function initGetPrStatsButton() {
    getAggregatedButton.val(GET_AGGREGATED_STATS);
    getAggregatedButton.on('click', function(event) {
      event.preventDefault();
      getAggregatedStats();
    });
  }

  function getAggregatedStats() {
    var from = fromInput.val();
    var to = toInput.val();
    var users = usersSelect.val();
    var requestISODate = new Date().toISOString();

    showLoading();
    reportsExecutor.showAggregatedStats(from, to, users, requestISODate)
      .then(showNotLoading, showNotLoading);
  }

  function showLoading() {
    allInputs.prop('disabled', true);
    refreshSelectedUsers();
    getAggregatedButton.val(getRandomLoadingMessage());
  }

  function getRandomLoadingMessage() {
    var randomIndex = Math.floor(Math.random() * LOADING.length);
    return LOADING[randomIndex];
  }

  function showNotLoading() {
    allInputs.prop('disabled', false);
    refreshSelectedUsers();
    getAggregatedButton.val(GET_AGGREGATED_STATS);
  }

  function initSelectAllButton() {
    selectAllButton.on('click', selectAll);
  }

  function selectAll() {
    allUsers.forEach(function(option) {
      var prev = usersSelect.val() || [];
      usersSelect.val(prev.concat($(option).val()));
    });
    refreshSelectedUsers();
  }

  function initClearAllButton() {
    clearButton.on('click', clearAll);
  }

  function clearAll() {
    usersSelect.val([]);
    refreshSelectedUsers();
  }

  function refreshSelectedUsers() {
    usersSelect.trigger('chosen:updated');
  }

  var queryStringifier = new QueryStringifier();
  var http = new WebHttp(queryStringifier);
  var reports = new Reports(http, config);
  var reportsExecutor = new ReportsExecutor();

  initUsersSelect();
  initGetPrStatsButton();
  initSelectAllButton();
  initClearAllButton();

});
