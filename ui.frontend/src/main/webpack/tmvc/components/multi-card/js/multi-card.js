import { registerComponent, Component } from '../.././../../site/js/mvw.js';

class Multicard extends Component {
    init() {
        this.cards = this.element.querySelectorAll('.vacation-cards .card');
        this.bullets = this.element.querySelector('#carouselBullets');
        this.cardsContainer = this.element.querySelector('.vacation-cards');
        this.isMobile = window.innerWidth <= 768;
        this.stacked = this.element.dataset.stacked === 'true';
        this.isFourCardCarousel = this.element.classList.contains('four-card-carousel');
        this.peekPercent = 0.05;
        this.programmaticScroll = false;
        this.currentIndex = 0;
        this.visibleCards = this.getVisibleCardsCount();
        
        // For four-card-carousel, stacked mode is disabled
        if (this.isFourCardCarousel) {
            this.stacked = false;
        }
        
        this.initCarousel();
    }

    getVisibleCardsCount() {
        if (this.isMobile) return 1;
        if (this.isFourCardCarousel) {
            if (window.innerWidth <= 1024) return 3;
            return 4;
        }
        return this.cards.length;
    }

    initCarousel() {
        if (!this.cards.length) {
            console.warn('No cards found for carousel');
            return;
        }

        // Setup equal heights for desktop
        this.setupEqualHeights();

        if (this.isFourCardCarousel) {
            this.initFourCardCarousel();
        } else if (this.isMobile && !this.stacked) {
            this.setupCarousel();
            this.setupBullets();
            this.setupEventListeners();
        } else {
            if (this.bullets) {
                this.bullets.style.display = 'none';
            }
            
            if (this.isMobile && this.stacked) {
                this.setupStackedLayout();
            }
        }

        requestAnimationFrame(() => {
            if (!this.isFourCardCarousel) {
                this.scrollToCard(this.currentIndex, false);
            }
            this.setActiveCard(this.currentIndex);
        });
    }

    initFourCardCarousel() {
        const totalCards = this.cards.length;
        const needsDesktopCarousel = !this.isMobile && totalCards > this.visibleCards;
        const needsMobileCarousel = this.isMobile && totalCards > 1;

        // Setup nav buttons
        this.setupFourCardNavButtons();

        if (this.isMobile) {
            this.setupMobileCarouselForFourCard();
            if (needsMobileCarousel) {
                this.showNavButtons();
            } else {
                this.hideNavButtons();
            }
        } else {
            this.setupDesktopCarouselForFourCard();
            if (needsDesktopCarousel) {
                this.showNavButtons();
            } else {
                this.hideNavButtons();
            }
        }

        this.updateNavButtonStates();
        this.setupFourCardEventListeners();
    }

    setupFourCardNavButtons() {
        // Desktop nav buttons (in header)
        this.leftButton = this.element.querySelector('.four-card-carousel .nav-icon-left');
        this.rightButton = this.element.querySelector('.four-card-carousel .nav-icon-right');

        // Create mobile nav buttons if they don't exist
        let mobileNavContainer = this.element.querySelector('.nav-icons-mobile');
        if (!mobileNavContainer && this.isMobile) {
            mobileNavContainer = document.createElement('div');
            mobileNavContainer.className = 'nav-icons-mobile';
            mobileNavContainer.innerHTML = `
                <button class="nav-icon-left" type="button" aria-label="Previous slide">
                    <i class="prev-icon"></i>
                </button>
                <button class="nav-icon-right" type="button" aria-label="Next slide">
                    <i class="next-icon"></i>
                </button>
            `;
            
            // Insert after vacation-cards
            if (this.cardsContainer && this.cardsContainer.parentNode) {
                this.cardsContainer.parentNode.insertBefore(mobileNavContainer, this.cardsContainer.nextSibling);
            }
        }

        // Update button references for mobile
        if (this.isMobile && mobileNavContainer) {
            this.leftButton = mobileNavContainer.querySelector('.nav-icon-left');
            this.rightButton = mobileNavContainer.querySelector('.nav-icon-right');
        }
    }

    setupDesktopCarouselForFourCard() {
        if (!this.cardsContainer) return;

        // Reset any mobile styles
        this.cardsContainer.style.display = '';
        this.cardsContainer.style.overflowX = '';
        this.cardsContainer.style.scrollSnapType = '';
        this.cardsContainer.style.padding = '';

        // Hide cards beyond the visible count
        this.updateVisibleCards();
    }

