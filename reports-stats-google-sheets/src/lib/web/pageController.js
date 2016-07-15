'use strict';

$(function() {
  function initRepositoriesSelect() {
    $('#select-repos').chosen();
    // TODO: fetch repositories
  }

  function initGetPrStatsButton() {
    $('#get-pr-stats').on('click', function(event) {
      event.preventDefault();
      getPrStats();
    });
  }

  function getPrStats() {
    // TODO: start search
  }

  function initSelectAllButton() {
    $('#select-all-repos').on('click', function(event) {
      event.preventDefault();
      selectAll();
    });
  }

  function selectAll() {
    $('select#select-repos').find('option').each(function(i, option) {
      var prev = $('select#select-repos').val() || [];
      $('select#select-repos').val(prev.concat($(option).val()));
      $('select#select-repos').trigger('chosen:updated');
    });
  }

  initRepositoriesSelect();
  initGetPrStatsButton();
  initSelectAllButton();

  var queryStringifier = new QueryStringifier();
  var http = new WebHttp(queryStringifier);
  var reports = new Reports(http);

  reports.getRepositories()
    .then(function() {
      // TODO: do stuff here -- get and populate teh list of repos
    });

});
