function initGlobalThirdPartyModal() {

  const modalOverlay = document.getElementById('tpm-modal');
  const modalWrapper = modalOverlay ? modalOverlay.querySelector('.tpm-wrapper') : null;
  const closeBtn = document.getElementById('tpm-close-btn');
  const ackBtn = document.getElementById('tpm-acknowledge-btn');

  if (!modalOverlay || !modalWrapper) {
    console.warn('Third Party Modal: Component HTML not found on page.');
    return;
  }

  let targetUrl = '';
  let targetWindow = '_self';
  let previousActiveElement = null;

  const openModal = () => {
    previousActiveElement = document.activeElement;
    const scrollbarWidth = window.innerWidth - document.documentElement.clientWidth;
    if (scrollbarWidth > 0) {
      document.body.style.paddingRight = `${scrollbarWidth}px`;
    }
    modalOverlay.classList.add('is-open');
    modalOverlay.setAttribute('aria-hidden', 'false');
    document.body.classList.add('tpm-no-scroll');
    setTimeout(() => {
      modalWrapper.focus();
    }, 150);
  };

  const closeModal = () => {
    modalOverlay.classList.remove('is-open');
    modalOverlay.setAttribute('aria-hidden', 'true');
    document.body.classList.remove('tpm-no-scroll');
    document.body.style.paddingRight = '';
    targetUrl = '';
    targetWindow = '_self';

    if (previousActiveElement &&
      previousActiveElement !== document.body &&
      previousActiveElement !== document.documentElement) {
      previousActiveElement.focus();
    }
  };

  const proceedToLink = () => {
    if (!targetUrl) return;
    window.targetUrl = targetUrl;
    const urlToVisit = targetUrl;
    const windowTarget = targetWindow;

    closeModal();

    if (windowTarget === '_blank') {
      window.open(urlToVisit, '_blank');
    } else {
      window.location.href = urlToVisit;
    }
  };

  const handleFocusTrap = (e) => {
    const isTabPressed = e.key === 'Tab' || e.keyCode === 9;
    if (!isTabPressed) return;

    const focusableElements = modalWrapper.querySelectorAll(
      'button, [href], input, select, textarea, [tabindex]:not([tabindex="-1"])'
    );

    if (focusableElements.length === 0) {
      e.preventDefault();
      return;
    }

    const firstElement = focusableElements[0];
    const lastElement = focusableElements[focusableElements.length - 1];

    if (e.shiftKey) {
      if (document.activeElement === firstElement) {
        lastElement.focus();
        e.preventDefault();
      }
    } else {
      if (document.activeElement === lastElement) {
        firstElement.focus();
        e.preventDefault();
      }
    }
  };

  document.addEventListener('click', function (e) {
    const link = e.target.closest('.third-party-link');
    if (link && link.href) {
      e.preventDefault();
      targetUrl = link.href;
      targetWindow = link.getAttribute('target') || '_self';
      openModal();
    }
  });

  if (closeBtn) closeBtn.addEventListener('click', closeModal);
  if (ackBtn) ackBtn.addEventListener('click', proceedToLink);

  modalOverlay.addEventListener('click', function (e) {
    if (e.target === modalOverlay) closeModal();
  });

  document.addEventListener('keydown', function (e) {
    if (!modalOverlay.classList.contains('is-open')) return;
    if (e.key === 'Escape') closeModal();
    handleFocusTrap(e);
  });
}

if (document.readyState === 'loading') {
  document.addEventListener('DOMContentLoaded', initGlobalThirdPartyModal);
} else {
  initGlobalThirdPartyModal();
}