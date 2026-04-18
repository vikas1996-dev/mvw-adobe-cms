import EditorialCarouselDetails from '../../destination-details/js/editorial-carousel.js';
import DOMPurify from 'dompurify';

class DestinationLanding {
  constructor(element) {
    this.element = element;
    this.data = null; 
    this.currentTab = 'regions';
    const urlParams = new URLSearchParams(window.location.search);
    this.activeRegionFilter = urlParams.get('region') || 'usa-east';
    this.apiEndpoint = this.element.dataset.apiEndpoint || this.buildApiEndpoint();
    this.imageBaseUrl = this.element.dataset.imageBaseUrl || 'https://content.vistana.com/files/live';
    this.isLoading = false;
    this.searchQuery = '';
    this.maxRecentlyViewed = 4;
    
    this.init();
  }

  buildApiEndpoint() {
    const pathname = window.location.pathname || '';

    if (!pathname || pathname === '/') {
      return '/_jcr_content.data.json';
    }

    if (pathname.endsWith('.html')) {
      return pathname.replace(/\.html$/, '/_jcr_content.data.json');
    }

    return `${pathname.replace(/\/$/, '')}/_jcr_content.data.json`;
  }

  init() {
    this.cacheElements();
    this.createSearchSuggestions();
    this.setupEventListeners();
    this.setupTabScrollArrow();
    this.loadData();
  }

  cacheElements() {
    // Search elements
    this.searchInput = this.element.querySelector('.search-input');
    this.searchIcon = this.element.querySelector('.search-icon');
    this.searchWrapper = this.element.querySelector('.search-wrapper');
    
    // Tab elements
    this.tabButtons = this.element.querySelectorAll('.tab-button');
    this.tabPanels = this.element.querySelectorAll('.tab-panel');
    
    // Regions panel elements
    this.filterPillsContainer = this.element.querySelector('.filter-pills');
    this.destinationCardsContainer = this.element.querySelector('.destination-cards');
    this.specialOfferContainer = this.element.querySelector('.special-offer-container');
    this.viewAllContainer = this.element.querySelector('.view-all-container');
    this.viewAllButton = this.element.querySelector('.btn-view-all');
    
    // Vacation types
    this.vacationTypeCardsContainer = this.element.querySelector('.vacation-type-cards');
    
    // Featured resorts carousel (editorial-carousel will handle it)
    this.editorialSlides = this.element.querySelector('.editorial-slides');
    this.announcer = this.element.querySelector('.sr-announcer');
  }

  getImageUrl(imagePath) {
    if (!imagePath) return '';
    if (imagePath.startsWith('http://') || imagePath.startsWith('https://')) {
      return imagePath;
    }
    return `${this.imageBaseUrl}${imagePath.startsWith('/') ? '' : '/'}${imagePath}`;
  }

  setupEventListeners() {
    if (this.searchInput) {
      this.searchInput.addEventListener('input', this.debounce(() => {
        this.searchQuery = this.searchInput.value;
        this.updateSearchSuggestions();
      }, 300));

      this.searchInput.addEventListener('focus', () => {
        this.updateSearchSuggestions();
      });

      this.searchInput.addEventListener('keydown', (e) => {
        if (e.key === 'Enter') {
          e.preventDefault();
          this.performSearch();
        }
        if (e.key === 'Escape') {
          this.hideSearchSuggestions();
        }
      });

      document.addEventListener('click', (e) => {
        if (this.searchWrapper && !this.searchWrapper.contains(e.target)) {
          this.hideSearchSuggestions();
        }
      });
    }

    if (this.searchIcon) {
      this.searchIcon.addEventListener('click', () => {
        this.performSearch();
      });
    }

    // Tab navigation
    this.tabButtons.forEach(button => {
      button.addEventListener('click', () => this.handleTabClick(button));
      button.addEventListener('keydown', (e) => this.handleTabKeydown(e, button));
    });

    // View All button
    if (this.viewAllButton) {
      this.viewAllButton.addEventListener('click', () => this.handleViewAll());
    }
  }

  setupTabScrollArrow() {
    const tabsContainer = this.element.querySelector('.tabs-container');
    const tabsNav = this.element.querySelector('.tabs-nav');
    
    if (!tabsContainer || !tabsNav) return;

    const scrollArrow = document.createElement('button');
    scrollArrow.className = 'tabs-scroll-arrow';
    scrollArrow.setAttribute('aria-label', 'Scroll tabs right');
    scrollArrow.innerHTML = '<i class="fa-solid fa-chevron-right" aria-hidden="true"></i>';
    
    tabsContainer.appendChild(scrollArrow);

    scrollArrow.addEventListener('click', (e) => {
      e.stopPropagation();
      tabsNav.scrollBy({ left: 150, behavior: 'smooth' });
    });
  }

  renderInitialContent() {
    if (!this.data) return;
  
    this.renderRegionFilters();
    this.renderDestinationCards();
    this.renderFeaturedResorts();
    this.announce(`Showing destinations for ${this.activeRegionFilter}`);
  }

