class ResortDestinationDetailsCard {
  constructor(element) {
    this.element = element;
    this.resortGrid = this.element.querySelector('.resort-grid');
    this.resultsCount = this.element.querySelector('.results-count');
    this.loadMoreBtn = this.element.querySelector('.load-more-btn');
    this.loadMoreContainer = this.element.querySelector('.load-more-container');
    this.announcer = this.element.querySelector('.sr-announcer');
    this.hiddenCards = this.element.querySelectorAll('.hidden-card');
    
    this.init();
  }

  init() {
    this.setupEventListeners();
    this.handleOfferCardLayout();
  }

  setupEventListeners() {
    // View All Resorts button click
    if (this.loadMoreBtn) {
      this.loadMoreBtn.addEventListener('click', () => {
        this.showAllResorts();
      });
    }

    // Resort card click navigation
    const resortCards = this.element.querySelectorAll('.resort-card');
    resortCards.forEach(card => {
      card.addEventListener('click', (e) => {
        // Don't navigate if clicking on TripAdvisor link or View Resort button
        if (!e.target.closest('.tripadvisor-rating') && !e.target.closest('.view-resort')) {
          const href = card.getAttribute('data-href');
          if (href) {
            window.location.href = href;
          }
        }
      });
    });
  }

  handleOfferCardLayout() {
    // Get visible resort cards count (not hidden)
    const visibleResortCards = this.element.querySelectorAll('.resort-card:not(.hidden-card):not([style*="display: none"])');
    const offerCard = this.element.querySelector('.offer-card');
    
    if (offerCard && visibleResortCards.length < 4) {
      // When fewer than 4 visible resort cards display offer card as vertical card (mobile-style)
      offerCard.classList.add('offer-card-vertical');
    } else if (offerCard) {
      // Otherwise display as horizontal full-width card
      offerCard.classList.remove('offer-card-vertical');
    }
  }

  showAllResorts() {
    // Show all hidden cards
    this.hiddenCards.forEach(card => {
      card.style.display = 'block';
    });

    // Update results count
    if (this.resultsCount) {
      const totalCards = this.element.querySelectorAll('.resort-card').length;
      this.resultsCount.textContent = `${totalCards} Resorts`;
    }

    // Hide load more button
    if (this.loadMoreContainer) {
      this.loadMoreContainer.style.display = 'none';
    }

    // Announce to screen readers
    this.announce('Showing all resorts');
  }

  announce(message) {
    if (this.announcer) {
      this.announcer.textContent = message;
    }
  }

 //for aem to integrate - commenting for now
  // updateResultsCount(displayedCount, totalCount) {
  //   if (this.resultsCount) {
  //     if (displayedCount === totalCount) {
  //       this.resultsCount.textContent = `${totalCount} Resort${totalCount !== 1 ? 's' : ''}`;
  //     } else {
  //       this.resultsCount.textContent = `${displayedCount} of ${totalCount} Resort${totalCount !== 1 ? 's' : ''}`;
  //     }
  //   }
  // }

  // toggleLoadMoreButton(show) {
  //   if (this.loadMoreContainer) {
  //     this.loadMoreContainer.style.display = show ? 'flex' : 'none';
  //   }
  // }

  // updateOfferCardLayout(visibleResortCount) {
  //   const offerCard = this.element.querySelector('.offer-card');
    
  //   if (offerCard) {
  //     if (visibleResortCount < 4) {
  //       offerCard.classList.add('offer-card-vertical');
  //     } else {
  //       offerCard.classList.remove('offer-card-vertical');
  //     }
  //   }
  // }
}

// Auto-initialize on DOM ready
if (typeof document !== 'undefined') {
  const init = () => {
    const elements = document.querySelectorAll('[data-mod="resort-destination-details-card"]');
    elements.forEach(el => {
      if (!el._resortDestinationDetailsCardInstance) {
        el._resortDestinationDetailsCardInstance = new ResortDestinationDetailsCard(el);
      }
    });
  };

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', init);
  } else {
    init();
  }
}

if (typeof window.registerComponent === 'function') {
  window.registerComponent('resort-destination-details-card', ResortDestinationDetailsCard);
}

export default ResortDestinationDetailsCard;
