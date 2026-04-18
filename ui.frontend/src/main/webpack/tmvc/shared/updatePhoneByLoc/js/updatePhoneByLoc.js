(function (window, document, $) {
  'use strict';

  var STORAGE_KEY = 'tmvc_loc_data';

  function getUrlLoc() {
    if (!window.location || !window.location.search) return null;
    var params = new URLSearchParams(window.location.search);
    return params.get('loc');
  }

  function formatUSPhoneNumbersOnLoad(selector) {
    selector = selector || '.cta-phone, .dynamic-phone-number';

    var phoneElements = document.querySelectorAll(selector);

    for (var i = 0; i < phoneElements.length; i++) {
      var el = phoneElements[i];
      if (!el || !el.textContent) continue;

      var digits = el.textContent.replace(/\D/g, '');

      // Remove leading US country code
      if (digits.length === 11 && digits.charAt(0) === '1') {
        digits = digits.substring(1);
      }

      if (digits.length === 10) {
        el.textContent =
          digits.substring(0, 3) + '-' +
          digits.substring(3, 6) + '-' +
          digits.substring(6);
      }
    }
  }

  function updatePhoneUI(phoneNumber) {
    var phoneLinks = document.querySelectorAll('.cta-phone');

    // Strip non-numeric characters for tel:
    var numericOnly = phoneNumber.replace(/(?!^\+)\D/g, '');

    phoneLinks.forEach(function (link) {
      // Update tel: href
      if (link.tagName === 'A') {
        link.setAttribute('href', 'tel:' + numericOnly);
      }

      // Check flag
      var shouldAppendText = link.getAttribute('data-append-with-text') === 'true';

      // Update phone number text only when allowed
      var phoneSpan = link.querySelector('.cta-phone__number');

      if (shouldAppendText) {
        if (phoneSpan) {
          phoneSpan.textContent = phoneNumber;
        } else {
          // fallback for phone-only elements
          link.textContent = phoneNumber;
        }
      } else if (phoneSpan) {
        // Clear or hide text when not allowed
        phoneSpan.textContent = '';
        // OR use: phoneSpan.style.display = 'none';
      }else{
		link.textContent = phoneNumber;
	  }
    });

    // Format only visible phone numbers
    formatUSPhoneNumbersOnLoad('.cta-phone__number');
  }



  function syncWithBackend(locParam) {
    var url = '/bin/mvw/phone-number-lookup';
    if (locParam) {
      url += '?loc=' + encodeURIComponent(locParam);
    }

    fetch(url, {
      method: 'GET',
      credentials: 'same-origin'
    })
      .then(function (response) {
        if (!response.ok) throw new Error('Network response was not ok');
        return response.json();
      })
      .then(function (response) {
        if (response && response.status === 'success' && response.phoneNumber) {
          var newData = {
            loc: response.loc || locParam || 'default',
            phoneNumber: response.phoneNumber,
            updatedAt: new Date().getTime()
          };

          try {
            localStorage.setItem(STORAGE_KEY, JSON.stringify(newData));
          } catch (e) { }

          updatePhoneUI(response.phoneNumber);
        }
      })
      .catch(function (error) {
        if (window.console) {
          console.error('Backend sync failed:', error);
        }
      });
  }


  function init() {
    var urlLoc = getUrlLoc();
    var storedData = null;
    try {
      storedData = JSON.parse(localStorage.getItem(STORAGE_KEY));
    } catch (e) { }
    if (storedData && storedData.phoneNumber) {
      updatePhoneUI(storedData.phoneNumber);
    }
    var currentStoredLoc = storedData ? storedData.loc : null;
    var shouldSync = false;
    if (!storedData) {
      shouldSync = true;
    } else if (urlLoc && urlLoc !== currentStoredLoc) {
      shouldSync = true;
    }
    if (shouldSync) {
      syncWithBackend(urlLoc);
    }
  }

  // DOM ready (safe for shared JS)
  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', init);
  } else {
    init();
  }

})(window, document);