    setupMobileCarouselForFourCard() {
        if (!this.cardsContainer) return;

        this.cardsContainer.style.display = 'flex';
        this.cardsContainer.style.overflowX = 'auto';
        this.cardsContainer.style.scrollSnapType = 'x mandatory';
        this.cardsContainer.style.scrollBehavior = 'smooth';
        this.cardsContainer.style.webkitOverflowScrolling = 'touch';

        this.cards.forEach((card) => {
            card.style.flex = '0 0 90%';
            card.style.scrollSnapAlign = 'center';
            card.style.display = '';
            card.style.opacity = '';
            card.style.margin = '0px 8px';
        });
    }

    updateVisibleCards() {
        if (this.isMobile) return;

        const startIndex = this.currentIndex;
        const endIndex = startIndex + this.visibleCards;

        this.cards.forEach((card, index) => {
            if (index >= startIndex && index < endIndex) {
                card.style.display = '';
                card.style.opacity = '1';
            } else {
                card.style.display = 'none';
                card.style.opacity = '0';
            }
        });
    }

    showNavButtons() {
        if (this.leftButton) this.leftButton.style.display = '';
        if (this.rightButton) this.rightButton.style.display = '';
        
        const navIconsContainer = this.element.querySelector('.four-card-carousel .nav-icons');
        if (navIconsContainer && !this.isMobile) {
            navIconsContainer.style.display = '';
        }

        const mobileNavContainer = this.element.querySelector('.nav-icons-mobile');
        if (mobileNavContainer && this.isMobile) {
            mobileNavContainer.style.display = '';
        }
    }

    hideNavButtons() {
        const navIconsContainer = this.element.querySelector('.four-card-carousel .nav-icons');
        if (navIconsContainer && !this.isMobile) {
            navIconsContainer.style.display = 'none';
        }

        const mobileNavContainer = this.element.querySelector('.nav-icons-mobile');
        if (mobileNavContainer && this.isMobile) {
            mobileNavContainer.style.display = 'none';
        }
    }

    updateNavButtonStates() {
        if (!this.leftButton || !this.rightButton) return;

        const totalCards = this.cards.length;

        if (this.isMobile) {
            // Mobile: disable at boundaries
            this.leftButton.disabled = this.currentIndex === 0;
            this.rightButton.disabled = this.currentIndex >= totalCards - 1;
        } else {
            // Desktop: disable based on visible range
            this.leftButton.disabled = this.currentIndex === 0;
            this.rightButton.disabled = this.currentIndex + this.visibleCards >= totalCards;
        }
    }

    setupFourCardEventListeners() {
        if (this.leftButton) {
            this.leftButton.addEventListener('click', (e) => {
                e.preventDefault();
                this.fourCardPrev();
            });
        }

        if (this.rightButton) {
            this.rightButton.addEventListener('click', (e) => {
                e.preventDefault();
                this.fourCardNext();
            });
        }

        // Mobile scroll listener
        if (this.isMobile && this.cardsContainer) {
            this.cardsContainer.addEventListener('scroll', () => {
                this.updateFourCardMobileIndex();
            });
        }

        // Touch events for mobile swipe
        if (this.isMobile) {
            this.setupTouchEventsForFourCard();
        }

        // Resize handler
        this.boundHandleResize = this.handleResize.bind(this);
        window.addEventListener('resize', this.boundHandleResize);
    }

    setupTouchEventsForFourCard() {
        if (!this.cardsContainer) return;

        let startX = 0;
        let startY = 0;

        this.cardsContainer.addEventListener('touchstart', (e) => {
            startX = e.touches[0].clientX;
            startY = e.touches[0].clientY;
        }, { passive: true });

        this.cardsContainer.addEventListener('touchend', (e) => {
            const endX = e.changedTouches[0].clientX;
            const endY = e.changedTouches[0].clientY;
            
            const diffX = startX - endX;
            const diffY = Math.abs(startY - endY);
            
            if (Math.abs(diffX) > 50 && diffY < 100) {
                if (diffX > 0) {
                    this.fourCardNext();
                } else {
                    this.fourCardPrev();
                }
            }
        }, { passive: true });
    }

    fourCardNext() {
        const totalCards = this.cards.length;

        if (this.isMobile) {
            if (this.currentIndex < totalCards - 1) {
                this.currentIndex++;
                this.scrollToCardForFourCard(this.currentIndex);
            }
        } else {
            if (this.currentIndex + this.visibleCards < totalCards) {
                this.currentIndex++;
                this.updateVisibleCards();
            }
        }

        this.updateNavButtonStates();
    }

