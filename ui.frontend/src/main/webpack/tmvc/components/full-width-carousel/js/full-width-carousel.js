import { registerComponent, Component } from '../../../../site/js/mvw.js';

class FullWidthCarousel extends Component {
    init() {
        this.leftButton = this.element.querySelector(".nav-icon-left");
        this.rightButton = this.element.querySelector(".nav-icon-right");
        this.navIcons = this.element.querySelector(".nav-icons");
        this.currentSlideElement = this.element.querySelector(".current-slide");
        this.carouselContainer = this.element.querySelector(".services-grid");
        this.originalCards = Array.from(this.element.querySelectorAll(".service-card"));

        this.isXl2CardCarousel = this.element.classList.contains('xl2CardCarousel');

        this.config = {
            loop: true,
            animationDuration: 300,
            cardGapDesktop: this.isXl2CardCarousel ? 32 : 25,
            cardGapMobile: 8,
            autoplay: false,
            autoplayInterval: 3000,
            hideNav: false,
            renderWindow: this.isXl2CardCarousel ? 1 : 1,
            mobileBreakpoint: 768,
            minSlidesForSmooth: 4,
            startAlignment: this.isXl2CardCarousel ? 'left' : 'center',
        };

        this.totalOriginalSlides = this.originalCards.length;
        if (this.totalOriginalSlides <= 1) {
            this.element.classList.add('carousel--inactive');
            this.config.loop = false;
        } else {
            this.element.classList.remove('carousel--inactive');
        }
        this.currentSlide = 0;
        this.isAnimating = false;
        this.lastDirection = "next";
        this.allCards = [];
        this.autoPlayId = null;
        this.swipeState = { startX: 0, startY: 0, isDragging: false, moved: false };
        this.cancelNextClick = false;

        if (this.validateElements()) {
            this.setupCarousel();
            this.addEventListeners();
            
            if (this.config.autoplay) {
                this.enableAutoplay();
            }
        }
    }

    validateElements() {
        if (
            !this.leftButton ||
            !this.rightButton ||
            !this.currentSlideElement ||
            !this.carouselContainer ||
            this.originalCards.length === 0
        ) {
            console.warn("Carousel: Missing required elements in component:", this.element);
            return false;
        }
        return true;
    }

    setupCarousel() {
        this.carouselContainer.style.cssText = `
            display: flex;
            justify-content: center;
            align-items: center;
            overflow: visible;
            position: relative;
            width: 100%;
        `;

        this.carouselContainer
            .querySelectorAll("[data-clone], [data-duplicate]")
            .forEach((el) => el.remove());

        this.originalCards.forEach((card, i) => {
            card.dataset.originalIndex = i;
            card.removeAttribute("data-clone");
            card.removeAttribute("data-duplicate");
        });

        if (this.config.loop) {
            if (this.totalOriginalSlides < this.config.minSlidesForSmooth) {
                this.duplicateSlides();
            }

            this.createNavigationClones();
        }

        this.allCards = Array.from(this.carouselContainer.querySelectorAll(".service-card"));
        this.styleCards();
        this.updateCarousel(false);
        this.bindCardClickGuards();
    }

    bindCardClickGuards() {
        if (this.cardClickHandler) return;
        this.cardClickHandler = (event) => {
            if (this.cancelNextClick) {
                event.preventDefault();
                event.stopPropagation();
                this.cancelNextClick = false;
            }
        };
        this.allCards.forEach((card) => {
            card.addEventListener("click", this.cardClickHandler, true);
        });
    }

    duplicateSlides() {
        this.originalCards.forEach((card, i) => {
            const duplicate = card.cloneNode(true);
            duplicate.dataset.duplicate = "true";
            duplicate.dataset.duplicateSource = i;
            duplicate.setAttribute("aria-hidden", "true");
            duplicate.setAttribute("tabindex", "-1");
            this.carouselContainer.appendChild(duplicate);
        });
    }

