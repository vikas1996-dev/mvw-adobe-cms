class EditorialCarouselDetails {
    constructor(element) {
      this.element = element;
      this.currentIndex = 0;
      this.isAnimating = false;
      this.config = {
        loop: this.element.dataset.loop === 'true',
        animationDuration: 300,
        cardGapDesktop: 24,
        cardGapMobile: 10,
        mobileBreakpoint: 768,
      };
      
      this.init();
    }
  
    init() {
      this.slidesContainer = this.element.querySelector('.editorial-slides');
      this.slideElements = Array.from(this.element.querySelectorAll('.editorial-card'));
      this.prevBtn = this.element.querySelector('.nav-icon-left');
      this.nextBtn = this.element.querySelector('.nav-icon-right');
      this.currentSlideElement = this.element.querySelector('.current-slide');
      this.totalSlides = this.slideElements.length;
  
      if (this.slideElements.length === 0) {
        console.warn('No slides found in carousel');
        return;
      }

      const cardCount = this.element.getAttribute('data-card-count');
      const slideCount = this.slideElements.length;
      
      if (cardCount === '1' || slideCount === 1) {
        return;
      }
      
      if (cardCount === '2' || slideCount === 2) {
        return;
      }

      this.setupCarousel();
      this.setupEventListeners();
      this.updateCarousel(false);
      
      setTimeout(() => {
        this.setUniformHeight();
      }, 100);
    }
  
    setupCarousel() {
      this.slidesContainer.style.cssText = `
        display: flex;
        justify-content: center;
        align-items: center;
        overflow: visible;
        position: relative;
        width: 100%;
      `;

      this.slideElements.forEach((slide, index) => {
        slide.dataset.originalIndex = index;
        slide.style.cssText += `
          position: absolute;
          top: 0;
          left: 50%;
          transition: transform ${this.config.animationDuration}ms ease-in-out, opacity ${this.config.animationDuration}ms ease-in-out;
          will-change: transform, opacity;
        `;
      });
    }

    setUniformHeight() {
      this.slideElements.forEach(slide => {
        slide.style.height = '';
      });

      requestAnimationFrame(() => {
        let maxHeight = 0;
        
        this.slideElements.forEach(slide => {
          const height = slide.offsetHeight;
          if (height > maxHeight) {
            maxHeight = height;
          }
        });

        // Apply uniform height to all slides
        if (maxHeight > 0) {
          this.slideElements.forEach(slide => {
            slide.style.height = `${maxHeight}px`;
          });

          // Update container height
          if (this.slidesContainer) {
            this.slidesContainer.style.height = `${maxHeight}px`;
          }
        }
      });
    }
  
    setupEventListeners() {
      if (this.prevBtn) {
        this.prevBtn.addEventListener('click', () => this.prev());
      }
  
      if (this.nextBtn) {
        this.nextBtn.addEventListener('click', () => this.next());
      }
  
      // Keyboard navigation
      this.element.addEventListener('keydown', (e) => {
        if (e.key === 'ArrowLeft') {
          this.prev();
        } else if (e.key === 'ArrowRight') {
          this.next();
        }
      });

      // Resize handler
      this.onResize = this.throttle(() => {
        this.setUniformHeight();
        this.updateCarousel(false);
      }, 150);
      window.addEventListener('resize', this.onResize);
    }

    getCardDimensions() {
      let cardWidth = 0;
      for (const card of this.slideElements) {
        const w = card.getBoundingClientRect().width || card.offsetWidth;
        if (w > 0) {
          cardWidth = w;
          break;
        }
      }
      if (!cardWidth) {
        cardWidth = window.innerWidth < this.config.mobileBreakpoint ? 342 : 588;
      }

      const cardGap = window.innerWidth < this.config.mobileBreakpoint
        ? this.config.cardGapMobile
        : this.config.cardGapDesktop;

      return {
        width: cardWidth,
        gap: cardGap,
        spacing: cardWidth + cardGap,
      };
    }

    calculateCardPosition(originalIndex, dimensions) {
      let offsetFromCurrent = originalIndex - this.currentIndex;

      if (this.config.loop) {
        if (offsetFromCurrent > this.totalSlides / 2) {
          offsetFromCurrent -= this.totalSlides;
        } else if (offsetFromCurrent < -this.totalSlides / 2) {
          offsetFromCurrent += this.totalSlides;
        }
      }

      const translateX = offsetFromCurrent * dimensions.spacing;
      const distance = Math.abs(offsetFromCurrent);
      const isInWindow = distance <= 1; 

      return {
        translateX,
        offsetFromCurrent,
        distance,
        isInWindow,
        isActive: offsetFromCurrent === 0,
        originalIndex,
      };
    }
  
    updateCarousel(animate = true) {
      if (this.isAnimating && animate) return;
      if (animate) this.isAnimating = true;

      const dimensions = this.getCardDimensions();
      const transitionValue = animate 
        ? `transform ${this.config.animationDuration}ms ease-in-out, opacity ${this.config.animationDuration}ms ease-in-out`
        : 'none';

      requestAnimationFrame(() => {
        this.slideElements.forEach((slide) => {
          const originalIndex = parseInt(slide.dataset.originalIndex, 10);
          const position = this.calculateCardPosition(originalIndex, dimensions);

          // Calculate styles
          let zIndex = 1;
          let opacity = 0.6;

          if (position.isInWindow) {
            if (position.isActive) {
              zIndex = 100;
              opacity = 1;
            } else {
              zIndex = 50 - position.distance * 10;
              opacity = 0.6;
            }
          }

          const baseTransform = `translateX(calc(-50% + ${position.translateX}px))`;

          // Apply styles
          slide.style.setProperty('--card-base-transform', baseTransform);
          slide.style.transform = baseTransform;
          slide.style.zIndex = zIndex;
          slide.style.opacity = opacity;
          slide.style.transition = transitionValue;
          slide.classList.toggle('is-active', position.isActive);
          slide.setAttribute('aria-hidden', position.isActive ? 'false' : 'true');
          slide.setAttribute('tabindex', position.isActive ? '0' : '-1');

          const links = slide.querySelectorAll('a, button');
          links.forEach(link => {
            if (position.isActive) {
              link.removeAttribute('tabindex');
            } else {
              link.setAttribute('tabindex', '-1');
            }
          });
        });

        // Update UI elements
        this.updateUI();
      });

      if (animate) {
        setTimeout(() => {
          this.isAnimating = false;
        }, this.config.animationDuration);
      }
    }

    updateUI() {
      // Update current slide counter
      if (this.currentSlideElement) {
        this.currentSlideElement.textContent = `${this.currentIndex + 1} / ${this.totalSlides}`;
      }

      // Update button states for non-loop mode
      if (!this.config.loop && this.prevBtn && this.nextBtn) {
        this.prevBtn.disabled = this.currentIndex === 0;
        this.nextBtn.disabled = this.currentIndex === this.totalSlides - 1;
        this.prevBtn.style.opacity = this.currentIndex === 0 ? '0.5' : '1';
        this.nextBtn.style.opacity = this.currentIndex === this.totalSlides - 1 ? '0.5' : '1';
      } else if (this.prevBtn && this.nextBtn) {
        this.prevBtn.disabled = false;
        this.nextBtn.disabled = false;
        this.prevBtn.style.opacity = '1';
        this.nextBtn.style.opacity = '1';
      }
    }
  
    next() {
      if (this.isAnimating) return;
      
      if (this.config.loop) {
        this.currentIndex = (this.currentIndex + 1) % this.totalSlides;
        this.updateCarousel(true);
      } else if (this.currentIndex < this.totalSlides - 1) {
        this.currentIndex++;
        this.updateCarousel(true);
      }
    }
  
    prev() {
      if (this.isAnimating) return;
      
      if (this.config.loop) {
        this.currentIndex = (this.currentIndex - 1 + this.totalSlides) % this.totalSlides;
        this.updateCarousel(true);
      } else if (this.currentIndex > 0) {
        this.currentIndex--;
        this.updateCarousel(true);
      }
    }

    throttle(fn, wait = 150) {
      let timeout = null;
      return function (...args) {
        if (!timeout) {
          fn.apply(this, args);
          timeout = setTimeout(() => {
            timeout = null;
          }, wait);
        }
      };
    }

    destroy() {
      if (this.onResize) {
        window.removeEventListener('resize', this.onResize);
      }
    }
  }
  
  // Auto-initialize on DOM ready
  if (typeof document !== 'undefined') {
    const init = () => {
      const elements = document.querySelectorAll('[data-mod="editorial-carousel-details"], [data-mod="editorial-carousel"]');
      elements.forEach(el => {
        if (!el._editorialCarouselDetailsInstance) {
          el._editorialCarouselDetailsInstance = new EditorialCarouselDetails(el);
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
    window.registerComponent('editorial-carousel-details', EditorialCarouselDetails);
  }
  
  export default EditorialCarouselDetails;
  