    fourCardPrev() {
        if (this.currentIndex > 0) {
            this.currentIndex--;

            if (this.isMobile) {
                this.scrollToCardForFourCard(this.currentIndex);
            } else {
                this.updateVisibleCards();
            }
        }

        this.updateNavButtonStates();
    }

    scrollToCardForFourCard(index) {
        if (!this.cardsContainer || !this.cards[index]) return;

        const card = this.cards[index];
        const containerWidth = this.cardsContainer.clientWidth;
        const cardWidth = card.offsetWidth;
        let scrollLeft = card.offsetLeft - this.cardsContainer.offsetLeft - (containerWidth - cardWidth) / 2;

        const maxScroll = this.cardsContainer.scrollWidth - containerWidth;
        scrollLeft = Math.max(0, Math.min(scrollLeft, maxScroll));

        this.cardsContainer.scrollTo({ left: scrollLeft, behavior: 'smooth' });
    }

    updateFourCardMobileIndex() {
        if (!this.cardsContainer) return;

        const scrollLeft = this.cardsContainer.scrollLeft;
        let newIndex = 0;
        let minDistance = Infinity;

        this.cards.forEach((card, index) => {
            const cardLeft = card.offsetLeft - this.cardsContainer.offsetLeft;
            const distance = Math.abs(cardLeft - scrollLeft);

            if (distance < minDistance) {
                minDistance = distance;
                newIndex = index;
            }
        });

        if (newIndex !== this.currentIndex) {
            this.currentIndex = newIndex;
            this.updateNavButtonStates();
        }
    }

    setupCarousel() {
        // Only set up carousel styles if on mobile and not stacked
        if (!this.isMobile || this.stacked) return;

        if (this.cardsContainer) {
            this.cardsContainer.style.display = 'flex';
            this.cardsContainer.style.overflowX = 'auto';
            this.cardsContainer.style.scrollSnapType = 'x mandatory';
            this.cardsContainer.style.scrollBehavior = 'smooth';
            this.cardsContainer.style.webkitOverflowScrolling = 'touch';
            const peekPadding = `${this.peekPercent * 100}%`;
            this.cardsContainer.style.padding = `0 ${peekPadding}`;
            this.cardsContainer.style.scrollPadding = `0 ${peekPadding}`;
            this.cardsContainer.style.gap = '0';
            
            // Hide scrollbars for cleaner look
            this.cardsContainer.style.scrollbarWidth = 'none';
            this.cardsContainer.style.msOverflowStyle = 'none';
        }

        const cardWidthPercent = 100 - this.peekPercent * 200;
        // Set up cards for horizontal layout
        this.cards.forEach((card) => {
            card.style.flex = `0 0 ${cardWidthPercent}%`;
            card.style.scrollSnapAlign = 'center';
            card.style.scrollSnapStop = 'always';
            card.style.margin = '0px 8px';
        });

        // Scroll to first card
        requestAnimationFrame(() => {
            this.scrollToCard(this.currentIndex, false);
            this.setActiveCard(this.currentIndex);
        });
    }

    setupStackedLayout() {
        // Only setup stacked layout if on mobile AND stacked mode is enabled
        if (!this.isMobile || !this.stacked) return;

        if (this.cardsContainer) {
            this.cardsContainer.style.display = 'block';
            this.cardsContainer.style.overflowX = 'visible';
            this.cardsContainer.style.scrollSnapType = 'none';
        }

        // Reset card styles for stacked layout
        this.cards.forEach((card) => {
            card.style.flex = '';
            card.style.scrollSnapAlign = '';
            card.style.scrollSnapStop = '';
            card.style.marginBottom = '1rem'; // Add spacing between stacked cards
        });
    }

    setupBullets() {
        // Only setup bullets if on mobile AND not stacked
        if (!this.isMobile || this.stacked || !this.bullets) return;

        this.bullets.style.display = 'flex';
        this.bullets.innerHTML = '';
        
        this.cards.forEach((_, index) => {
            const bullet = document.createElement('button');
            bullet.type = 'button';
            bullet.className = `bullet ${index === this.currentIndex ? 'active' : ''}`;
            bullet.setAttribute('aria-label', `Go to slide ${index + 1}`);
            bullet.setAttribute('data-index', index);
            
            bullet.addEventListener('click', () => {
                this.goToSlide(index);
            });

            this.bullets.appendChild(bullet);
        });
        this.updateBullets();
    }