    createNavigationClones() {
        const cloneCount = Math.min(this.config.renderWindow, this.totalOriginalSlides);
        const allSlidesInDOM = Array.from(
            this.carouselContainer.querySelectorAll(".service-card")
        );

        for (let i = 0; i < cloneCount; i++) {
            const sourceIndex = allSlidesInDOM.length - cloneCount + i;
            const sourceCard = allSlidesInDOM[sourceIndex];
            const clone = sourceCard.cloneNode(true);

            clone.dataset.clone = "true";
            clone.dataset.cloneSource = this.getOriginalIndex(sourceCard);
            clone.setAttribute("aria-hidden", "true");
            clone.setAttribute("tabindex", "-1");

            this.carouselContainer.insertBefore(clone, this.carouselContainer.firstChild);
        }

        const updatedSlides = Array.from(
            this.carouselContainer.querySelectorAll(".service-card")
        );

        for (let i = 0; i < cloneCount; i++) {
            const sourceCard = updatedSlides.find(
                (card) =>
                    card.dataset.originalIndex !== undefined ||
                    card.dataset.duplicateSource !== undefined
            );

            if (!sourceCard) continue;

            const clone = updatedSlides[cloneCount + i].cloneNode(true);
            clone.dataset.clone = "true";
            clone.dataset.cloneSource = this.getOriginalIndex(
                updatedSlides[cloneCount + i]
            );
            clone.setAttribute("aria-hidden", "true");
            clone.setAttribute("tabindex", "-1");

            this.carouselContainer.appendChild(clone);
        }
    }

    styleCards() {
        const transition = `transform ${this.config.animationDuration}ms ease-in-out, opacity ${this.config.animationDuration}ms ease-in-out`;
        const leftPosition = this.config.startAlignment === 'left' || this.config.startAlignment === 'flex-start' ? '0' : '50%';

        this.allCards.forEach((card) => {
            card.style.cssText += `
                position: absolute;
                top: 0;
                left: ${leftPosition};
                transition: ${transition};
                will-change: transform, opacity;
            `;
        });
    }

    updateCarousel(animate = true) {
        if (this.isAnimating && animate) return;
        if (animate) this.isAnimating = true;

        const dimensions = this.getCardDimensions();
        const transitionValue = `transform ${this.config.animationDuration}ms ease-in-out, opacity ${this.config.animationDuration}ms ease-in-out`;

        requestAnimationFrame(() => {
            this.allCards.forEach((card, cardIndex) => {
                const position = this.calculateCardPosition(card, cardIndex, dimensions);
                this.applyCardStyles(card, position, transitionValue);
            });

            this.updateUI();
        });

        if (animate) {
            setTimeout(() => {
                this.handleWrapping();
                this.isAnimating = false;
            }, this.config.animationDuration);
        }
    }

    getCardDimensions() {
        let cardWidth = 0;
        for (const card of this.allCards) {
            const w = card.getBoundingClientRect().width || card.offsetWidth;
            if (w > 0) {
                cardWidth = w;
                break;
            }
        }
        if (!cardWidth) {
            cardWidth = window.innerWidth < this.config.mobileBreakpoint ? 342 : 588;
        }

        const cardGap =
            window.innerWidth < this.config.mobileBreakpoint
                ? this.config.cardGapMobile
                : this.config.cardGapDesktop;

        return {
            width: cardWidth,
            gap: cardGap,
            spacing: cardWidth + cardGap,
        };
    }

    calculateCardPosition(card, cardIndex, dimensions) {
        const originalIndex = this.getOriginalIndex(card);
        const firstOriginalCardIndex = this.allCards.findIndex(
            (c) =>
                c.dataset.originalIndex !== undefined ||
                c.dataset.duplicateSource !== undefined
        );

        let effectivePosition = originalIndex;

        if (card.dataset.duplicate === "true") {
            effectivePosition = originalIndex + this.totalOriginalSlides;
        }
        else if (card.dataset.clone === "true") {
            const isPrependedClone = cardIndex < firstOriginalCardIndex;
            const totalVisibleSlides = this.getTotalVisibleSlides();
            effectivePosition =
                originalIndex +
                (isPrependedClone ? -totalVisibleSlides : totalVisibleSlides);
        }

        let offsetFromCurrent = effectivePosition - this.currentSlide;

        const totalVisibleSlides = this.getTotalVisibleSlides();
        const halfSlides = totalVisibleSlides / 2;

        if (offsetFromCurrent > halfSlides) {
            offsetFromCurrent -= totalVisibleSlides;
        } else if (offsetFromCurrent < -halfSlides) {
            offsetFromCurrent += totalVisibleSlides;
        }

        const translateX = offsetFromCurrent * dimensions.spacing;
        const distance = Math.abs(offsetFromCurrent);
        const isInWindow = distance <= this.config.renderWindow;

        return {
            translateX,
            offsetFromCurrent,
            distance,
            isInWindow,
            isActive: offsetFromCurrent === 0,
            originalIndex,
        };
    }

