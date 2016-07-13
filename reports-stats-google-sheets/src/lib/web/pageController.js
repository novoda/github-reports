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

  initRepositoriesSelect();
  initGetPrStatsButton();
});