    setupEventListeners() {
        // Only setup event listeners if on mobile and not stacked
        if (!this.isMobile || this.stacked) return;

        // Update bullets on scroll
        if (this.cardsContainer) {
            this.cardsContainer.addEventListener('scroll', () => {
                this.updateActiveBullet();
            });
        }

        // Touch swipe detection
        this.setupTouchEvents();

        // Handle window resize
        window.addEventListener('resize', this.handleResize.bind(this));
    }

    handleResize() {
        const wasMobile = this.isMobile;
        this.isMobile = window.innerWidth <= 768;
        this.visibleCards = this.getVisibleCardsCount();

        // For four-card-carousel, handle resize differently
        if (this.isFourCardCarousel) {
            if (wasMobile !== this.isMobile) {
                this.currentIndex = 0;
                this.destroy();
                this.initCarousel();
            } else if (!this.isMobile) {
                // On desktop, update visible cards count and re-render
                this.updateVisibleCards();
                this.updateNavButtonStates();
            }
            this.resizeElementHeights();
            return;
        }

        this.currentIndex = this.getInitialIndex();

        // If mobile state changed, reinitialize
        if (wasMobile !== this.isMobile) {
            this.destroy();
            this.initCarousel();
        } else {
            // If still on same breakpoint, just update equal heights
            this.resizeElementHeights();
        }
    }

    setupTouchEvents() {
        // Only setup touch events if on mobile and not stacked
        if (!this.isMobile || this.stacked || !this.cardsContainer) return;

        let startX = 0;
        let startY = 0;

        this.cardsContainer.addEventListener('touchstart', (e) => {
            startX = e.touches[0].clientX;
            startY = e.touches[0].clientY;
        }, { passive: true });

        this.cardsContainer.addEventListener('touchend', (e) => {
            if (!this.cardsContainer) return;

            const endX = e.changedTouches[0].clientX;
            const endY = e.changedTouches[0].clientY;
            
            const diffX = startX - endX;
            const diffY = Math.abs(startY - endY);
            
            // Only handle horizontal swipes with minimal vertical movement
            if (Math.abs(diffX) > 50 && diffY < 100) {
                if (diffX > 0) {
                    this.nextSlide();
                } else {
                    this.previousSlide();
                }
            }
        }, { passive: true });
    }

    goToSlide(index) {
        if (!this.isMobile || this.stacked || index < 0 || index >= this.cards.length) return;
        this.currentIndex = index;
        this.scrollToCard(index);
        this.updateBullets();
        this.setActiveCard(index);
    }

    scrollToCard(index, smooth = true) {
        if (!this.isMobile || this.stacked || !this.cardsContainer || !this.cards[index]) return;

        const card = this.cards[index];
        const containerWidth = this.cardsContainer.clientWidth;
        const cardWidth = card.offsetWidth;
        let scrollLeft =
            card.offsetLeft - this.cardsContainer.offsetLeft - (containerWidth - cardWidth) / 2;

        const maxScroll = this.cardsContainer.scrollWidth - containerWidth;
        scrollLeft = Math.max(0, Math.min(scrollLeft, maxScroll));

        this.programmaticScroll = true;
        this.cardsContainer.scrollTo({ left: scrollLeft, behavior: smooth ? 'smooth' : 'auto' });
        clearTimeout(this.scrollResetTimer);
        this.scrollResetTimer = setTimeout(() => {
            this.programmaticScroll = false;
        }, smooth ? 400 : 0);
        this.setActiveCard(index);
    }

    nextSlide() {
        if (!this.isMobile || this.stacked) return;
        const nextIndex = (this.currentIndex + 1) % this.cards.length;
        this.goToSlide(nextIndex);
    }

    previousSlide() {
        if (!this.isMobile || this.stacked) return;
        const prevIndex = (this.currentIndex - 1 + this.cards.length) % this.cards.length;
        this.goToSlide(prevIndex);
    }

    updateActiveBullet() {
        if (!this.isMobile || this.stacked || !this.cardsContainer || !this.bullets) return;

        const scrollLeft = this.cardsContainer.scrollLeft;
        
        // Find which card is currently in view
        let newIndex = 0;
        let minDistance = Infinity;
        
        this.cards.forEach((card, index) => {
            const cardLeft = card.offsetLeft - this.cardsContainer.offsetLeft;
            const distance = Math.abs(cardLeft - scrollLeft);
            
            if (distance < minDistance) {
                minDistance = distance;
                newIndex = index;
            }
        });

        if (newIndex !== this.currentIndex) {
            this.currentIndex = newIndex;
            this.updateBullets();
            this.setActiveCard(newIndex);
        }
    }

