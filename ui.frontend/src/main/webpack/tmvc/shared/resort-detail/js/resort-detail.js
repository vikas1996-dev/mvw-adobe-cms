(function () {
  'use strict';

  // Small helpers
  const $ = (sel, ctx = document) => ctx.querySelector(sel);
  const $$ = (sel, ctx = document) => Array.from((ctx || document).querySelectorAll(sel));
  const on = (el, evt, fn, opts) => el && el.addEventListener(evt, fn, opts);
  const off = (el, evt, fn, opts) => el && el.removeEventListener(evt, fn, opts);
  const debounce = (fn, wait = 120) => {
    let t;
    return (...args) => { clearTimeout(t); t = setTimeout(() => fn.apply(null, args), wait); };
  };

  /* -----------------------------
   * Accessibility helpers for section headers
   * --------------------------- */
  const TOGGLE_CLASS = 'active';
  const BOUND_FLAG = 'data-amen-bound';

  function setHeaderAccessibility(header) {
    if (!header) return;
    if (!header.hasAttribute('role')) header.setAttribute('role', 'button');
    if (!header.hasAttribute('tabindex')) header.setAttribute('tabindex', '0');
    header.setAttribute('aria-expanded', String(header.classList.contains(TOGGLE_CLASS)));
  }

  function toggleHeader(header) {
    if (!header) return;
    const content = header.nextElementSibling;
    if (!content) return;
    const open = content.classList.toggle(TOGGLE_CLASS);
    header.classList.toggle(TOGGLE_CLASS, open);
    header.setAttribute('aria-expanded', String(open));
  }

  function bindSectionHeaders(container = document) {
    if (!container) return;
    if (container.hasAttribute && container.hasAttribute(BOUND_FLAG)) return;
    if (container.setAttribute) container.setAttribute(BOUND_FLAG, 'true');

    $$('.section-header', container).forEach(header => {
      // remove legacy inline handlers
      if (header.hasAttribute && header.hasAttribute('onclick')) header.removeAttribute('onclick');
      setHeaderAccessibility(header);

      on(header, 'click', (e) => { e.preventDefault(); toggleHeader(header); });
      on(header, 'keydown', (e) => {
        if (e.key === 'Enter' || e.key === ' ' || e.key === 'Spacebar') {
          e.preventDefault(); toggleHeader(header);
        }
      });
    });
  }

  /* -----------------------------
   * Media modal (resort-media-model)
   * --------------------------- */
  function initMediaModal(root = document) {
    const modal = $('.resort-media-model', root);
    if (!modal) return;
    // guard to avoid double-binding
    if (modal.dataset.bound === 'true') return;
    modal.dataset.bound = 'true';

    const inner = $('.resort-media__model', modal);
    const openSelector = '[data-open-media-model]';
    const openButtons = $$(openSelector, document);
    const closeButtons = $$('.close_model', modal);
    const videoIframe = inner ? inner.querySelector('iframe') : null;
    const fallbackImage = inner ? inner.querySelector('.fallback-image') : null;

    function showModal({ videoId, imageSrc } = {}) {
      if (!modal) return;
      // set iframe src safely
      if (videoIframe) {
        if (videoId) videoIframe.src = `https://www.youtube.com/embed/${videoId}?autoplay=1&rel=0&showinfo=0`;
        else videoIframe.src = '';
      }
      if (fallbackImage) {
        if (imageSrc) { fallbackImage.src = imageSrc; fallbackImage.style.display = 'block'; }
        else { fallbackImage.src = ''; fallbackImage.style.display = 'none'; }
      }

      modal.classList.add('resort-media-model--open');
      modal.style.display = 'flex';
      // focus first close for accessibility
      const firstClose = modal.querySelector('.close_model');
      if (firstClose) firstClose.focus();
      // do not alter body overflow (page scroll retained)
    }

    function closeModal() {
      if (!modal) return;
      modal.classList.remove('resort-media-model--open');
      modal.style.display = 'none';
      if (videoIframe) videoIframe.src = '';
    }

    // bind open triggers
    openButtons.forEach(btn => {
      on(btn, 'click', (e) => {
        e.preventDefault();
        const videoId = btn.getAttribute('data-video-id');
        const imageSrc = btn.getAttribute('data-image-src');
        showModal({ videoId, imageSrc });
      });
    });

    // close buttons
    closeButtons.forEach(btn => on(btn, 'click', (e) => { e.preventDefault(); closeModal(); }));

    // overlay click
    on(modal, 'click', (e) => { if (e.target === modal) closeModal(); });

    // keyboard
    on(document, 'keydown', (e) => {
      if (modal.style.display !== 'flex') return;
      if (e.key === 'Escape') closeModal();
    });
  }

  /* -----------------------------
   * Accommodations modal
   * --------------------------- */
  function initAccommodationsModal(root = document) {
    const modal = $('.accommodations-modal', root);
    if (!modal) return;
    if (modal.dataset.bound === 'true') return;
    modal.dataset.bound = 'true';

    const titleEl = modal.querySelector('.accommodations-modal__title');
    const carousel = modal.querySelector('.accommodations-modal__carousel');
    const viewport = modal.querySelector('.accommodations-modal__media'); // ✅ VIEWPORT
    const prevBtn = modal.querySelector('.accommodations-prev');
    const nextBtn = modal.querySelector('.accommodations-next');
    const closeBtn = modal.querySelector('.accommodations-modal__close');

    let currentIndex = 0;
    let slidesPerView = window.innerWidth <= 680 ? 1 : 2;

    /* =========================
       TRACK + VIEWPORT SETUP
    ========================= */
    if (viewport) viewport.style.overflow = 'hidden';

    if (carousel) {
      carousel.style.display = 'flex';
      carousel.style.flexWrap = 'nowrap';
      carousel.style.gap = '12px';
      carousel.style.transition = 'transform 0.4s ease';
      carousel.style.willChange = 'transform';
    }

    function setSlideSizes() {
      slidesPerView = window.innerWidth <= 680 ? 1 : 2;
      const slides = carousel.querySelectorAll('.accommodations-slide');
      slides.forEach(s => s.style.flex = `0 0 ${100 / slidesPerView}%`);
      updateTrack();
      updateControls();
    }

    function updateTrack() {
      const slides = carousel.querySelectorAll('.accommodations-slide');
      if (!slides.length) return;

      const gap = parseFloat(getComputedStyle(carousel).gap) || 0;
      const slideWidth = slides[0].getBoundingClientRect().width + gap;
      const maxIndex = Math.max(0, slides.length - slidesPerView);

      currentIndex = Math.max(0, Math.min(currentIndex, maxIndex));
      const left = currentIndex * slideWidth;

      carousel.style.transform = `translateX(-${left}px)`;
    }

    function updateControls() {
      const slides = carousel.querySelectorAll('.accommodations-slide');
      if (!slides.length) return;
      prevBtn.classList.toggle('disabled', currentIndex === 0);
      nextBtn.classList.toggle('disabled', currentIndex >= slides.length - slidesPerView);
    }

    function showSlide(index) {
      currentIndex = index;
      updateTrack();
      updateControls();
    }

    /* =========================
       OPEN MODAL
    ========================= */
    function openModalForCard(card) {
      const data = card.dataset;

      titleEl.textContent = data.title || '';
      modal.querySelector('.text').innerHTML = data.description || '';

      modal.querySelector('.detail-list').innerHTML = `
      ${data.sleeps ? `<div class="detail"><i class="fa-light fa-people-group"></i><span>Sleeps ${data.sleeps}</span></div>` : ''}
      ${data.size ? `<div class="detail"><i class="fa-light fa-ruler-combined"></i><span>${data.size} sq ft</span></div>` : ''}
      ${data.view ? `<div class="detail"><i class="fa-light fa-binoculars"></i><span>${data.view}</span></div>` : ''}
    `;
      const floorPlanBtn = modal.querySelector('.floor-plan-btn');
      const floorPlanTemplate = document.getElementById('floorPlan');
      const floorPlanImg = floorPlanTemplate?.content.querySelector('.floorplan-image');

      if (data.floorplan) {
        const fullPath = `${data.floorplan}`;
        if (floorPlanImg) {
          floorPlanImg.src = fullPath;
        }
        floorPlanBtn.style.display = 'inline-flex';

      } else {
        floorPlanBtn.style.display = 'none';
      }
      carousel.innerHTML = '';

      const uniqueImages = new Set();

      function createSlide(ratio, path) {
        if (!ratio) return;

        // Only allow 16x9 images
        if (!ratio.includes('16:9')) return;

        const key = path.replace(/\/+$/, '');

        if (uniqueImages.has(key)) return;
        uniqueImages.add(key);

        const slide = document.createElement('div');
        slide.className = 'accommodations-slide';
        slide.innerHTML = `<img src="${path}" alt="${data.title}">`;
        carousel.appendChild(slide);
      }




      try {
        const galleries = JSON.parse(data.gallery || '[]');

        galleries.forEach(g => {

          // STRUCTURE A → images.photo
          if (Array.isArray(g.images)) {
            g.images.forEach(img => {
              if (Array.isArray(img.photo)) {
                img.photo.forEach(p => createSlide(p.ratio, p.path));
              }
            });
          }

          // STRUCTURE B → direct photo
          if (Array.isArray(g.photo)) {
            g.photo.forEach(p => createSlide(p.ratio, p.path));
          }

          if (g.path) {
            createSlide(g.ratio, p.path);
          }

        });

      } catch (e) {
        console.warn("Gallery parse error", e);
      }


      const amenContainer = modal.querySelector('.accommodations-modal__amenities');
      amenContainer.innerHTML = '';

      try {
        const amenities = JSON.parse(data.amenities || '[]');
        amenities.forEach(group => {
          const col = document.createElement('div');
          col.className = 'amenities-col';
          col.innerHTML = `<h4>${group.title}</h4><ul>${group.items.map(i => `<li>${i}</li>`).join('')}</ul>`;
          amenContainer.appendChild(col);
        });
      } catch (e) { }

      currentIndex = 0;
      setSlideSizes();

      modal.style.display = 'flex';
      modal.setAttribute('aria-hidden', 'false');
      document.body.style.overflow = 'hidden';
    }

    function closeModal() {
      modal.style.display = 'none';
      modal.setAttribute('aria-hidden', 'true');
      document.body.style.overflow = '';
    }

    /* =========================
       EVENTS
    ========================= */
    $$('.viewmore-link').forEach(link =>
      on(link, 'click', e => {
        e.preventDefault();
        openModalForCard(link.closest('.service-card'));
      })
    );

    on(prevBtn, 'click', () => showSlide(currentIndex - 1));
    on(nextBtn, 'click', () => showSlide(currentIndex + 1));
    on(closeBtn, 'click', closeModal);

    on(window, 'resize', debounce(setSlideSizes, 150));

    on(document, 'keydown', e => {
      if (modal.style.display !== 'flex') return;
      if (e.key === 'Escape') closeModal();
      if (e.key === 'ArrowLeft') showSlide(currentIndex - 1);
      if (e.key === 'ArrowRight') showSlide(currentIndex + 1);
    });
  }


  /* -----------------------------
   * Other initializers (lightweight)
   * --------------------------- */
  function initAlert(root = document) {
    const alertHeader = $('.resort-alert .alert-header', root);
    const alertContent = $('.resort-alert .alert-content', root);
    if (!alertHeader || !alertContent) return;
    const chevron = alertHeader.querySelector('.fa-chevron-down');
    alertContent.style.display = 'none';
    on(alertHeader, 'click', () => {
      const isOpen = alertContent.style.display === 'block';
      alertContent.style.display = isOpen ? 'none' : 'block';
      if (chevron) chevron.classList.toggle('rotate', !isOpen);
    });
  }

  function initContentToggles(root = document) {
    $$('.content-sec', root).forEach(section => {
      const content = section.querySelector('.rte-content');
      const btn = section.querySelector('.show-more');
      if (!content || !btn) return;
      on(btn, 'click', (e) => { e.preventDefault(); const expanded = content.classList.toggle('expanded'); btn.textContent = expanded ? 'Show Less' : 'Show More'; });
    });
  }

  function initRatings(root = document) {
    $$('.rating', root).forEach(el => {
      const value = Number.parseFloat(el.dataset.rating || '0').toFixed(1);
      const url = `https://www.tripadvisor.com/img/cdsi/img2/ratings/traveler/${value}-MCID-5.svg`;
      el.innerHTML = `<img src="${url}" alt="TripAdvisor rating ${value}" />`;
    });
  }

  function initTextSections(root = document) {
    $$('.text-sec', root).forEach(section => {
      const textArea = section.querySelector('.text-area');
      const showMoreBtn = section.querySelector('.show-more');
      if (!textArea || !showMoreBtn) return;
      const maxHeight = parseInt(getComputedStyle(textArea).maxHeight, 10);
      if (!Number.isNaN(maxHeight) && textArea.scrollHeight > maxHeight) showMoreBtn.style.display = 'inline-block';
    });
  }

  function initNav(root = document) {
    const nav = $('[data-accordion]', root);
    if (!nav) return;
    const toggle = $('.nav-toggle', nav);
    const navigation = $('.resort-header');
    const links = $$('a[href^="#"]:not(.nav-toggle), .all-amenities[href^="#"]', nav);

  if (toggle) {
    on(toggle, 'click', (e) => {
      const isMobile = window.innerWidth <= 768;
      const target = document.querySelector('#overview');

      setActiveLink(toggle, nav);

      if (!isMobile) {
        if (!target) return;
        e.preventDefault();

        const navHeader = document.querySelector('.resort-header');
        const offset = navHeader ? navHeader.offsetHeight : 0;
        const top = target.getBoundingClientRect().top + window.pageYOffset;

        window.scrollTo({
          top: top - offset - 100,
          behavior: 'smooth'
        });

        return;
      }

      e.preventDefault();

      const isOpen = nav.classList.contains('open');

      if (!isOpen) {
        nav.classList.add('open');
      } else {
        nav.classList.remove('open');

        if (!target) return;

        const navHeader = document.querySelector('.resort-header');
        const offset = navHeader ? navHeader.offsetHeight : 0;
        const top = target.getBoundingClientRect().top + window.pageYOffset;

        setTimeout(() => {
          window.scrollTo({
            top: top - offset - 20,
            behavior: 'smooth'
          });
        }, 250);
      }
    });
  }
    links.forEach(link => on(link, 'click', (e) => {
      const targetId = link.getAttribute('href');
      const target = targetId && $(targetId);
      if (!target) return;

      e.preventDefault();

      setActiveLink(link, nav);

      const currentScroll = window.pageYOffset || document.documentElement.scrollTop || 0;
      const targetTop = target.getBoundingClientRect().top + currentScroll;

      const isDesktop = window.innerWidth > 768;
      const additionalOffset = isDesktop ? 100 : 0;

      let navHeight = 0;

      if (navigation) {
        const willBeSticky = targetTop > 250;

        if (willBeSticky && !navigation.classList.contains('sticky-nav')) {
          navigation.classList.add('sticky-nav');
          navHeight = navigation.offsetHeight + (isDesktop ? 320 : 377);
          navigation.classList.remove('sticky-nav');
        } else {
          navHeight = navigation.offsetHeight - (isDesktop ? 0 : 240);
        }
      }

      const offsetTop = targetTop - navHeight - additionalOffset;

      window.scrollTo({
        top: offsetTop,
        behavior: 'smooth'
      });

      if (window.innerWidth <= 768) {
        nav.classList.remove('open');
      }
    }));

    // Toggle 'sticky-nav' class on the page navigation and resort hero when user scrolls a bit
    try {
      const handleScroll = debounce(() => {
        const offset = window.pageYOffset || document.documentElement.scrollTop || 0;
        const isSticky = offset > 250;

        if (navigation) {
          if (isSticky) {
            navigation.classList.add('sticky-nav');
            // Force reflow to restart animation
            void navigation.offsetWidth;
            navigation.classList.add('is-visible');
          } else {
            navigation.classList.remove('is-visible');
            // Remove sticky-nav after fade out animation completes
            setTimeout(() => {
              if (!navigation.classList.contains('is-visible')) {
                navigation.classList.remove('sticky-nav');
              }
            }, 400);
          }
        }

      }, 20);

      on(window, 'scroll', handleScroll);
      // initialize state
      handleScroll();
    } catch (e) {
      // noop
    }
  }

  function setActiveLink(activeEl, nav) {
    const allLinks = $$('a[href^="#"]', nav);
    allLinks.forEach(l => l.classList.remove('active'));

    if (activeEl) activeEl.classList.add('active');
  }
  function initAllAmenities(root = document) {
    $$('.all-amenities[href^="#"]', root).forEach(link => {
      on(link, 'click', (e) => {
        const targetId = link.getAttribute('href');
        const target = targetId && $(targetId);
        const navigation = $('.resort-header'); 
        
        if (!target) return;
        e.preventDefault();

        const currentScroll = window.pageYOffset || document.documentElement.scrollTop || 0;
        const targetTop = target.getBoundingClientRect().top + currentScroll;

        const isDesktop = window.innerWidth > 768;
        const navHeight = navigation ? navigation.offsetHeight : 0;
        const additionalOffset = isDesktop ? 100 : 20; 

        window.scrollTo({
          top: targetTop - navHeight - additionalOffset,
          behavior: 'smooth'
        });
      });
    });
  }

  function initDiningModal(root = document) {
    const modal = $('.dining-modal', root);
    if (!modal || modal.dataset.bound === 'true') return;
    modal.dataset.bound = 'true';

    const titleEl = modal.querySelector('.dining-modal__title');
    const carousel = modal.querySelector('.dining-modal__carousel');
    const viewport = modal.querySelector('.dining-modal__media');
    const prevBtn = modal.querySelector('.dining-prev');
    const nextBtn = modal.querySelector('.dining-next');
    const closeBtn = modal.querySelector('.dining-modal__close');

    let currentIndex = 0;
    let slidesPerView = window.innerWidth <= 680 ? 1 : 2;

    if (viewport) viewport.style.overflow = 'hidden';
    if (carousel) {
      carousel.style.display = 'flex';
      carousel.style.flexWrap = 'nowrap';
      carousel.style.gap = '12px';
      carousel.style.transition = 'transform .4s ease';
    }

    function setSlideSizes() {
      slidesPerView = window.innerWidth <= 680 ? 1 : 2;
      carousel.querySelectorAll('.dining-slide')
        .forEach(s => s.style.flex = `0 0 ${100 / slidesPerView}%`);
      updateTrack();
    }

    function updateTrack() {
      const slides = carousel.querySelectorAll('.dining-slide');
      if (!slides.length) return;
      const gap = parseFloat(getComputedStyle(carousel).gap) || 0;
      const slideWidth = slides[0].getBoundingClientRect().width + gap;
      const maxIndex = Math.max(0, slides.length - slidesPerView);
      currentIndex = Math.max(0, Math.min(currentIndex, maxIndex));
      carousel.style.transform = `translateX(-${currentIndex * slideWidth}px)`;
    }

    function openDiningModal(card) {
      const data = card.dataset;

      titleEl.textContent = data.title || '';
      modal.querySelector('.text').innerHTML = data.description || '';

      modal.querySelector('.detail-list').innerHTML = `
      ${data.cuisine ? `<div class="detail">${data.cuisine}</div>` : ''}
      ${data.atmosphere ? `<div class="detail">${data.atmosphere}</div>` : ''}
    `;

      carousel.innerHTML = '';
      const unique = new Set();

      try {
        const galleries = JSON.parse(data.gallery || '[]');
        galleries.forEach(g => {
          (g.images || []).forEach(img =>
            (img.photo || []).forEach(p => {
              if (p.path && !unique.has(p.path)) {
                unique.add(p.path);
                carousel.innerHTML +=
                  `<div class="dining-slide">
                   <img src="https://content.vistana.com/files/live${p.path}" alt="${data.title}">
                 </div>`;
              }
            })
          );
        });
      } catch (e) { }

      currentIndex = 0;
      setSlideSizes();

      modal.style.display = 'flex';
      document.body.style.overflow = 'hidden';
    }

    function closeModal() {
      modal.style.display = 'none';
      document.body.style.overflow = '';
    }

    $$('.view-dining-link').forEach(btn =>
      on(btn, 'click', e => {
        e.preventDefault();
        openDiningModal(btn.closest('.dining-card'));
      })
    );

    on(prevBtn, 'click', () => { currentIndex--; updateTrack(); });
    on(nextBtn, 'click', () => { currentIndex++; updateTrack(); });
    on(closeBtn, 'click', closeModal);
    on(window, 'resize', debounce(setSlideSizes, 120));
  }

  function initDiningSliders(root = document) {
    const sliders = $$('.dining-cards', root);
    if (!sliders.length) return;

    if (window.SimpleSlider) {
      sliders.forEach(container => {
        const context = container.closest('.resort-sec') || document;
        if (window.SimpleSlider && window.SimpleSlider.init) {
          window.SimpleSlider.init(context, {
            trackSelector: '.dining-cards',
            cardSelector: '.dining-card',
            nextSelector: '.next',
            prevSelector: '.prev',
            gap: 24,
            visibleFn: () => window.innerWidth <= 767 ? 1 : 3
          });
        }
      });
      return;
    }

    sliders.forEach(track => {
      const cards = $$('.dining-card', track);
      if (!cards.length) return;

      const container = track.closest('.resort-sec') || track.parentElement || document;

      // Get ALL prev/next buttons (both desktop and mobile)
      const nextBtns = $$('.next', container);
      const prevBtns = $$('.prev', container);

      let index = 0;
      const getVisible = () => window.innerWidth <= 767 ? 1 : 3;

      const update = () => {
        const gap = 24;
        const cardWidth = cards[0].offsetWidth + gap;
        track.style.transform = `translateX(-${index * cardWidth}px)`;

        // Update ALL prev buttons
        prevBtns.forEach(btn => {
          if (btn) btn.classList.toggle('disabled', index === 0);
        });

        // Update ALL next buttons
        nextBtns.forEach(btn => {
          if (btn) btn.classList.toggle('disabled', index >= cards.length - getVisible());
        });
      };

      // Bind ALL next buttons
      nextBtns.forEach(btn => {
        on(btn, 'click', () => {
          if (index < cards.length - getVisible()) {
            index++;
            update();
          }
        });
      });

      // Bind ALL prev buttons
      prevBtns.forEach(btn => {
        on(btn, 'click', () => {
          if (index > 0) {
            index--;
            update();
          }
        });
      });

      on(window, 'resize', debounce(() => {
        index = 0;
        update();
      }, 120));

      update();
    });
  }


  /* -----------------------------
   * Filter normalization + update anchors
   * --------------------------- */

  function normalizeKeepSpaces(value) {
    return value
      .toLowerCase()
      .replace(/\s+-\s+/g, '-')
      .trim();
  }

  function updateFilterAnchors(root = document) {
    const baseLink = '/content/tmvcs/us/en/experiences/resorts.html';
    const anchors = root.querySelectorAll('.region-filter a, .state-filter a');

    anchors.forEach(a => {
      // Skip breadcrumb links in destination hero — handled by destination-breadcrumb.js
      if (a.closest('.hero-breadcrumb')) return;

      const isRegion = Boolean(a.closest('.region-filter'));
      const paramName = isRegion ? 'region' : 'search';
      const rawValue = (isRegion ? a.dataset.region : a.dataset.state) || a.textContent || '';
      const processedValue = isRegion ? normalizeKeepSpaces(rawValue) : rawValue;

      a.href = processedValue ? `${baseLink}?${paramName}=${encodeURIComponent(processedValue)}` : baseLink;
    });
  }


  /** Google Map API Code  Start */
  function initGoogleMap(root = document) {
    const mapEl = document.getElementById('map');
    if (!mapEl) return;

    if (mapEl.dataset.loaded === 'true') return;
    mapEl.dataset.loaded = 'true';

    let cachedKey = null;

    function getApiKey() {
      if (cachedKey) return Promise.resolve(cachedKey);

      return fetch('/bin/mvw/googleMapsApi')
        .then(res => res.json())
        .then(data => {
          cachedKey = data.googleMapApiKey;
          return cachedKey;
        });
    }

    function loadScript(apiKey) {
      return new Promise((resolve, reject) => {
        if (window.google && window.google.maps) {
          resolve();
          return;
        }

        window.__initMap = () => resolve();

        const script = document.createElement('script');
        script.src = `https://maps.googleapis.com/maps/api/js?key=${apiKey}&callback=__initMap`;
        script.async = true;
        script.defer = true;

        script.onerror = reject;

        document.head.appendChild(script);
      });
    }

    function renderMap() {
      const lat = parseFloat(mapEl.dataset.lat);
      const lng = parseFloat(mapEl.dataset.lng);

      const location = { lat, lng };

      const map = new google.maps.Map(mapEl, {
        zoom: 15,
        center: location,
        zoomControl: true,
        mapTypeControl: true,
        fullscreenControl: true,
        streetViewControl: false,
      });

      new google.maps.Marker({
        position: location,
        map: map,
      });
    }

    getApiKey()
      .then(loadScript)
      .then(renderMap)
      .catch(err => console.error('Google Map failed:', err));
  }

  /** Google Map API Code end */


  /* -----------------------------
   * Init all when DOM ready
   * --------------------------- */
  function trackResortPageView() {
    try {
      var path = window.location.pathname;
      var resortMatch = path.match(/\/experiences\/resorts\/([^\/]+)\.html/);
      if (!resortMatch) return;
      var slug = resortMatch[1];

      var MAX = 10;
      var ids = JSON.parse(localStorage.getItem('recentlyViewedResorts') || '[]');
      ids = ids.filter(function(id) { return id !== slug; });
      ids.unshift(slug);
      ids = ids.slice(0, MAX);
      localStorage.setItem('recentlyViewedResorts', JSON.stringify(ids));

      var ts = JSON.parse(localStorage.getItem('recentlyViewedResortsTimestamps') || '{}');
      ts[slug] = Date.now();
      localStorage.setItem('recentlyViewedResortsTimestamps', JSON.stringify(ts));
    } catch (e) {}
  }

  function initAll() {
    // bind amenity headers in the other-amenities container (if present)
    const amenContainer = document.querySelector('.other-amenities-container');
    if (amenContainer) bindSectionHeaders(amenContainer);

    // open services by default if exists
    const servicesContent = document.getElementById('services-content');
    if (servicesContent && !servicesContent.classList.contains(TOGGLE_CLASS)) {
      const header = servicesContent.previousElementSibling;
      if (header) toggleHeader(header);
    }

    initAlert();
    initContentToggles();
    initRatings();
    initTextSections();
    initNav();
    initAllAmenities();
    initDiningSliders();
    initMediaModal();
    updateFilterAnchors(document);
    initAccommodationsModal();
    initDiningModal();
    initGoogleMap();
    trackResortPageView();
  }

  if (document.readyState === 'loading') document.addEventListener('DOMContentLoaded', initAll, { once: true });
  else initAll();

  // expose API
  window.MVCResortDetail = { init: initAll, bindSectionHeaders, updateFilterAnchors };
})();