  // Data Loading
  loadData() {
    this.showLoadingSpinner();
    this.announce('Loading destination data...');

    fetch(this.apiEndpoint)
      .then(response => {
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }
        return response.json();
      })
      .then(data => {
        this.data = data;
        
        // Build featured resorts array from referenceProperty in destinations
        this.data.featuredResorts = [];
        this.data.allResorts = [];
        if (this.data.destinations) {
          this.data.destinations.forEach(dest => {
            if (dest.referenceProperty && Array.isArray(dest.referenceProperty)) {
              dest.referenceProperty.forEach(resort => {
                if (resort.marshaCode) {
                  // All resorts flat list (for recently viewed matching)
                  if (!this.data.allResorts.find(r => r.marshaCode === resort.marshaCode)) {
                    this.data.allResorts.push(resort);
                  }
                  // Featured resorts only 
                  if (resort.featured === 'true') {
                    if (!this.data.featuredResorts.find(r => r.marshaCode === resort.marshaCode)) {
                      this.data.featuredResorts.push(resort);
                    }
                  }
                }
              });
            }
          });
        }
        
        this.hideLoadingSpinner();
        this.renderInitialContent();
        this.announce('Destination data loaded successfully');
      })
      .catch(error => {
        console.error('Failed to load destination data:', error);
        this.hideLoadingSpinner();
        this.showError('Unable to load destination data. Please try again later.');
        this.announce('Error loading destination data');
      });
  }

  showLoadingSpinner() {
    this.isLoading = true;
    const spinner = this.element.querySelector('.loading-spinner');
    if (spinner) {
      spinner.style.display = 'block';
      spinner.setAttribute('aria-hidden', 'false');
    }
    // Hide search wrapper while loading
    if (this.searchInput) {
      this.searchInput.disabled = true;
      this.searchInput.setAttribute('aria-disabled', 'true');
    }
    if (this.searchWrapper) {
      this.searchWrapper.classList.add('is-loading');
    }
    // Hide tab panels and featured resorts while loading
    this.tabPanels.forEach(panel => {
      panel.style.display = 'none';
    });
    const featuredResortsSection = this.element.querySelector('.featured-resorts-section');
    if (featuredResortsSection) {
      featuredResortsSection.style.display = 'none';
    }
  }

  hideLoadingSpinner() {
    this.isLoading = false;
    const spinner = this.element.querySelector('.loading-spinner');
    if (spinner) {
      spinner.style.display = 'none';
      spinner.setAttribute('aria-hidden', 'true');
    }
    // Show search wrapper after data is loaded
    if (this.searchInput) {
      this.searchInput.disabled = false;
      this.searchInput.setAttribute('aria-disabled', 'false');
    }
    if (this.searchWrapper) {
      this.searchWrapper.classList.remove('is-loading');
    }
    this.tabPanels.forEach(panel => {
      const isActive = panel.classList.contains('active');
      panel.style.display = '';
    });

    const featuredResortsSection = this.element.querySelector('.featured-resorts-section');
    if (featuredResortsSection) {
      const isRegionsTab = this.currentTab === 'regions';
      featuredResortsSection.style.display = isRegionsTab ? 'block' : 'none';
    }
  }

  showError(message) {
    const errorContainer = this.element.querySelector('.error-message');
    if (errorContainer) {
      errorContainer.textContent = message;
      errorContainer.style.display = 'block';
      errorContainer.setAttribute('role', 'alert');
    }
  }

  // Tab Navigation
  handleTabClick(button) {
    const tabName = button.dataset.tab;
    this.switchTab(tabName);
  }

  handleTabKeydown(e, button) {
    const buttons = Array.from(this.tabButtons);
    const currentIndex = buttons.indexOf(button);
    let newIndex;

    switch(e.key) {
      case 'ArrowLeft':
        e.preventDefault();
        newIndex = currentIndex > 0 ? currentIndex - 1 : buttons.length - 1;
        buttons[newIndex].focus();
        this.switchTab(buttons[newIndex].dataset.tab);
        break;
      case 'ArrowRight':
        e.preventDefault();
        newIndex = currentIndex < buttons.length - 1 ? currentIndex + 1 : 0;
        buttons[newIndex].focus();
        this.switchTab(buttons[newIndex].dataset.tab);
        break;
      case 'Home':
        e.preventDefault();
        buttons[0].focus();
        this.switchTab(buttons[0].dataset.tab);
        break;
      case 'End':
        e.preventDefault();
        buttons[buttons.length - 1].focus();
        this.switchTab(buttons[buttons.length - 1].dataset.tab);
        break;
    }
  }

  switchTab(tabName) {
    this.currentTab = tabName;
    
    this.tabButtons.forEach(button => {
      const isActive = button.dataset.tab === tabName;
      button.classList.toggle('active', isActive);
      button.setAttribute('aria-selected', isActive);
      button.setAttribute('tabindex', isActive ? '0' : '-1');
    });

    // Update panels
    this.tabPanels.forEach(panel => {
      const panelId = panel.id;
      const isActive = panelId === `${tabName}-panel`;
      panel.classList.toggle('active', isActive);
      panel.hidden = !isActive;
      
      panel.style.display = '';
    });

    // Show/hide featured resorts based on tab
    const featuredResortsSection = this.element.querySelector('.featured-resorts-section');
    if (featuredResortsSection) {
      if (tabName === 'regions') {
        featuredResortsSection.style.display = 'block';
      } else {
        featuredResortsSection.style.display = 'none';
      }
    }

    if (tabName === 'vacation-types') {
      this.renderVacationTypes();
    }

    const activeButton = this.element.querySelector(`.tab-button[data-tab="${tabName}"]`);
    if (activeButton) {

      activeButton.scrollIntoView({
        behavior: 'smooth',
        inline: 'center',
        block: 'nearest'
      });

      this.announce(`${activeButton.textContent.trim()} tab selected`);
    }
  }

  renderRegionFilters() {
    if (!this.filterPillsContainer || !this.data.regions) return;

    const regionNodenames = {};
    this.data.destinations.forEach(dest => {
      if (dest.region && dest.region.name && dest.region.nodename) {
        regionNodenames[dest.region.name] = dest.region.nodename;
      }
    });

    // Show all region pills (no "All" pill)
    const pills = this.data.regions.map(region => ({
      id: regionNodenames[region] || this.slugify(region),
      name: region
    }));

    this.filterPillsContainer.innerHTML = DOMPurify.sanitize(pills.map(pill => `
      <button 
        class="filter-pill ${pill.id === this.activeRegionFilter ? 'active' : ''}"
        data-region="${pill.id}"
        aria-pressed="${pill.id === this.activeRegionFilter}"
      >
        ${this.escapeHtml(pill.name)}
      </button>
    `).join(''));

    // Add event listeners
    this.filterPillsContainer.querySelectorAll('.filter-pill').forEach(pill => {
      pill.addEventListener('click', () => this.handleRegionFilter(pill));
    });
  }

  slugify(text) {
    return text.toLowerCase().replace(/\s+/g, '-').replace(/[^a-z0-9-]/g, '');
  }

  handleRegionFilter(pill) {
    const regionId = pill.dataset.region;
    this.activeRegionFilter = regionId;

    this.showAllDestinations = false;

    this.filterPillsContainer.querySelectorAll('.filter-pill').forEach(p => {
      const isActive = p.dataset.region === regionId;
      p.classList.toggle('active', isActive);
      p.setAttribute('aria-pressed', isActive);
    });

    // Update URL with selected region
    const url = new URL(window.location);
    if (regionId && regionId !== 'usa-east') {
      url.searchParams.set('region', regionId);
    } else {
      url.searchParams.delete('region');
    }
    window.history.replaceState({}, '', url);

    this.renderDestinationCards();
    this.filterFeaturedResortsByRegion();
    
    // Announce
    const regionName = regionId === 'all' ? 'All regions' : pill.textContent.trim();
    this.announce(`Showing destinations in ${regionName}`);
  }

  // Destination Cards
  renderDestinationCards() {
    if (!this.destinationCardsContainer || !this.data.destinations) return;

    let destinations = [];
    
    if (this.activeRegionFilter === 'all') {
      destinations = this.data.destinations;
    } else {
      // Filter by selected region using nodename
      destinations = this.data.destinations.filter(dest => 
        dest.region.nodename === this.activeRegionFilter
      );
    }

    const isMobile = window.innerWidth <= 768;
    const hasMoreThanFour = destinations.length > 4;
    const showLimited = isMobile && hasMoreThanFour && !this.showAllDestinations;
    
    const displayDestinations = showLimited ? destinations.slice(0, 4) : destinations;

    this.destinationCardsContainer.innerHTML = DOMPurify.sanitize(displayDestinations.map(dest => 
      this.createDestinationCard(dest)
    ).join(''));

    // Add event listeners
    this.destinationCardsContainer.querySelectorAll('.destination-card').forEach(card => {
      this.addCardInteractivity(card);
    });

    this.handleShowAllDestinationsButton(showLimited, hasMoreThanFour, isMobile);
  }

  handleShowAllDestinationsButton(showLimited, hasMoreThanFour, isMobile) {
    const existingBtn = this.element.querySelector('.btn-show-all-destinations');
    if (existingBtn) {
      existingBtn.remove();
    }

    if (showLimited) {
      const showAllBtn = document.createElement('button');
      showAllBtn.className = 'btn-show-all-destinations';
      showAllBtn.textContent = 'Show All Destinations';
      showAllBtn.setAttribute('type', 'button');
      
      this.destinationCardsContainer.parentNode.insertBefore(
        showAllBtn,
        this.destinationCardsContainer.nextSibling
      );

      showAllBtn.addEventListener('click', () => {
        this.showAllDestinations = true;
        this.renderDestinationCards();
      });

      if (this.viewAllContainer) {
        this.viewAllContainer.style.display = 'none';
      }
    } else {
      if (this.viewAllContainer && isMobile) {
        this.viewAllContainer.style.display = '';
      }
    }
  }

  createDestinationCard(destination) {
    const imagePath = destination.images && destination.images[0] && destination.images[0].photo && destination.images[0].photo[0] 
      ? destination.images[0].photo[0].path 
      : '';
    const imageUrl = imagePath ? this.getImageUrl(imagePath) : 'https://placehold.co/370x260';
    
    let url = `/content/tmvcs/us/en/destinations/${destination.nodename}`;
    if (destination.structuredContent && Array.isArray(destination.structuredContent)) {
      const slugEntry = destination.structuredContent.find(item => item.startsWith('slug='));
      if (slugEntry) {
        const slug = slugEntry.replace('slug=', '');
        url = `/content/tmvcs/us/en${slug}.html`;
      }
    }
    
    const resortCount = destination.resortCount || 0;

    return `
      <article class="destination-card" role="listitem">
        <a href="${this.escapeHtml(url)}" class="card-link" aria-label="${this.escapeHtml(destination.name)}, ${resortCount} resort${resortCount !== 1 ? 's' : ''}">
          <div class="card-image-wrapper">
            <img 
              src="${this.escapeHtml(imageUrl)}" 
              alt="${destination.images && destination.images[0] ? this.escapeHtml(destination.images[0].altText || destination.name) : this.escapeHtml(destination.name)}"
              loading="lazy"
            />
          </div>
          <div class="card-content">
            <h3 class="card-title">${this.escapeHtml(destination.name)}</h3>
            <p class="card-meta">${resortCount} Resort${resortCount !== 1 ? 's' : ''}</p>
            <span class="card-cta" role="button">View Destination</span>
          </div>
        </a>
      </article>
    `;
  }

  // Featured Resorts Carousel
  renderFeaturedResorts() {
    if (!this.editorialSlides || !this.data.featuredResorts) return;

    const resortsToShow = this.getFeaturedResortsForRegion();
    
    this.editorialSlides.innerHTML = DOMPurify.sanitize(resortsToShow.map((resort, index) =>
      this.createFeaturedResortCard(resort, index)
    ).join(''));
    
    // Re-initialize editorial carousel after content update
    this.initializeCarousel();
  }

  getFeaturedResortsForRegion() {
    if (!this.data.featuredResorts || this.data.featuredResorts.length === 0) {
      return [];
    }

    if (this.activeRegionFilter === 'all') {
      return this.data.featuredResorts;
    }
    
    const filteredResorts = [];
    
    if (this.data.destinations) {
      this.data.destinations.forEach(dest => {
        if (dest.region && dest.region.nodename === this.activeRegionFilter) {
          if (dest.referenceProperty && Array.isArray(dest.referenceProperty)) {
            dest.referenceProperty.forEach(resort => {
              if (resort.marshaCode && this.data.featuredResorts.find(r => r.marshaCode === resort.marshaCode)) {
                filteredResorts.push({
                  ...resort,
                  regionName: dest.region.name,
                  regionNodename: dest.region.nodename
                });
              }
            });
          }
        }
      });
    }
    
    return filteredResorts;
  }

  filterFeaturedResortsByRegion() {
    this.renderFeaturedResorts();
  }

  initializeCarousel() {
    const carouselContainer = this.element.querySelector('.featured-resorts-carousel');
    if (!carouselContainer) return;

    const slides = carouselContainer.querySelectorAll('.editorial-card');
    const slideCount = slides.length;

    if (slideCount === 1) {
      const navIcons = carouselContainer.querySelector('.nav-icons');
      if (navIcons) {
        const prevBtn = navIcons.querySelector('.nav-icon-left');
        const nextBtn = navIcons.querySelector('.nav-icon-right');
        if (prevBtn) {
          prevBtn.disabled = true;
          prevBtn.style.opacity = '0.5';
          prevBtn.style.cursor = 'not-allowed';
        }
        if (nextBtn) {
          nextBtn.disabled = true;
          nextBtn.style.opacity = '0.5';
          nextBtn.style.cursor = 'not-allowed';
        }
      }

      const currentSlide = carouselContainer.querySelector('.current-slide');
      if (currentSlide) {
        currentSlide.textContent = '1 / 1';
      }

      if (slides[0]) {
        slides[0].style.position = 'static';
        slides[0].style.transform = 'none';
        slides[0].style.opacity = '1';
        slides[0].classList.add('is-active');
      }

      if (carouselContainer._editorialCarouselDetailsInstance) {
        if (typeof carouselContainer._editorialCarouselDetailsInstance.destroy === 'function') {
          carouselContainer._editorialCarouselDetailsInstance.destroy();
        }
        carouselContainer._editorialCarouselDetailsInstance = null;
      }

      return;
    }

    const navIcons = carouselContainer.querySelector('.nav-icons');
    if (navIcons) {
      const prevBtn = navIcons.querySelector('.nav-icon-left');
      const nextBtn = navIcons.querySelector('.nav-icon-right');
      if (prevBtn) {
        prevBtn.disabled = false;
        prevBtn.style.opacity = '';
        prevBtn.style.cursor = '';
      }
      if (nextBtn) {
        nextBtn.disabled = false;
        nextBtn.style.opacity = '';
        nextBtn.style.cursor = '';
      }
    }

    if (carouselContainer._editorialCarouselDetailsInstance) {
      if (typeof carouselContainer._editorialCarouselDetailsInstance.destroy === 'function') {
        carouselContainer._editorialCarouselDetailsInstance.destroy();
      }
      carouselContainer._editorialCarouselDetailsInstance = null;
    }

    const carousel = new EditorialCarouselDetails(carouselContainer);
    
    const originalGetCardDimensions = carousel.getCardDimensions.bind(carousel);
    carousel.getCardDimensions = function() {
      const isMobile = window.innerWidth < this.config.mobileBreakpoint;
      let cardWidth = isMobile ? 342 : 792; 
      
      for (const card of this.slideElements) {
        const w = card.getBoundingClientRect().width || card.offsetWidth;
        if (w > 0) {
          cardWidth = w;
          break;
        }
      }

      const cardGap = isMobile ? this.config.cardGapMobile : this.config.cardGapDesktop;

      return {
        width: cardWidth,
        gap: cardGap,
        spacing: cardWidth + cardGap, 
      };
    };
    
    carouselContainer._editorialCarouselDetailsInstance = carousel;
    
    // Move keyboard focus to the newly active slide only when navigating via arrow keys
    const moveFocusToActiveSlide = () => {
      setTimeout(() => {
        const activeSlide = carouselContainer.querySelector('.editorial-card.is-active');
        if (activeSlide) activeSlide.focus();
      }, 320);
    };

    carouselContainer.addEventListener('keydown', (e) => {
      if (e.key === 'ArrowLeft' || e.key === 'ArrowRight') {
        moveFocusToActiveSlide();
      }
    });
    
    carousel.updateCarousel(false);
  }

  createFeaturedResortCard(resort, index) {
    const imageUrl = resort.image ? this.getImageUrl(resort.image) : '';
    const location = [resort.city, resort.state].filter(Boolean).join(', ') || 'Featured Resort';
    const inCollection = resort.collection === "City Collection";
    const logoUrl = resort.logo ? this.getImageUrl(resort.logo) : '';
    
    return `
      <article class="editorial-card" role="listitem" data-original-index="${index}" tabindex="0">
        <div class="resort-image-wrapper">
          <img src="${this.escapeHtml(imageUrl)}" alt="${this.escapeHtml(resort.name)}" loading="lazy" />
          ${inCollection ? `
            <div class="collection-badge">
              <div class="badge-content">
                <div class="badge-icon">
                  <i class="fa-light fa-building" aria-hidden="true"></i>
                </div>
                <div class="badge-text">City Collection</div>
              </div>
            </div>
          ` : ''}
        </div>
        <div class="resort-content">
          <div class="resort-info-wrapper">
            <div class="resort-text">
              <div class="resort-name">${this.escapeHtml(resort.name)}</div>
              <div class="resort-location">${this.escapeHtml(location)}</div>
            </div>
            
            <div class="resort-logo">
              <img src="${this.escapeHtml(logoUrl)}" alt="logo" />
            </div>
  
            <div class="resort-actions">
              <a href="/content/tmvcs/us/en/experiences/resorts/${resort.slug}.html" class="btn-view-details">
                <span>View Resort Details</span>
              </a>
            </div>
          </div>
        </div>
      </article>
    `;
  }

  addCardInteractivity(card) {
    const link = card.querySelector('.card-link');
    if (!link) return;

    // Keyboard support
    link.addEventListener('keydown', (e) => {
      if (e.key === 'Enter' || e.key === ' ') {
        e.preventDefault();
        link.click();
      }
    });

    // Touch support for mobile
    let touchStartTime;
    card.addEventListener('touchstart', () => {
      touchStartTime = Date.now();
    });

    card.addEventListener('touchend', (e) => {
      const touchDuration = Date.now() - touchStartTime;
      if (touchDuration < 200) { 
        card.classList.add('tapped');
        setTimeout(() => card.classList.remove('tapped'), 300);
      }
    });
  }

  renderVacationTypes() {
    if (!this.vacationTypeCardsContainer || !this.data.vacations) return;

    this.vacationTypeCardsContainer.innerHTML = DOMPurify.sanitize(this.data.vacations.map(vacation =>
      this.createVacationTypeCard(vacation)
    ).join(''));

    this.vacationTypeCardsContainer.querySelectorAll('.vacation-type-card').forEach(card => {
      this.addVacationTypeCardInteractivity(card);
    });
  }

  createVacationTypeCard(vacation) {
    const displayName = vacation.name
      .split('-')
      .map(word => word.charAt(0).toUpperCase() + word.slice(1))
      .join(' ');
    
    const url = `/content/tmvcs/us/en/experiences/resorts.html?vacationtype=${encodeURIComponent(vacation.nodename)}`;
    
    const imageUrl = vacation.image ? this.getImageUrl(vacation.image) : '';

    return `
      <article class="vacation-type-card" role="listitem" data-vacation-type="${this.escapeHtml(vacation.nodename)}">
        <a href="${this.escapeHtml(url)}" class="card-link" aria-label="${this.escapeHtml(displayName)} vacation">
          <div class="card-image-wrapper">
            ${imageUrl ? `<img src="${imageUrl}" alt="${this.escapeHtml(displayName)}" class="card-image" />` : ''}
          </div>
          <div class="card-content">
            <h3 class="card-title">${this.escapeHtml(displayName)}</h3>
            <span class="card-cta" role="button">View Resorts</span>
          </div>
        </a>
      </article>
    `;
  }

  addVacationTypeCardInteractivity(card) {
    const link = card.querySelector('.card-link');
    if (!link) return;

    link.addEventListener('keydown', (e) => {
      if (e.key === 'Enter' || e.key === ' ') {
        e.preventDefault();
        link.click();
      }
    });

    let touchStartTime;
    card.addEventListener('touchstart', () => {
      touchStartTime = Date.now();
    });

    card.addEventListener('touchend', (e) => {
      const touchDuration = Date.now() - touchStartTime;
      if (touchDuration < 200) { 
        card.classList.add('tapped');
        setTimeout(() => card.classList.remove('tapped'), 300);
      }
    });
  }

  // View All Button
  handleViewAll() {
    // Get the current active region filter
    const regionFilter = this.activeRegionFilter;
    
    // Build URL with region parameter
    let url = '/content/tmvcs/us/en/experiences/resorts.html';
    
    // Only add region parameter if it's not 'all'
    if (regionFilter && regionFilter !== 'all') {
      url += `?region=${encodeURIComponent(regionFilter)}`;
    }
    
    window.location.href = url;
  }

  // Search Functionality
  createSearchSuggestions() {
    if (!this.searchWrapper) return;
    const suggestionsDiv = document.createElement('div');
    suggestionsDiv.className = 'search-suggestions';
    suggestionsDiv.id = 'search-suggestions-list';
    suggestionsDiv.setAttribute('role', 'listbox');
    this.searchWrapper.appendChild(suggestionsDiv);
    this.searchSuggestions = suggestionsDiv;
  }

  updateSearchSuggestions() {
    if (!this.data || !this.searchSuggestions) return;

    const query = this.searchQuery.trim();

    // Empty query: show recently viewed or featured
    if (!query) {
      const recentlyViewedResorts = this.getRecentlyViewedResorts();
      const recentlyViewedDestinations = this.getRecentlyViewedDestinations();
      const hasHistory = recentlyViewedResorts.length > 0 || recentlyViewedDestinations.length > 0;

      if (hasHistory) {
        this.displaySearchSuggestions([], [], [], [...recentlyViewedResorts, ...recentlyViewedDestinations]);
      } else {
        const featuredResorts = this.data.featuredResorts ? this.data.featuredResorts.slice(0, 3) : [];
        this.displaySearchSuggestions([], featuredResorts, []);
      }
      this.searchSuggestions.classList.add('active');
      return;
    }

    // Search logic
    const normalizedQuery = this.normalizeText(query);
    
    // Search resorts
    const matchingResorts = this.data.featuredResorts ? this.data.featuredResorts.filter(resort => {
      const searchable = this.normalizeText(`${resort.name || ''} ${resort.city || ''} ${resort.state || ''} ${resort.country || ''}`);
      return searchable.includes(normalizedQuery);
    }).slice(0, 3) : [];

    // Search destinations (only show if destination page exists)
    const matchingDestinations = [];
    if (this.data.destinations) {
      this.data.destinations.forEach(dest => {
        const destName = dest.name || '';
        if (this.normalizeText(destName).includes(normalizedQuery)) {
          matchingDestinations.push(destName);
        }
      });
    }

    const uniqueDestinations = [...new Set(matchingDestinations)].slice(0, 3);

    if (matchingResorts.length === 0 && uniqueDestinations.length === 0) {
      const recentlyViewedResorts = this.getRecentlyViewedResorts();
      const recentlyViewedDestinations = this.getRecentlyViewedDestinations();

      if (recentlyViewedResorts.length === 0 && recentlyViewedDestinations.length === 0) {
        const featured = this.data.featuredResorts ? this.data.featuredResorts.slice(0, 3) : [];
        let html = '';
        if (featured.length > 0) {
          html += `<div class="search-suggestion-section-header">Featured Resorts</div>`;
          featured.forEach(resort => {
            html += this.renderSuggestionItem(resort, 'resort', query);
          });
        }
        
        const vacationTypes = this.data.vacations || [];
        if (vacationTypes.length > 0) {
          html += this.renderVacationTypeSuggestions(vacationTypes, query);
        }
        
        this.searchSuggestions.innerHTML = DOMPurify.sanitize(html);
        this.attachSuggestionEvents();
        this.searchSuggestions.classList.add('active');
      } else {
        let html = this.renderRecentHistorySection(recentlyViewedResorts, recentlyViewedDestinations, query);
        this.searchSuggestions.innerHTML = DOMPurify.sanitize(html);
        this.attachSuggestionEvents();
        this.searchSuggestions.classList.add('active');
      }
      return;
    }

    this.displaySearchSuggestions(matchingResorts, [], uniqueDestinations);
    this.searchSuggestions.classList.add('active');
  }

  renderVacationTypeSuggestions(vacationTypes, query) {
    let html = `<div class="vacation-type-container"><div class="vacation-type-header">Browse by Vacation Type</div><div class="vacation-type-list">`;
    
    vacationTypes.forEach(vt => {
      const displayName = vt.name
        .split('-')
        .map(word => word.charAt(0).toUpperCase() + word.slice(1))
        .join(' ');
      
      let icon = "umbrella-beach";
      const val = displayName.toLowerCase();
      if (val.includes("mountain") || val.includes("ski")) icon = "person-skiing";
      else if (val.includes("island")) icon = "tree-palm";
      else if (val.includes("city") || val.includes("urban")) icon = "tree-city";

      html += `
        <div class="search-suggestion-item-vacation-type" role="option" tabindex="0" data-type="vacationType" data-value="${this.escapeHtml(vt.nodename)}">
          <div class="vacation-type-content">
            <i class="fas fa-${icon}"></i>
            <div class="vacation-type-name">${this.getSafeHighlight(displayName, query)}</div>
          </div>
          <i class="fas fa-arrow-right"></i>
        </div>`;
    });
    
    html += `</div></div>`;
    return html;
  }

  getSafeHighlight(originalText, query) {
    if (!originalText || !query) return originalText;
    const escapedQuery = query.trim().replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
    const ignoredChars = "[\\.\\-''ʻ'\"\"\\s,]*";
    const fuzzyPattern = escapedQuery.split('').join(ignoredChars);
    try {
      const regex = new RegExp(`(${fuzzyPattern})`, 'gi');
      return originalText.replace(regex, '<strong>$1</strong>');
    } catch (e) {
      return originalText;
    }
  }

  renderSuggestionItem(item, type, query) {
    const isResort = type === 'resort';
    const isDestination = type === 'destination';
    
    let primaryText, secondaryText, imgSrc, dataValue;
    
    if (isResort) {
      primaryText = item.name || '';
      secondaryText = `${item.city || ''}${item.state ? `, ${item.state}` : ''}${item.country ? `, ${item.country}` : ''}`;
      imgSrc = item.image ? this.getImageUrl(item.image) : '';
      dataValue = item.marshaCode || item.name;
    } else if (isDestination) {
      primaryText = item.split(',')[0];
      secondaryText = item;
      imgSrc = '';
      dataValue = item;
    }

    return `
      <div class="search-suggestion-item" role="option" tabindex="0" data-type="${type}" data-value="${this.escapeHtml(dataValue)}">
        ${isResort && imgSrc ? `<div class="suggestion-thumbnail"><img src="${this.escapeHtml(imgSrc)}" alt="" onerror="this.style.display='none'"/></div>` : `<div class="suggestion-icon"><i class="fas ${isResort ? 'fa-hotel' : 'fa-map-marker-alt'}" aria-hidden="true"></i></div>`}
        <div class="suggestion-content">
          <div class="suggestion-text-primary">${this.getSafeHighlight(primaryText, query)}</div>
          <div class="suggestion-text-secondary">${this.getSafeHighlight(secondaryText, query)}</div>
        </div>
      </div>
    `;
  }

  renderRecentHistorySection(resorts, destinations, query) {
    const allRecent = [];
    const rTimestamps = this.getStoredTimestamps('recentlyViewedResorts');
    const dTimestamps = this.getStoredTimestamps('recentlyViewedDestinations');

    resorts.forEach(item => allRecent.push({ item, type: 'resort', ts: rTimestamps[item.marshaCode] || rTimestamps[item.slug] || rTimestamps[item.name] || 0 }));
    destinations.forEach(item => allRecent.push({ item, type: 'destination', ts: dTimestamps[item] || 0 }));
    allRecent.sort((a, b) => b.ts - a.ts);

    let html = `<div class="search-suggestion-section-header">Recently Viewed</div>`;
    allRecent.slice(0, 4).forEach(({ item, type }) => {
      html += this.renderSuggestionItem(item, type, query);
    });
    return html;
  }

  displaySearchSuggestions(matchingResorts, featuredResorts, uniqueDestinations, recentlyViewed = null) {
    const query = this.searchQuery.trim();
    let html = '';

    if (recentlyViewed && recentlyViewed.length > 0) {
      html += this.renderRecentHistorySection(this.getRecentlyViewedResorts(), this.getRecentlyViewedDestinations(), query);
    }

    if (featuredResorts.length > 0) {
      html += `<div class="search-suggestion-section-header">Featured Resorts</div>`;
      featuredResorts.forEach(r => html += this.renderSuggestionItem(r, 'resort', query));
      
      const vacationTypes = this.data.vacations || [];
      if (vacationTypes.length > 0) {
        html += this.renderVacationTypeSuggestions(vacationTypes, query);
      }
    }

    if (uniqueDestinations.length > 0) {
      uniqueDestinations.forEach(dest => html += this.renderSuggestionItem(dest, 'destination', query));
    }

    if (matchingResorts.length > 0) {
      matchingResorts.forEach(r => html += this.renderSuggestionItem(r, 'resort', query));
    }

    this.searchSuggestions.innerHTML = DOMPurify.sanitize(html);
    this.attachSuggestionEvents();
  }

  attachSuggestionEvents() {
    if (!this.searchSuggestions) return;

    this.searchSuggestions.querySelectorAll('.search-suggestion-item').forEach(item => {
      item.addEventListener('click', () => {
        const type = item.dataset.type;
        const value = item.dataset.value;
        this.handleSuggestionClick(type, value, item);
      });

      item.addEventListener('keydown', (e) => {
        if (e.key === 'Enter' || e.key === ' ') {
          e.preventDefault();
          const type = item.dataset.type;
          const value = item.dataset.value;
          this.handleSuggestionClick(type, value, item);
        }
      });
    });

    this.searchSuggestions.querySelectorAll('.search-suggestion-item-vacation-type').forEach(item => {
      item.addEventListener('click', () => {
        const value = item.dataset.value;
        this.handleVacationTypeClick(value);
      });

      item.addEventListener('keydown', (e) => {
        if (e.key === 'Enter' || e.key === ' ') {
          e.preventDefault();
          const value = item.dataset.value;
          this.handleVacationTypeClick(value);
        }
      });
    });
  }

  performSearch() {
    const query = this.searchInput.value.trim();
    
    if (query) {
      this.hideSearchSuggestions();
      const targetUrl = '/content/tmvcs/us/en/experiences/resorts.html';
      const searchUrl = `${targetUrl}?search=${encodeURIComponent(query)}`;
      window.location.href = searchUrl;
    }
  }

  handleSuggestionClick(type, value, itemElement) {
    if (type === 'resort') {
      const allResorts = this.data.allResorts || this.data.featuredResorts || [];
      const resort = allResorts.find(r => (r.marshaCode || r.name) === value);
      if (resort) {
        const slug = resort.slug || resort.marshaCode.toLowerCase();
        window.location.href = `/content/tmvcs/us/en/experiences/resorts/${slug}.html`;
        return;
      }
    } else if (type === 'destination') {
      const destination = this.data.destinations ? this.data.destinations.find(dest => dest.name === value) : null;
      
      let url = `/content/tmvcs/us/en/destinations/${value.toLowerCase().replace(/[^a-z0-9]+/g, '-')}.html`;
      
      if (destination && destination.structuredContent && Array.isArray(destination.structuredContent)) {
        const slugEntry = destination.structuredContent.find(item => item.startsWith('slug='));
        if (slugEntry) {
          const slug = slugEntry.replace('slug=', '');
          url = `/content/tmvcs/us/en${slug}.html`;
        }
      }
      
      window.location.href = url;
    }
    
    this.hideSearchSuggestions();
  }

  handleVacationTypeClick(vacationTypeValue) {
    const url = `/content/tmvcs/us/en/experiences/resorts.html?vacationtype=${encodeURIComponent(vacationTypeValue)}`;
    window.location.href = url;
    this.hideSearchSuggestions();
  }

  hideSearchSuggestions() {
    if (this.searchSuggestions) {
      this.searchSuggestions.classList.remove('active');
    }
  }

  normalizeText(text) {
    if (!text) return '';
    return text.toString().toLowerCase()
      .normalize('NFD').replace(/[\u0300-\u036f]/g, '')
      .replace(/[''ʻ'""]/g, '')
      .replace(/[.,"!?;:()\[\]{}]/g, '')
      .replace(/\s+/g, ' ').trim();
  }

  // Recently Viewed - Resorts
  getRecentlyViewedResorts() {
    try {
      const stored = localStorage.getItem('recentlyViewedResorts');
      if (!stored || !this.data || !this.data.allResorts) return [];
      const resortIds = JSON.parse(stored);
      return resortIds.map(id => this.data.allResorts.find(r => r.slug === id || r.marshaCode === id || (r.marshaCode && r.marshaCode.toLowerCase() === id.toLowerCase()))).filter(Boolean).slice(0, this.maxRecentlyViewed);
    } catch (e) {
      return [];
    }
  }


  // Recently Viewed - Destinations
  getRecentlyViewedDestinations() {
    try {
      const stored = localStorage.getItem('recentlyViewedDestinations');
      if (!stored) return [];
      return JSON.parse(stored).slice(0, this.maxRecentlyViewed);
    } catch (e) {
      return [];
    }
  }

  getStoredTimestamps(key) {
    try {
      return JSON.parse(localStorage.getItem(`${key}Timestamps`) || '{}');
    } catch (e) {
      return {};
    }
  }

  debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
      const later = () => {
        clearTimeout(timeout);
        func(...args);
      };
      clearTimeout(timeout);
      timeout = setTimeout(later, wait);
    };
  }

  // Utilities
  announce(message) {
    if (this.announcer) {
      this.announcer.textContent = message;
    }
  }

  announceError(message) {
    if (this.announcer) {
      this.announcer.textContent = message;
      this.announcer.setAttribute('role', 'alert');
      setTimeout(() => {
        this.announcer.setAttribute('role', 'status');
      }, 1000);
    }
  }

  escapeHtml(text) {
    if (!text) return '';
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
  }
}

// Initialize component when DOM is ready
if (typeof document !== 'undefined') {
  const init = () => {
    const elements = document.querySelectorAll('[data-component="destination-landing"]');
    elements.forEach(element => {
      if (!element._destinationLandingInstance) {
        element._destinationLandingInstance = new DestinationLanding(element);
      }
    });
  };
  
  
  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', init);
  } else {
    init();
  }
}

export default DestinationLanding;