    setActiveCard(index) {
        this.cards.forEach((card, idx) => {
            const isActive = idx === index;
            card.classList.toggle('is-active', isActive);
            card.setAttribute('aria-hidden', isActive ? 'false' : 'true');
            card.tabIndex = isActive ? 0 : -1;
        });
    }

    updateBullets() {
        if (!this.isMobile || this.stacked || !this.bullets) return;

        const bullets = this.bullets.querySelectorAll('.bullet');
        bullets.forEach((bullet, index) => {
            const isActive = index === this.currentIndex;
            bullet.classList.toggle('active', isActive);
            bullet.setAttribute('aria-current', isActive ? 'true' : 'false');
        });
    }

    // Equal height methods from CampaignCards component
    setupEqualHeights() {
        this.resizeElementHeights();
        const processChange = this.debounce(() => this.resizeElementHeights());
        window.addEventListener('resize', (e) => { processChange() });
    }

    resizeElementHeights() {
        // Only set equal heights on desktop (non-mobile) or if stacked on mobile
        if (!this.isMobile || (this.isMobile && this.stacked)) {
            this.resetElementHeight(this.cards, ['.card-content', '.title', '.description']);
            this.setEqualHeight(this.cards, '.title');
            this.setEqualHeight(this.cards, '.description');
        } else {
            // Reset heights on mobile carousel
            this.resetElementHeight(this.cards, ['.card-content', '.title', '.description']);
        }
    }

    debounce(func, timeout = 300) {
        let timer;
        return (...args) => {
            clearTimeout(timer);
            timer = setTimeout(() => { func.apply(this, args); }, timeout);
        };
    }

    resetElementHeight(selector, elements) {
        selector.forEach((card) => {
            for (var i = 0; i < elements.length; i++) {
                const element = card.querySelector(elements[i]);
                if (element) {
                    element.style.height = 'auto';
                }
            }
        });
    }

    setEqualHeight(selector, elementClass) {
        var max_height = 0;
        
        // First pass: find the maximum height
        selector.forEach((card) => {
            const element = card.querySelector(elementClass);
            if (element) {
                // Get the actual height including padding
                const element_height = element.offsetHeight;
                if (element_height > max_height) {
                    max_height = element_height;
                }
            }
        });
        
        // Second pass: apply the maximum height
        selector.forEach((card) => {
            const element = card.querySelector(elementClass);
            if (element) {
                element.style.height = max_height + 'px';
            }
        });
    }

    getInitialIndex() {
        if (!this.cards || !this.cards.length) {
            return 0;
        }
        if (this.isMobile && !this.stacked && this.cards.length > 1) {
            return Math.max(Math.ceil(this.cards.length / 2) - 1, 0);
        }
        return 0;
    }

    destroy() {
        // Remove event listeners
        if (this.cardsContainer) {
            this.cardsContainer.style.padding = '';
            this.cardsContainer.style.scrollPadding = '';
            this.cardsContainer.style.gap = '';
            this.cardsContainer.style.display = '';
            this.cardsContainer.style.overflowX = '';
            this.cardsContainer.style.scrollSnapType = '';
            this.cardsContainer.style.scrollBehavior = '';
        }

        // Reset card styles
        this.cards.forEach(card => {
            card.style.flex = '';
            card.style.scrollSnapAlign = '';
            card.style.scrollSnapStop = '';
            card.style.marginBottom = '';
            card.style.display = '';
            card.style.opacity = '';
        });

        // Reset heights when destroying
        this.resetElementHeight(this.cards, ['.card-content', '.title', '.description']);

        // Clear bullets
        if (this.bullets) {
            this.bullets.innerHTML = '';
            this.bullets.style.display = 'none';
        }

        // Remove mobile nav container for four-card-carousel
        if (this.isFourCardCarousel) {
            const mobileNavContainer = this.element.querySelector('.nav-icons-mobile');
            if (mobileNavContainer) {
                mobileNavContainer.remove();
            }
        }

        // Remove resize listener
        if (this.boundHandleResize) {
            window.removeEventListener('resize', this.boundHandleResize);
        }
        window.removeEventListener('resize', this.handleResize);
        clearTimeout(this.scrollResetTimer);
    }
}

registerComponent('multicard', Multicard);
export default Multicard;