(function (window, document) {
  'use strict';

  function query(selector, ctx) { return (ctx || document).querySelector(selector); }
  function queryAll(selector, ctx) { return Array.from((ctx || document).querySelectorAll(selector)); }

  class ResortGallery {
    constructor() {
      this.modal = null;
      this.imgEl = null;
      this.captionEl = null;
      this.counterEl = null;
      this.items = [];
      this.index = 0;
      this.boundKey = null;
    }

    init() {
      // build references
      this.modal = query('.gallery-modal');
      if (!this.modal) return;
      this.figureEl = query('.gallery-modal__figure', this.modal);
      this.captionEl = query('.gallery-modal__caption', this.modal);
      this.counterEl = query('.gallery-modal__counter', this.modal);

      // collect media items from the resort-media model (media-grid) — keep sequence
      const mediaItems = queryAll('.resort-media-model .media-grid__item');
      this.items = mediaItems.map(el => {
        const img = el.getAttribute('data-image-src') || (el.querySelector('img') && el.querySelector('img').src) || '';
        const videoId = el.getAttribute('data-video-id') || null;
        const account = el.getAttribute('data-video-account') || el.getAttribute('data-video-account');
        const player = el.getAttribute('data-video-player') || el.getAttribute('data-video-player');
        const alt = (el.querySelector('img') && el.querySelector('img').alt) || '';
        return { img, videoId, account, player, alt };
      }).filter(i => i.img || i.videoId);

      // wire open triggers: page-level [data-open-gallery] and any resort-media triggers
      queryAll('[data-open-gallery]').forEach(btn => btn.addEventListener('click', (e) => {
        e.preventDefault();
        this.open(0);
      }));

      // if user clicks inside the model thumbnails, open gallery at that index
      const modelItems = queryAll('.resort-media-model .media-grid__item');
      modelItems.forEach((el, idx) => {
        el.addEventListener('click', (e) => {
          e.preventDefault();
          this.open(idx);
        });
      });

      // controls
      query('[data-gallery-prev]', this.modal).addEventListener('click', () => this.prev());
      query('[data-gallery-next]', this.modal).addEventListener('click', () => this.next());
      queryAll('[data-gallery-close]', this.modal).forEach(btn => btn.addEventListener('click', () => this.close()));

      // 'Back to Resort Details' in gallery should close gallery and open the media model
      const backBtn = this.modal.querySelector('.gallery-modal__back');
      if (backBtn) backBtn.addEventListener('click', (e) => {
        e.preventDefault();
        // close gallery
        this.close();
        // open resort media model if present
        const mediaModal = document.querySelector('.resort-media-model');
        if (!mediaModal) return;
        mediaModal.classList.add('resort-media-model--open');
        mediaModal.style.display = 'flex';
        // Remove the line below to allow page scrolling
        // document.body.style.overflow = 'hidden';
        const firstClose = mediaModal.querySelector('.close_model');
        if (firstClose) firstClose.focus();
      });

      // keyboard
      this.boundKey = (e) => {
        if (!this.modal || this.modal.getAttribute('aria-hidden') === 'true') return;
        if (e.key === 'ArrowRight') this.next();
        if (e.key === 'ArrowLeft') this.prev();
        if (e.key === 'Escape') this.close();
      };
      document.addEventListener('keydown', this.boundKey);
    }

    open(idx = 0) {
      if (!this.items || !this.items.length || !this.modal) return;
      this.index = Math.max(0, Math.min(idx, this.items.length - 1));
      this.render();
      this.modal.setAttribute('aria-hidden', 'false');
      this.modal.style.display = 'flex';
      document.body.style.overflow = 'hidden';
      // focus close for accessibility
      const closeBtn = this.modal.querySelector('.gallery-modal__close');
      if (closeBtn) closeBtn.focus();
    }

    close() {
      if (!this.modal) return;
      this.modal.setAttribute('aria-hidden', 'true');
      this.modal.style.display = 'none';
      document.body.style.overflow = '';
      // remove any injected iframe to stop Brightcove playback
      if (this.figureEl) this.figureEl.innerHTML = '';
    }

    render() {
      const item = this.items[this.index] || {};
      // clear previous content
      if (this.figureEl) this.figureEl.innerHTML = '';

      if (item.videoId && item.account && item.player) {
        // render Brightcove iframe
        const iframe = document.createElement('iframe');
        iframe.setAttribute('frameborder', '0');
        iframe.setAttribute('allowfullscreen', '');
        iframe.setAttribute('allow', 'autoplay; fullscreen');
        iframe.style.width = '60%';
        iframe.style.height = '62vh';
        // Brightcove player URL pattern
        iframe.src = `https://players.brightcove.net/${encodeURIComponent(item.account)}/${encodeURIComponent(item.player)}_default/index.html?videoId=${encodeURIComponent(item.videoId)}&autoplay=true`;
        this.figureEl.appendChild(iframe);
        // create/attach caption below media
        const cap = document.createElement('figcaption');
        cap.className = 'gallery-modal__caption';
        cap.textContent = item.alt || '';
        this.figureEl.appendChild(cap);
        this.captionEl = cap;
      } else if (item.img) {
        // render image fallback
        const img = document.createElement('img');
        img.className = 'gallery-modal__img';
        img.src = item.img;
        img.alt = item.alt || '';
        img.style.maxWidth = '100%';
        img.style.maxHeight = '62vh';
        this.figureEl.appendChild(img);
        // create/attach caption below image
        const cap = document.createElement('figcaption');
        cap.className = 'gallery-modal__caption';
        cap.textContent = item.alt || '';
        this.figureEl.appendChild(cap);
        this.captionEl = cap;
      } else {
        if (this.captionEl) this.captionEl.textContent = '';
        else {
          const cap = document.createElement('figcaption');
          cap.className = 'gallery-modal__caption';
          this.figureEl.appendChild(cap);
          this.captionEl = cap;
        }
      }

      if (this.counterEl) this.counterEl.textContent = `${this.index + 1} / ${this.items.length}`;
    }

    next() {
      if (this.index < this.items.length - 1) {
        this.index++;
        this.render();
      }
    }
    prev() {
      if (this.index > 0) {
        this.index--;
        this.render();
      }
    }
  }

  // expose as global and auto-init
  const gallery = new ResortGallery();
  function bootstrap() {
    try { gallery.init(); } catch (e) { console.error('Gallery init error', e); }
  }
  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', bootstrap, { once: true });
  } else {
    bootstrap();
  }
  window.getResortGallery = () => gallery;

})(window, document);