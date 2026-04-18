(function () {
  'use strict';

  var BASE_LINK = '/content/tmvcs/us/en/experiences/destinations.html';

  /**
   * Updates .region-filter a links in the destination hero breadcrumb
   * to point to the destination landing page with the correct ?region= param.
   *
   * Reads data-region (nodename) from each anchor set by destinationsHero.html.
   * Mirrors the updateFilterAnchors pattern from resort-detail.js.
   *
   * @param {Document|Element} root
   */
  function updateDestinationBreadcrumb(root) {
    root = root || document;

    var anchors = root.querySelectorAll('.hero-breadcrumb .region-filter a');

    anchors.forEach(function (a) {
      var regionNodename = a.getAttribute('data-region');

      if (regionNodename) {
        a.href = BASE_LINK + '?region=' + encodeURIComponent(regionNodename);
      } else {
        a.href = BASE_LINK;
      }
    });
  }

  /**
   * Tracks the current destination detail page visit into recentlyViewedDestinations
   * localStorage so it shows up in the search dropdown recently viewed list.
   * Reads destination name from the .state-filter element in destinationsHero.
   */
  function trackDestinationPageView() {
    try {
      var MAX = 10;
      var stateFilter = document.querySelector('.hero-breadcrumb .state-filter');
      var destinationName = stateFilter ? stateFilter.textContent.trim() : '';
      if (!destinationName) return;

      var ids = JSON.parse(localStorage.getItem('recentlyViewedDestinations') || '[]');
      ids = [destinationName, ids.filter(function(id) { return id !== destinationName; })].flat().slice(0, MAX);
      localStorage.setItem('recentlyViewedDestinations', JSON.stringify(ids));

      var ts = JSON.parse(localStorage.getItem('recentlyViewedDestinationsTimestamps') || '{}');
      ts[destinationName] = Date.now();
      localStorage.setItem('recentlyViewedDestinationsTimestamps', JSON.stringify(ts));
    } catch (e) {}
  }

  function init() {
    updateDestinationBreadcrumb(document);
    trackDestinationPageView();
  }

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', init, { once: true });
  } else {
    init();
  }

  window.MVWDestinationBreadcrumb = { init: init, updateDestinationBreadcrumb: updateDestinationBreadcrumb };

})();