    applyCardStyles(card, position, transitionValue) {
        let zIndex = 1;
        let opacity = 0.5;

        if (position.isInWindow) {
            if (position.isActive) {
                zIndex = 100;
                opacity = 1;
            } else {
                const totalVisibleSlides = this.getTotalVisibleSlides();
                zIndex = 100 - position.distance * 10;

                if (this.lastDirection === "next" && position.offsetFromCurrent === -1)
                    zIndex += 6;
                if (this.lastDirection === "prev" && position.offsetFromCurrent === 1)
                    zIndex += 6;

                if (this.isXl2CardCarousel) {
                    if (position.offsetFromCurrent < 0) {
                        opacity = 0;
                        zIndex = 0;
                    } else if (position.offsetFromCurrent > 0) {
                        opacity = 1;
                    }
                }
            }
        } else {
            if (this.isXl2CardCarousel && position.offsetFromCurrent < 0) {
                opacity = 0;
            }
        }

        let baseTransform = '';
        if (this.config.startAlignment === 'left' || this.config.startAlignment === 'flex-start') {
            baseTransform = `translateX(${position.translateX}px)`;
        } else {
            baseTransform = `translateX(calc(-50% + ${position.translateX}px))`;
        }

        card.style.setProperty('--card-base-transform', baseTransform);
        card.style.removeProperty('transform');
        card.style.zIndex = zIndex;
        card.style.opacity = opacity;
        card.style.transition = transitionValue;
        card.classList.toggle('is-active', position.isActive);
    }

    handleWrapping() {
        const totalVisibleSlides = this.getTotalVisibleSlides();

        if (this.currentSlide < 0) {
            this.currentSlide += totalVisibleSlides;
            this.updateCarousel(false);
        } else if (this.currentSlide >= totalVisibleSlides) {
            this.currentSlide -= totalVisibleSlides;
            this.updateCarousel(false);
        }
    }

    goToSlide(direction) {
        if (this.isAnimating) return;

        if (this.config.loop) {
            if (direction === "next") {
                this.currentSlide++;
            } else {
                this.currentSlide--;
            }
        } else {
            if (direction === "next" && this.currentSlide < this.totalOriginalSlides - 1) {
                this.currentSlide++;
            } else if (direction === "prev" && this.currentSlide > 0) {
                this.currentSlide--;
            }
        }

        this.lastDirection = direction;
        this.updateCarousel(true);
    }

    updateUI() {
        const displayIndex = ((this.currentSlide % this.totalOriginalSlides) + this.totalOriginalSlides) % this.totalOriginalSlides;
        this.currentSlideElement.textContent = `${displayIndex + 1} / ${this.totalOriginalSlides}`;
        
        if (!this.config.loop) {
            this.leftButton.disabled = this.currentSlide === 0;
            this.rightButton.disabled = this.currentSlide === this.totalOriginalSlides - 1;
            this.leftButton.style.opacity = this.currentSlide === 0 ? "0.5" : "1";
            this.rightButton.style.opacity = this.currentSlide === this.totalOriginalSlides - 1 ? "0.5" : "1";
        } else {
            this.leftButton.disabled = false;
            this.rightButton.disabled = false;
            this.leftButton.style.opacity = "1";
            this.rightButton.style.opacity = "1";
        }
    }

    getOriginalIndex(card) {
        if (card.dataset.originalIndex !== undefined) {
            return parseInt(card.dataset.originalIndex, 10);
        }
        if (card.dataset.duplicateSource !== undefined) {
            return parseInt(card.dataset.duplicateSource, 10);
        }
        if (card.dataset.cloneSource !== undefined) {
            return parseInt(card.dataset.cloneSource, 10);
        }
        return 0;
    }

    getTotalVisibleSlides() {
        if (this.config.loop && this.totalOriginalSlides < this.config.minSlidesForSmooth) {
            return this.totalOriginalSlides * 2;
        }
        return this.totalOriginalSlides;
    }

