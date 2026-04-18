(function (window) {
  // Shared SimpleSlider - lightweight, dependency-free slider used across sites
  class SimpleSlider {
    constructor(root, opts = {}) {
      this.root = root instanceof Element ? root : document;
      this.trackSelector = opts.trackSelector || '.dining-cards';
      this.cardSelector = opts.cardSelector || '.dining-card';
      this.nextSelector = opts.nextSelector || '.next';
      this.prevSelector = opts.prevSelector || '.prev';
      this.gap = typeof opts.gap === 'number' ? opts.gap : 24;
      this.visibleFn = typeof opts.visibleFn === 'function' ? opts.visibleFn : () => (window.innerWidth <= 767 ? 1 : 3);

      this.track = this.root.querySelector(this.trackSelector);
      this.cards = Array.from(this.root.querySelectorAll(this.cardSelector));

      // keep lists of matching control elements (desktop + mobile)
      this.nextBtns = Array.from(this.root.querySelectorAll(this.nextSelector));
      this.prevBtns = Array.from(this.root.querySelectorAll(this.prevSelector));

      this.index = 0;
      this.onResize = null;
      this._onRootClick = null;
    }

    init() {
      if (!this.track || this.cards.length === 0) return;

      // Use event delegation on the root context to handle clicks on next/prev controls
      this._onRootClick = (e) => {
        // prefer closest match within the click path
        if (e.target.closest(this.nextSelector)) {
          this.next();
          return;
        }
        if (e.target.closest(this.prevSelector)) {
          this.prev();
          return;
        }
      };

      this.root.addEventListener('click', this._onRootClick);

      this.onResize = this._throttle(() => {
        this.index = 0;
        // refresh card list in case of DOM changes or responsive layout
        this.cards = Array.from(this.root.querySelectorAll(this.cardSelector));
        this.update();
      }, 150);

      window.addEventListener('resize', this.onResize);

      // initial update
      this.update();
      return this;
    }

    destroy() {
      if (this._onRootClick && this.root) this.root.removeEventListener('click', this._onRootClick);
      if (this.onResize) window.removeEventListener('resize', this.onResize);
    }

    getVisibleCount() {
      return this.visibleFn();
    }

    // choose the first button that is currently visible (not display:none and has size)
    _chooseVisibleButton(buttons) {
      if (!buttons || !buttons.length) return null;
      for (let btn of buttons) {
        const style = window.getComputedStyle(btn);
        if (style.display !== 'none' && style.visibility !== 'hidden' && btn.offsetWidth > 0 && btn.offsetHeight > 0) {
          return btn;
        }
      }
      // fallback to first
      return buttons[0] || null;
    }

    update() {
      if (this.cards.length === 0) return;
      const visible = this.getVisibleCount();
      const cardRect = this.cards[0].getBoundingClientRect();
      const cardWidth = Math.round(cardRect.width) + this.gap;

      // clamp index
      const maxIndex = Math.max(0, this.cards.length - visible);
      if (this.index > maxIndex) this.index = maxIndex;
      if (this.index < 0) this.index = 0;

      // apply transform
      this.track.style.transform = `translateX(-${this.index * cardWidth}px)`;

      // update buttons state for all matching controls
      const disabledPrev = this.index === 0;
      const disabledNext = this.index >= maxIndex;

      this.prevBtns.forEach(btn => btn.classList.toggle('disabled', disabledPrev));
      this.nextBtns.forEach(btn => btn.classList.toggle('disabled', disabledNext));
    }

    next() {
      const visible = this.getVisibleCount();
      const maxIndex = Math.max(0, this.cards.length - visible);
      if (this.index < maxIndex) {
        this.index++;
        this.update();
      }
    }

    prev() {
      if (this.index > 0) {
        this.index--;
        this.update();
      }
    }

    _throttle(fn, wait = 150) {
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

    // convenience static initializer accepting selector or element
    static init(rootOrSelector, opts = {}) {
      const root = typeof rootOrSelector === 'string' ? document.querySelector(rootOrSelector) : rootOrSelector;
      if (!root) return null;
      const slider = new SimpleSlider(root, opts);
      slider.init();
      return slider;
    }
  }

  // Export to window for global use (also usable via import in bundled environment)
  window.SimpleSlider = SimpleSlider;
})(window);
