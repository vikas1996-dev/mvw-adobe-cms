class ResortList {
  constructor(element) {
    this.element = element;

    const ds = this.element.dataset;
    this.pageSize = parseInt(ds.pageSize) || 24;
    this.offerInterval = 12;
    this.apiEndpoint = ds.apiEndpoint || this.buildApiEndpoint();
    this.destinationsApiEndpoint = '/bin/mvw/experiences/searchDestinations';
    this.imageBaseUrl = ds.imageBaseUrl || 'https://content.vistana.com/files/live';

    this.allResorts = [];
    this.filteredResorts = [];
    this.destinations = [];
    this.activeFilters = {
      region: [],
      vacationType: [],
      activities: [],
      brand: []
    };
    this.searchQuery = '';
    this.currentSort = 'promoted';
    this.currentPage = 1;
    this.totalResorts = 0;
    this.isLoading = false;
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
    this.searchInput = this.element.querySelector('.search-input');
    this.searchIcon = this.element.querySelector('.search-icon');
    this.searchWrapper = this.element.querySelector('.search-wrapper');
    this.resortGrid = this.element.querySelector('.resort-grid');
    this.resultsCount = this.element.querySelector('.results-count');
    this.filtersToggle = this.element.querySelector('.filters-toggle');
    this.filtersSidebar = this.element.querySelector('.filters-sidebar');
    this.closeFilters = this.element.querySelector('.close-filters');
    this.filterOverlay = this.element.querySelector('.filter-overlay');
    this.clearFiltersBtn = this.element.querySelector('.clear-filters');
    this.applyFiltersBtn = this.element.querySelector('.apply-filters');
    this.activeFiltersContainers = this.element.querySelectorAll('.active-filters');
    this.announcer = this.element.querySelector('.sr-announcer');

    this.applyUrlParams();
    this.createSearchSuggestions();
    this.setupEventListeners();
    this.loadData();
  }

  getImageUrl(imagePath) {
    if (!imagePath) return '';
    if (imagePath.startsWith('http://') || imagePath.startsWith('https://')) {
      return imagePath;
    }
    return `${this.imageBaseUrl}/${imagePath}`;
  }

  createSearchSuggestions() {
    if (!this.searchWrapper) return;
    const suggestionsDiv = document.createElement('div');
    suggestionsDiv.className = 'search-suggestions';
    suggestionsDiv.id = 'search-suggestions-list';
    suggestionsDiv.setAttribute('role', 'listbox');
    this.searchWrapper.appendChild(suggestionsDiv);
    this.searchSuggestions = suggestionsDiv;
  }

  announce(message) {
    if (this.announcer) {
      this.announcer.textContent = message;
    }
  }

  setupEventListeners() {
    if (this.searchInput) {
      this.searchInput.addEventListener('input', this.debounce(() => {
        this.searchQuery = this.searchInput.value;
        this.updateSearchSuggestions();
        
        if (!this.searchQuery.trim()) {
          this.applyFilters();
        }
      }, 300));

      this.searchInput.addEventListener('focus', () => {
        this.updateSearchSuggestions();
      });

      this.searchInput.addEventListener('keydown', (e) => {
        if (e.key === 'Enter') {
          e.preventDefault();
          this.searchQuery = this.searchInput.value;
          this.hideSearchSuggestions();
          this.applyFilters();
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
        this.hideSearchSuggestions();
        this.applyFilters();
      });
    }

    const sortIds = ['#sort-container', '#sort-container-mobile'];
    sortIds.forEach(id => {
      const container = this.element.querySelector(id);
      if (!container) return;

      const dropdownItems = container.querySelectorAll('.dropdown-item');

      const toggleMenu = (e) => {
        const isOpen = container.classList.contains('open');
        this.closeAllSorts();
        if (!isOpen) {
          container.classList.add('open');
          container.setAttribute('aria-expanded', 'true');

          if (e.type === 'keydown') {
            const firstItem = container.querySelector('.dropdown-item.active') || dropdownItems[0];
            setTimeout(() => firstItem?.focus(), 10);
          }
        }
      };

      container.addEventListener('click', (e) => {
        if (!e.target.closest('.dropdown-item')) {
          toggleMenu(e);
        }
      });

      container.addEventListener('keydown', (e) => {
        if (e.target === container && (e.key === 'Enter' || e.key === ' ')) {
          e.preventDefault();
          toggleMenu(e);
        }

        if (e.key === 'Escape') {
          this.closeAllSorts();
          container.focus();
        }
      });

      dropdownItems.forEach(item => {
        item.setAttribute('tabindex', '0');

        const handleSelect = (e) => {
          e.stopPropagation();
          const value = item.getAttribute('data-value');
          const text = item.textContent;

          this.element.querySelectorAll('.dropdown-item').forEach(i => {
            i.classList.remove('active');
            i.setAttribute('aria-selected', 'false');
          });

          this.element.querySelectorAll(`.dropdown-item[data-value="${value}"]`).forEach(i => {
            i.classList.add('active');
            i.setAttribute('aria-selected', 'true');
          });

          this.element.querySelectorAll('.sort-label').forEach(lbl => {
            lbl.textContent = `Sort By: ${text}`;
          });

          this.closeAllSorts();
          this.currentSort = value;
          this.sortResorts();
          this.renderResorts();
          this.announce(`Sorted by ${text}`);
          container.focus();
        };

        item.addEventListener('click', handleSelect);
        item.addEventListener('keydown', (e) => {
          if (e.key === 'Enter' || e.key === ' ') {
            e.preventDefault();
            handleSelect(e);
          }
          if (e.key === 'ArrowDown') {
            e.preventDefault();
            item.nextElementSibling?.focus();
          }
          if (e.key === 'ArrowUp') {
            e.preventDefault();
            item.previousElementSibling?.focus();
          }
        });
      });
    });

    document.addEventListener('click', (e) => {
      const isClickInsideSort = sortIds.some(id => {
        const container = this.element.querySelector(id);
        return container && container.contains(e.target);
      });
      
      if (!isClickInsideSort) {
        this.closeAllSorts();
      }
    });

    const toggleMobileMenu = (active) => {
      this.filtersSidebar?.classList.toggle('active', active);
      this.filterOverlay?.classList.toggle('active', active);
      document.body.style.overflow = active ? 'hidden' : '';

      if (active) {
        this.filtersSidebar?.setAttribute('aria-hidden', 'false');
        setTimeout(() => this.closeFilters?.focus(), 100);
      } else {
        this.filtersSidebar?.setAttribute('aria-hidden', 'true');
        this.filtersToggle?.focus();
      }
    };

    this.filtersToggle?.addEventListener('click', () => toggleMobileMenu(true));
    this.closeFilters?.addEventListener('click', () => toggleMobileMenu(false));
    this.filterOverlay?.addEventListener('click', () => toggleMobileMenu(false));

    this.clearFiltersBtn?.addEventListener('click', () => this.clearFilters());

    this.element.querySelectorAll('.filter-title').forEach(title => {
      title.addEventListener('click', () => {
        const isCollapsed = title.classList.toggle('collapsed');
        title.setAttribute('aria-expanded', !isCollapsed);
        title.nextElementSibling?.classList.toggle('hidden');
      });
    });
  }

  closeAllSorts() {
    this.element.querySelectorAll('.sort-container').forEach(c => {
      c.classList.remove('open');
      c.setAttribute('aria-expanded', 'false');
    });
  }

  applyUrlParams() {
    const params = new URLSearchParams(window.location.search);
    const paramMap = {
      'region': 'region',
      'vacationtype': 'vacationType',
      'activity': 'activities',
      'brand': 'brand'
    };

    Object.keys(paramMap).forEach(paramKey => {
      const internalKey = paramMap[paramKey];
      const values = params.getAll(paramKey);
      if (values.length > 0) {
        const explodedValues = values.flatMap(v => v.split(','));
        this.activeFilters[internalKey] = explodedValues;
      }
    });

    if (params.has('search')) {
      this.searchQuery = params.get('search');
      if (this.searchInput) this.searchInput.value = this.searchQuery;
    }
  }

  loadData() {
    this.showLoadingSpinner();
    this.announce('Loading resort data...');

    Promise.all([
      fetch(this.apiEndpoint).then(response => {
        if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
        return response.json();
      }),
      fetch(this.destinationsApiEndpoint).then(response => {
        if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
        return response.json();
      }).catch(err => {
        console.warn('Failed to load destinations data:', err);
        return { destinations: [] };
      })
    ])
      .then(([resortData, destinationsData]) => {
        this.allResorts = resortData.resorts || [];
        this.filteredResorts = [...this.allResorts];
        this.totalResorts = resortData.totalResorts || this.allResorts.length;
        this.destinations = destinationsData.destinations || destinationsData || [];

        this.filterOptionLabels = { region: {}, vacationType: {}, activities: {}, brand: {} };
        const fd = resortData.filters?.[0];
        if (fd) {
          fd.regions?.forEach(opt => this.filterOptionLabels.region[opt.nodename] = opt.name);
          fd.vacationTypes?.forEach(opt => this.filterOptionLabels.vacationType[opt.nodename] = opt.name);
          fd.activities?.forEach(opt => this.filterOptionLabels.activities[opt.nodename] = opt.name);
          fd.brands?.forEach(opt => this.filterOptionLabels.brand[opt.nodename] = opt.name);
          this.renderFilters(fd);
        }

        this.offerCards = resortData.offerCards || [];
        this.hideLoadingSpinner();
        this.applyFilters();
        this.updateClearAllVisibility();
      })
      .catch(error => {
        console.error('Error loading resort data:', error);
        this.showError('Failed to load resort data. Please try again later.');
        this.hideLoadingSpinner();
      });
  }

  showLoadingSpinner() {
    this.isLoading = true;
    if (this.searchWrapper) {
      this.searchWrapper.style.display = 'none';
    }
    if (this.resortGrid) {
      this.resortGrid.innerHTML = `
        <div class="loading-spinner" role="status">
          <div class="spinner"></div>
          <p>Loading resorts...</p>
        </div>
      `;
    }
  }

  hideLoadingSpinner() {
    this.isLoading = false;
    // Show search wrapper after data is loaded
    if (this.searchWrapper) {
      this.searchWrapper.style.display = '';
    }
  }

  showError(message) {
    if (this.resortGrid) {
      this.resortGrid.innerHTML = `
        <div class="error-message" role="alert">
          <div class="error-icon"><i class="fas fa-exclamation-triangle"></i></div>
          <p>${message}</p>
        </div>
      `;
    }
  }

  renderFilters(filterData) {
    ['region', 'vacationType', 'activities', 'brand'].forEach(type => {
      const dataKey = type === 'vacationType' ? 'vacationTypes' : type + (type.endsWith('s') ? '' : 's');
      const options = filterData[dataKey] || [];
      const processedOptions = type === 'region' ? [...options] : [...options].sort((a, b) => (a.name || '').localeCompare(b.name || ''));
      this.renderFilterGroup(type, processedOptions);
    });
  }

  renderFilterGroup(filterName, options) {
    const targetGroup = this.element.querySelector(`[data-filter-type="${filterName}"]`);
    const optionsContainer = targetGroup?.querySelector('.filter-options');
    if (!optionsContainer) return;

    optionsContainer.innerHTML = options.map(option => {
      const isChecked = this.activeFilters[filterName].includes(option.nodename) ? 'checked' : '';
      return `
        <label>
          <input type="checkbox" name="${filterName}" value="${option.nodename}" tabindex="0" ${isChecked}>
          <span>${option.name}</span>
        </label>
      `;
    }).join('');

    optionsContainer.querySelectorAll('input').forEach(cb => {
      cb.addEventListener('change', (e) => this.handleFilterChange(filterName, e.target.value, e.target.checked));
      cb.addEventListener('keydown', (e) => {
        if (e.key === 'Enter') {
          e.preventDefault();
          cb.checked = !cb.checked;
          this.handleFilterChange(filterName, cb.value, cb.checked);
        }
      });
    });
  }

  handleFilterChange(filterType, value, checked) {
    if (checked) {
      if (!this.activeFilters[filterType].includes(value)) this.activeFilters[filterType].push(value);
    } else {
      this.activeFilters[filterType] = this.activeFilters[filterType].filter(v => v !== value);
    }
    this.applyFilters();
    this.updateClearAllVisibility();
  }

  updateClearAllVisibility() {
    if (!this.clearFiltersBtn) return;
    const hasActive = Object.values(this.activeFilters).some(arr => arr.length > 0) || this.searchQuery;
    this.clearFiltersBtn.style.display = hasActive ? 'flex' : 'none';
  }

  normalizeText(text) {
    if (!text) return '';
    return text.toString().toLowerCase()
      .normalize('NFD').replace(/[\u0300-\u036f]/g, '')
      .replace(/['’ʻ‘“”]/g, '')
      .replace(/[.,"!?;:()\[\]{}]/g, '')
      .replace(/\s+/g, ' ').trim();
  }

  applyFilters() {
    const query = this.normalizeText(this.searchQuery);

    this.filteredResorts = this.allResorts.filter(resort => {
      if (query) {
        const searchableParts = [
          resort.name,
          resort.city,
          resort.state,
          resort.country,
          resort.region,
          resort.dcmBrand?.name || resort.dcmBrand
        ];
        const searchableString = this.normalizeText(
          searchableParts.filter(part => part !== null && part !== undefined && part !== '').join(' ')
        );
        if (!searchableString.includes(query)) return false;
      }

      const checkFilter = (type, resortField) => {
        if (this.activeFilters[type].length === 0) return true;
        const activeLabels = this.activeFilters[type].map(id => this.filterOptionLabels[type][id]?.toLowerCase());
        if (Array.isArray(resortField)) {
          return resortField.some(val => activeLabels.includes(val?.toLowerCase()));
        }
        const fieldVal = typeof resortField === 'object' ? resortField?.name : resortField;
        return activeLabels.includes(fieldVal?.toLowerCase());
      };

      return checkFilter('region', resort.region) &&
             checkFilter('vacationType', resort.vacationType) &&
             checkFilter('activities', resort.activities) &&
             checkFilter('brand', resort.dcmBrand);
    });

    this.currentPage = 1;
    this.updateUrlParameters();
    this.sortResorts();
    this.renderResorts();
    this.updateResultsCount();
    this.renderActiveFilterPills();
    this.updateClearAllVisibility();
    const count = this.filteredResorts.length;
    this.announce(`Filtered results. Found ${count} resort${count === 1 ? '' : 's'}.`);
  }

  updateUrlParameters() {
    const url = new URL(window.location);
    ['region', 'vacationtype', 'activity', 'brand', 'search'].forEach(p => url.searchParams.delete(p));

    if (this.activeFilters.region.length) url.searchParams.set('region', this.activeFilters.region.join(','));
    if (this.activeFilters.vacationType.length) url.searchParams.set('vacationtype', this.activeFilters.vacationType.join(','));
    if (this.activeFilters.activities.length) url.searchParams.set('activity', this.activeFilters.activities.join(','));
    if (this.activeFilters.brand.length) url.searchParams.set('brand', this.activeFilters.brand.join(','));
    if (this.searchQuery) url.searchParams.set('search', this.searchQuery);

    window.history.replaceState({}, '', url);
  }

  sortResorts() {
    const strategies = {
      'promoted': (a, b) => {
        const originalOrderA = this.allResorts.findIndex(resort => resort.universalPropertyCode === a.universalPropertyCode);
        const originalOrderB = this.allResorts.findIndex(resort => resort.universalPropertyCode === b.universalPropertyCode);
        return originalOrderA - originalOrderB;
      },
      'name-asc': (a, b) => {
        const brandA = a.dcmBrand?.name || '';
        const brandB = b.dcmBrand?.name || '';
        return brandA.localeCompare(brandB);
      },
      'name-desc': (a, b) => (a.city || '').localeCompare(b.city || ''),
      'location': (a, b) => (b.city || '').localeCompare(a.city || ''),
      'rating': (a, b) => (parseFloat(b.rating) || 0) - (parseFloat(a.rating) || 0),
      'reviews': (a, b) => (parseInt(b.reviews) || 0) - (parseInt(a.reviews) || 0)
    };
    if (strategies[this.currentSort]) this.filteredResorts.sort(strategies[this.currentSort]);
  }

  renderResorts() {
    if (!this.resortGrid) return;
    const resortsToShow = this.filteredResorts.slice(0, this.currentPage * this.pageSize);

    if (resortsToShow.length === 0) {
      this.resortGrid.innerHTML = '<div class="no-results"><p>No results.</p></div>';
      return;
    }

    let html = '';
    const offerCards = this.offerCards || [];
    
    const isMobile = window.innerWidth < 769;
    const currentOfferInterval = isMobile ? 6 : this.offerInterval; // 6 for mobile, 12 for desktop

    for (let i = 0; i < resortsToShow.length; i++) {
      html += this.createResortCard(resortsToShow[i]);
      if ((i + 1) % currentOfferInterval === 0) {
        const offerIndex = Math.floor(i / currentOfferInterval) % offerCards.length;
        const offer = offerCards[offerIndex];
        if (offer) html += this.createOfferCard(offer);
      }
    }

    if (resortsToShow.length < this.filteredResorts.length) {
      html += `<div class="load-more-container"><button type="button" class="load-more" tabindex="0">Show More Resorts</button></div>`;
    }

    this.resortGrid.innerHTML = html;

    this.resortGrid.querySelectorAll('.resort-card').forEach(card => {
      card.addEventListener('click', (e) => {
        if (!e.target.closest('.tripadvisor-rating')) {
          const url = card.getAttribute('data-href');
          if (url) {
            const slug = url.split('/').pop().replace('.html', '');
            
            const resort = this.allResorts.find(r => {
              const match = r.slug === slug || (r.marshaCode && r.marshaCode.toLowerCase() === slug.toLowerCase());
              return match;
            });
            
            window.location.href = url;
          }
        }
      });
    });
    
    this.resortGrid.querySelectorAll('.view-resort').forEach(link => {
      link.addEventListener('click', (e) => {
        e.preventDefault();
        const url = link.getAttribute('href');
        if (url) {
          const slug = url.split('/').pop().replace('.html', '');
          const resort = this.allResorts.find(r => r.slug === slug || (r.marshaCode && r.marshaCode.toLowerCase() === slug.toLowerCase()));
          window.location.href = url;
        }
      });
    });
    
    this.resortGrid.querySelector('.load-more')?.addEventListener('click', () => this.loadMore());
  }

  createOfferCard(offer) {
    const img = offer.images?.[0]?.path || '';
    const imageUrl = this.getImageUrl(img);
    return `
      <section class="offer-card" aria-label="Offer: ${offer.title || ''}">
        <div class="offer-image">
          <img src="${imageUrl}" alt="${offer.title}" role="presentation" loading="lazy">
          ${offer.badge ? `<div class="offer-badge" aria-hidden="true"><span>${offer.badge}</span></div>` : ''}
        </div>
        <div class="offer-content">
          <div class="offer-text">
            <div class="offer-eyebrow">${offer.eyebrow || ''}</div>
            <h2 class="offer-title">${offer.title || ''}</h2>
            <div class="offer-description">${offer.shortDescriptionAd || offer.descriptionAd || ''}</div>
          </div>
          <div class="offer-actions">
            ${offer.buttonOfferUrlAd ? `<a href="${offer.buttonOfferUrlAd}" class="offer-primary-cta">${offer.buttonOfferTextAd}</a>` : ''}
            ${offer.buttonOfferUrlAd1 ? `<a href="${offer.buttonOfferUrlAd1}" class="offer-secondary-cta">${offer.buttonOfferTextAd1}</a>` : ''}
          </div>
        </div>
      </section>`;
  }

  createResortCard(resort) {
    const name = resort?.name ?? '';
    const slug = resort?.slug;
    const locationDisplay = [resort.city, resort.state, resort.country]
      .filter(part => part && part.trim() !== '')
      .join(', ');
    const img = resort.image ? this.getImageUrl(resort.image) : '';
    const collection = resort?.collection ?? null;

    // TODO: Revisit URL structure after dispatcher changes are done
    return `
      <article class="resort-card" aria-labelledby="title-${resort.universalPropertyCode}" data-href="/content/tmvcs/us/en/experiences/resorts/${slug}.html">
        <div class="resort-image">
          <img src="${img}" alt="" role="presentation" loading="lazy">
          ${collection ? `<div class="collection-badge"><i class="fa-solid fa-building" aria-hidden="true"></i>${collection}</div>` : ''}
        </div>
        <div class="resort-content">
          <div class="resort-info">
            <h3 class="resort-title" id="title-${resort.universalPropertyCode}">${name}</h3>
            ${locationDisplay ? `<p class="resort-location">${locationDisplay}</p>` : ''}
          </div>
          <div class="resort-actions">
            <a href="/content/tmvcs/us/en/experiences/resorts/${slug}.html" class="view-resort" aria-label="View details for ${name}">View Resort</a>
            ${this.renderTripAdvisorRating(resort) ?? ''}
          </div>
        </div>
      </article>`;
  }

  renderTripAdvisorRating(resort) {
      if (!resort.tripadvisorId || !resort.ratingImage || !resort.webUrl) return '';
      return `
        <a href="${resort.webUrl}" target="_blank" rel="noopener noreferrer" class="tripadvisor-rating third-party-link" aria-label="View ${resort.name} reviews on TripAdvisor">
          <img src="${resort.ratingImage}" alt="${resort.rating} rating" class="rating-image" />
        </a>`;
    }

  renderActiveFilterPills() {
    if (!this.activeFiltersContainers) return;
    const hasFilters = Object.values(this.activeFilters).some(a => a.length > 0) || this.searchQuery;

    this.activeFiltersContainers.forEach(container => {
      container.innerHTML = '';
      container.classList.toggle('has-pills', !!hasFilters);

      const createPill = (label, type, val) => {
        const pill = document.createElement('div');
        pill.className = 'filter-pill';
        pill.innerHTML = `
          <span class="pill-text">${label}</span>
          <button class="pill-remove" data-type="${type}" data-val="${val}" aria-label="Remove filter ${label}">
            <i class="fas fa-times" aria-hidden="true"></i>
          </button>`;

        pill.querySelector('button').addEventListener('click', () => {
          if (type === 'search') {
            this.searchInput.value = '';
            this.searchQuery = '';
          } else {
            this.activeFilters[type] = this.activeFilters[type].filter(v => v !== val);
            const cb = this.element.querySelector(`input[name="${type}"][value="${val}"]`);
            if (cb) cb.checked = false;
          }
          this.applyFilters();
        });
        container.appendChild(pill);
      };

      Object.keys(this.activeFilters).forEach(type => {
        this.activeFilters[type].forEach(val => {
          const label = this.filterOptionLabels[type][val] || val;
          createPill(label, type, val);
        });
      });
      if (this.searchQuery) createPill(`Search: ${this.searchQuery}`, 'search', '');
    });

    this.element.querySelector('.filter-header')?.classList.toggle('has-pills', !!hasFilters);
  }

  updateResultsCount() {
    if (!this.resultsCount) return;
    const showing = Math.min(this.currentPage * this.pageSize, this.filteredResorts.length);
    this.resultsCount.textContent = this.filteredResorts.length ? `1-${showing} of ${this.filteredResorts.length} Resorts` : `0 of ${this.allResorts.length} Resorts`;
  }

  loadMore() {
    this.currentPage++;
    this.renderResorts();
    this.updateResultsCount();
    this.announce(`Loaded more resorts. Now showing ${Math.min(this.currentPage * this.pageSize, this.filteredResorts.length)}.`);
  }

  clearFilters() {
    this.activeFilters = { region: [], vacationType: [], activities: [], brand: [] };
    this.element.querySelectorAll('input[type="checkbox"]').forEach(cb => cb.checked = false);
    if (this.searchInput) this.searchInput.value = '';
    this.searchQuery = '';
    this.applyFilters();
    this.announce('All filters cleared.');
  }

  getSafeHighlight(originalText, query) {
    if (!originalText || !query) return originalText;
    const escapedQuery = query.trim().replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
    const ignoredChars = "[\\.\\-'’ʻ‘“”\\s,]*";
    const fuzzyPattern = escapedQuery.split('').join(ignoredChars);
    try {
      const regex = new RegExp(`(${fuzzyPattern})`, 'gi');
      return originalText.replace(regex, '<strong>$1</strong>');
    } catch (e) {
      return originalText;
    }
  }

  updateSearchSuggestions() {
    const query = this.searchQuery.trim();
    if (!this.searchSuggestions) return;
  
    if (this.isLoading) return;

    if (!query) {
      const recentlyViewed = this.getRecentlyViewedResorts();
      const recentlyViewedDestinations = this.getRecentlyViewedDestinations();
      const hasHistory = recentlyViewed.length > 0 || recentlyViewedDestinations.length > 0;

      if (hasHistory) {
        this.displaySearchSuggestions([], [], [], [...recentlyViewed, ...recentlyViewedDestinations]);
      } else {
        const featuredResorts = this.allResorts.filter(r => r.featured === "Featured").slice(0, 3);
        this.displaySearchSuggestions([], featuredResorts, []);
      }
      this.searchSuggestions.classList.add('active');
      return;
    }

    const normalizedQuery = this.normalizeText(query);
    const matchingResorts = this.allResorts.filter(resort => {
      const searchable = this.normalizeText(`${resort.name} ${resort.city} ${resort.state} ${resort.country} ${resort.region} ${resort.dcmBrand}`);
      return searchable.includes(normalizedQuery);
    }).slice(0, 3);
    
    const matchingDestinations = [];
    if (this.destinations && this.destinations.length > 0) {
      this.destinations.forEach(dest => {
        const destName = dest.name || '';
        if (this.normalizeText(destName).includes(normalizedQuery)) {
          matchingDestinations.push(dest);
        }
      });
    }
    const uniqueLocations = matchingDestinations.slice(0, 2).map(dest => dest.name);

    if (matchingResorts.length === 0 && uniqueLocations.length === 0) {
      if (query.length >= 3) {
        let html = `<div class="search-no-results" role="status"><div class="no-results-text">No results</div></div>`;
        const recentlyViewed = this.getRecentlyViewedResorts();
        const recentlyViewedDestinations = this.getRecentlyViewedDestinations();

        if (recentlyViewed.length === 0 && recentlyViewedDestinations.length === 0) {
          const featured = this.allResorts.filter(r => r.featured === "Featured").slice(0, 3);
          if (featured.length > 0) {
            html += `<div class="search-suggestion-section-header">Featured Resorts</div>`;
            featured.forEach(resort => {
              html += this.renderSuggestionItem(resort, 'resort', query);
            });
          }

          const vacationTypes = this.filterOptionLabels?.vacationType ? Object.entries(this.filterOptionLabels.vacationType) : [];
          if (vacationTypes.length > 0) {
            html += this.renderVacationTypeSuggestions(vacationTypes.map(([key, value]) => ({ key, value })), query);
          }
        } else {
          html += this.renderRecentHistorySection(recentlyViewed, recentlyViewedDestinations, query);
        }
        this.searchSuggestions.innerHTML = html;
        this.attachSuggestionEvents();
        this.searchSuggestions.classList.add('active');
      } else {
        this.hideSearchSuggestions();
      }
      return;
    }

    this.displaySearchSuggestions(matchingResorts, [], uniqueLocations);
    this.searchSuggestions.classList.add('active');
  }

  renderSuggestionItem(item, type, query) {
    const isResort = type === 'resort';
    const val = isResort ? item.marshaCode : item;
    const primaryText = isResort ? item.name : item.split(',')[0];
    const secondaryText = isResort ? `${item.city}${item.state ? `, ${item.state}` : ""}${item.country ? `, ${item.country}` : ""}` : (type === 'location' ? item : item);
    const img = isResort && item.image ? this.getImageUrl(item.image) : '';

    return `
      <div class="search-suggestion-item" role="option" tabindex="0" data-type="${type}" data-value="${val}">
        ${isResort ? `<div class="suggestion-thumbnail"><img src="${img}" alt="" onerror="this.style.display='none'"/></div>` : `<div class="suggestion-icon"><i class="fas fa-map-marker-alt" aria-hidden="true"></i></div>`}
        <div class="suggestion-content">
          <div class="suggestion-text-primary">${this.getSafeHighlight(primaryText, query)}</div>
          <div class="suggestion-text-secondary">${this.getSafeHighlight(secondaryText, query)}</div>
        </div>
      </div>`;
  }

  renderVacationTypeSuggestions(matches, query) {
    let html = `<div class="vacation-type-container"><div class="vacation-type-header">Browse by Vacation Type</div><div class="vacation-type-list">`;
    matches.forEach(vt => {
      let icon = "umbrella-beach";
      const val = vt.value.toLowerCase();
      if (val.includes("mountain") || val.includes("ski")) icon = "person-skiing";
      else if (val.includes("island")) icon = "tree-palm";
      else if (val.includes("city") || val.includes("urban")) icon = "tree-city";

      html += `
        <div class="search-suggestion-item-vacation-type" role="option" tabindex="0" data-type="vacationType" data-value="${vt.key}">
          <div class="vacation-type-content">
            <i class="fas fa-${icon}"></i>
            <div class="vacation-type-name">${this.getSafeHighlight(vt.value, query)}</div>
          </div>
          <i class="fas fa-arrow-right"></i>
        </div>`;
    });
    html += `</div></div>`;
    return html;
  }

  renderRecentHistorySection(resorts, destinations, query) {
    const allRecent = [];
    const rTimestamps = this.getStoredTimestamps('recentlyViewedResorts');
    const dTimestamps = this.getStoredTimestamps('recentlyViewedDestinations');

    resorts.forEach(item => allRecent.push({ item, type: 'resort', ts: rTimestamps[item.marshaCode] || rTimestamps[item.slug] || 0 }));
    destinations.forEach(item => allRecent.push({ item, type: 'location', ts: dTimestamps[item] || 0 }));
    allRecent.sort((a, b) => b.ts - a.ts);

    let html = `<div class="search-suggestion-section-header">Recently Viewed</div>`;
    allRecent.slice(0, 4).forEach(({ item, type }) => {
      html += this.renderSuggestionItem(item, type, query);
    });
    return html;
  }

  displaySearchSuggestions(matchingResorts, featuredResorts, uniqueLocations, recentlyViewed = null) {
    const query = this.searchQuery.trim();
    let html = '';

    if (recentlyViewed && recentlyViewed.length > 0) {
      html += this.renderRecentHistorySection(this.getRecentlyViewedResorts(), this.getRecentlyViewedDestinations(), query);
    }

    if (featuredResorts.length > 0) {
      html += `<div class="search-suggestion-section-header">Featured Resorts</div>`;
      featuredResorts.forEach(r => html += this.renderSuggestionItem(r, 'resort', query));
    }

    const isInitialState = !query && (!recentlyViewed || recentlyViewed.length === 0);
    const isNoResultsState = query.length >= 3 && matchingResorts.length === 0 && uniqueLocations.length === 0;

    if (isInitialState || isNoResultsState) {
      const vtMatches = this.filterOptionLabels?.vacationType 
        ? Object.entries(this.filterOptionLabels.vacationType).map(([key, value]) => ({ key, value })) 
        : [];
        
      if (vtMatches.length > 0) {
        html += this.renderVacationTypeSuggestions(vtMatches, query);
      }
    }

    if (uniqueLocations.length > 0) {
      uniqueLocations.forEach(loc => html += this.renderSuggestionItem(loc, 'location', query));
    }

    if (matchingResorts.length > 0) {
      matchingResorts.forEach(r => html += this.renderSuggestionItem(r, 'resort', query));
    }

    // Only show 'View All Results' when there are matching results
    if (matchingResorts.length > 0 || uniqueLocations.length > 0) {
      html += `<div class="search-suggestions-view-all" tabindex="0" role="button">View All Results</div>`;
    }   
    this.searchSuggestions.innerHTML = html;
    this.attachSuggestionEvents();
  }

  attachSuggestionEvents() {
    const handleSuggestionClick = (item) => {
      const { type, value: val } = item.dataset;
      if (type === 'location') {
        const destination = this.destinations ? this.destinations.find(dest => dest.name === val) : null;
        
        if (destination) {
          let url = `/content/tmvcs/us/en/destinations/${val.toLowerCase().replace(/[^a-z0-9]+/g, '-')}.html`;
          
          if (destination.structuredContent && Array.isArray(destination.structuredContent)) {
            const slugEntry = destination.structuredContent.find(item => item.startsWith('slug='));
            if (slugEntry) {
              const slug = slugEntry.replace('slug=', '');
              url = `/content/tmvcs/us/en${slug}.html`;
            }
          }
          
          window.location.href = url;
          return;
        } else {
          this.searchInput.value = val;
          this.searchQuery = val;
        }
      } else if (type === 'vacationType') {
        if (!this.activeFilters.vacationType.includes(val)) {
          this.activeFilters.vacationType.push(val);
          const cb = this.element.querySelector(`input[name="vacationType"][value="${val}"]`);
          if (cb) cb.checked = true;
        }
        this.searchInput.value = '';
        this.searchQuery = '';
        this.hideSearchSuggestions();
        this.applyFilters();
        return;
      } else {
        const resort = this.allResorts.find(r => r.marshaCode === val);
        if (resort) {
          const slug = resort.slug || resort.marshaCode.toLowerCase();
          window.location.href = `/content/tmvcs/us/en/experiences/resorts/${slug}.html`;
          return;
        }
      }
      this.hideSearchSuggestions();
      this.applyFilters();
    };

    this.searchSuggestions.querySelectorAll('.search-suggestion-item, .search-suggestion-item-vacation-type').forEach(item => {
      item.addEventListener('click', () => handleSuggestionClick(item));
      item.addEventListener('keydown', (e) => { if (e.key === 'Enter') handleSuggestionClick(item); });
    });

    const viewAllBtn = this.searchSuggestions.querySelector('.search-suggestions-view-all');
    viewAllBtn?.addEventListener('click', () => { this.hideSearchSuggestions(); this.applyFilters(); });
    viewAllBtn?.addEventListener('keydown', (e) => { if (e.key === 'Enter') { this.hideSearchSuggestions(); this.applyFilters(); } });
  }

  hideSearchSuggestions() {
    if (this.searchSuggestions) {
      this.searchSuggestions.classList.remove('active');
      this.searchSuggestions.innerHTML = '';
    }
  }

  debounce(func, wait) {
    let timeout;
    return (...args) => {
      clearTimeout(timeout);
      timeout = setTimeout(() => func.apply(this, args), wait);
    };
  }

  getRecentlyViewedResorts() {
    try {
      const stored = localStorage.getItem('recentlyViewedResorts');
      if (!stored) return [];
      const resortIds = JSON.parse(stored);
      return resortIds.map(id => this.allResorts.find(r => r.marshaCode === id || r.slug === id || (r.marshaCode && r.marshaCode.toLowerCase() === id.toLowerCase()))).filter(Boolean).slice(0, this.maxRecentlyViewed);
    } catch (e) {
      return [];
    }
  }

 

  getStoredTimestamps(key) {
    try {
      return JSON.parse(localStorage.getItem(key + 'Timestamps') || '{}');
    } catch (e) {
      return {};
    }
  }

  getRecentlyViewedDestinations() {
    try {
      return JSON.parse(localStorage.getItem('recentlyViewedDestinations') || '[]').slice(0, this.maxRecentlyViewed);
    } catch (e) {
      return [];
    }
  }
}

// Auto-init
if (typeof document !== 'undefined') {
  const init = () => {
    document.querySelectorAll('[data-mod="resort-list"]').forEach(el => {
      if (!el._resortListInstance) el._resortListInstance = new ResortList(el);
    });
  };
  document.readyState === 'loading' ? document.addEventListener('DOMContentLoaded', init) : init();
}

export default ResortList;