    addEventListeners() {
        // Navigation buttons
        this.leftButton.addEventListener("click", (e) => {
            e.preventDefault();
            this.goToSlide("prev");
        });

        this.rightButton.addEventListener("click", (e) => {
            e.preventDefault();
            this.goToSlide("next");
        });

        // Keyboard navigation
        document.addEventListener("keydown", (e) => {
            if (this.isAnimating) return;

            const rect = this.element.getBoundingClientRect();
            const isVisible = rect.top < window.innerHeight && rect.bottom > 0;

            if (isVisible) {
                if (e.key === "ArrowLeft") {
                    e.preventDefault();
                    this.goToSlide("prev");
                } else if (e.key === "ArrowRight") {
                    e.preventDefault();
                    this.goToSlide("next");
                }
            }
        });

        // Swipe support
        this.setupTouchEvents();
        this.setupPointerEvents();

        // Window resize
        this.onResize = this.throttle(() => {
            this.updateCarousel(false);
        }, 150);

        window.addEventListener("resize", this.onResize);

        // Hide nav if configured
        if (this.config.hideNav && this.navIcons) {
            this.navIcons.style.display = "none";
        }
    }

    setupTouchEvents() {
        this.carouselContainer.addEventListener("touchstart", (e) => {
            const touch = e.touches[0];
            this.handleSwipeStart(touch.clientX, touch.clientY);
        });

        this.carouselContainer.addEventListener(
            "touchmove",
            (e) => {
                const touch = e.touches[0];
                this.handleSwipeMove(touch.clientX, touch.clientY, true, e);
            },
            { passive: false }
        );

        this.carouselContainer.addEventListener("touchend", (e) => {
            const touch = e.changedTouches[0];
            this.handleSwipeEnd(touch.clientX, touch.clientY);
        });
    }

    setupPointerEvents() {
        this.onMouseDown = (e) => {
            if (e.button !== 0) return;
            e.preventDefault();
            this.handleSwipeStart(e.clientX, e.clientY);
            document.addEventListener("mousemove", this.onMouseMove);
            document.addEventListener("mouseup", this.onMouseUp);
        };

        this.onMouseMove = (e) => {
            this.handleSwipeMove(e.clientX, e.clientY, false, e);
        };

        this.onMouseUp = (e) => {
            this.handleSwipeEnd(e.clientX, e.clientY);
            document.removeEventListener("mousemove", this.onMouseMove);
            document.removeEventListener("mouseup", this.onMouseUp);
        };

        this.carouselContainer.addEventListener("mousedown", this.onMouseDown);
    }

    handleSwipeStart(x, y) {
        this.swipeState.startX = x;
        this.swipeState.startY = y;
        this.swipeState.isDragging = true;
        this.swipeState.moved = false;
    }

    handleSwipeMove(x, y, isTouch, event) {
        if (!this.swipeState.isDragging) return;
        const diffX = Math.abs(x - this.swipeState.startX);
        const diffY = Math.abs(y - this.swipeState.startY);
        if (diffX > 5) {
            this.swipeState.moved = true;
        }
        if (isTouch && diffX > diffY && event) {
            event.preventDefault();
        }
    }

    handleSwipeEnd(x, y) {
        if (!this.swipeState.isDragging) return;
        const diffX = this.swipeState.startX - x;
        const threshold = 50;
        if (Math.abs(diffX) > threshold) {
            this.goToSlide(diffX > 0 ? "next" : "prev");
            this.cancelNextClick = true;
        }
        this.swipeState.isDragging = false;
        this.swipeState.moved = false;
    }

    enableAutoplay() {
        if (!this.config.autoplay || this.autoPlayId) return;

        this.autoPlayId = setInterval(() => {
            if (!this.isAnimating) {
                this.goToSlide("next");
            }
        }, this.config.autoplayInterval);

        this.carouselContainer.addEventListener("mouseenter", this.pauseAutoplay.bind(this));
        this.carouselContainer.addEventListener("mouseleave", this.resumeAutoplay.bind(this));
    }

    pauseAutoplay() {
        if (this.autoPlayId) {
            clearInterval(this.autoPlayId);
            this.autoPlayId = null;
        }
    }

    resumeAutoplay() {
        if (this.config.autoplay && !this.autoPlayId) {
            this.enableAutoplay();
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
        if (this.autoPlayId) {
            clearInterval(this.autoPlayId);
        }
        window.removeEventListener("resize", this.onResize);
        if (this.onMouseDown) {
            this.carouselContainer.removeEventListener("mousedown", this.onMouseDown);
        }
        if (this.onMouseMove) {
            document.removeEventListener("mousemove", this.onMouseMove);
        }
        if (this.onMouseUp) {
            document.removeEventListener("mouseup", this.onMouseUp);
        }
        if (this.cardClickHandler) {
            this.allCards.forEach((card) =>
                card.removeEventListener("click", this.cardClickHandler, true)
            );
        }
    }
}

registerComponent('full-width-carousel', FullWidthCarousel);

export default FullWidthCarousel;