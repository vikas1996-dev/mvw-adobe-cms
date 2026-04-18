import { getVideoModal } from '../../videomodal/js/videomodal';

class MediaLightbox {
  constructor() {
    this.imageOverlay = null;
    this.imageElement = null;
    this.closeButton = null;
    this.boundEscHandler = null;
    this.contentContainer = null;
    this.lastTrigger = null;
    this.currentModalClass = null;
  }

  init(root = document) {
    const triggers = root.querySelectorAll('[data-media-lightbox]');
    if (!triggers.length) return;
    triggers.forEach(trigger => {
      if (trigger.dataset.mediaLightboxBound === 'true') return;
      trigger.dataset.mediaLightboxBound = 'true';
      trigger.addEventListener('click', (event) => this.handleTrigger(event, trigger));
      trigger.addEventListener('keydown', (event) => {
        if (event.key === 'Enter' || event.key === ' ') {
          event.preventDefault();
          this.handleTrigger(event, trigger);
        }
      });
    });
  }

  handleTrigger(event, trigger) {
    // remember which trigger opened the lightbox (so we can read attributes)
    this.lastTrigger = trigger;
    event.preventDefault();
    const mediaType = trigger.getAttribute('data-media-lightbox');
    const videoId = trigger.getAttribute('data-video-id');
    const imageSrc = trigger.getAttribute('data-image-src') || trigger.getAttribute('href');
    const contentId = trigger.getAttribute('data-content-id');

    if (mediaType === 'video' && videoId) {
      // video handled elsewhere
      if (window.getVideoModal) {
        window.getVideoModal().open(videoId);
      }
      return;
    }

    if (mediaType === 'content' && contentId) {
      this.openContent(contentId);
      return;
    }

    if (imageSrc) this.openImage(imageSrc, trigger.getAttribute('data-image-alt') || '');
  }

  openContent(templateId) {
    const tpl = document.getElementById(templateId);
    if (!tpl) return;
    if (!this.imageOverlay) this.buildImageOverlay();

    // Clear previous content
    if (this.contentContainer) this.contentContainer.innerHTML = '';

    const clone = tpl.content.cloneNode(true);
    this.contentContainer.appendChild(clone);

    // add optional class from trigger onto overlay
    try {
      const cls = this.lastTrigger && this.lastTrigger.getAttribute && this.lastTrigger.getAttribute('data-modal-class');
      if (cls) { this.currentModalClass = cls; this.imageOverlay.classList.add(cls); }
    } catch (e) {}

    this.imageOverlay.classList.add('media-lightbox--open');
    this.closeButton.focus();
  }

  openImage(src, alt) {
    if (!this.imageOverlay) this.buildImageOverlay();
    this.imageElement.src = src;
    this.imageElement.alt = alt;
    // ensure content container is empty
    if (this.contentContainer) this.contentContainer.innerHTML = '';
    this.imageElement.style.display = '';
    // add optional class from trigger onto overlay
    try {
      const cls = this.lastTrigger && this.lastTrigger.getAttribute && this.lastTrigger.getAttribute('data-modal-class');
      if (cls) { this.currentModalClass = cls; this.imageOverlay.classList.add(cls); }
    } catch (e) {}
    this.imageOverlay.classList.add('media-lightbox--open');
    this.closeButton.focus();
  }

  buildImageOverlay() {
    this.imageOverlay = document.createElement('div');
    this.imageOverlay.className = 'media-lightbox';
    this.imageOverlay.innerHTML = `
      <figure class="media-lightbox__content" role="document">
        <button type="button" class="media-lightbox__close" aria-label="Close media">&times;</button>
        <div class="media-lightbox__content-body">
          <img class="media-lightbox__image" src="" alt="" style="display:none;" />
        </div>
      </figure>
    `;

    // cache elements
    this.imageElement = this.imageOverlay.querySelector('.media-lightbox__image');
    this.closeButton = this.imageOverlay.querySelector('.media-lightbox__close');
    this.contentContainer = this.imageOverlay.querySelector('.media-lightbox__content-body');

    // close button
    this.closeButton.addEventListener('click', () => this.closeImage());

    // clicking on the overlay (outside the content) closes the lightbox
    this.imageOverlay.addEventListener('click', (evt) => {
      if (evt.target === this.imageOverlay) this.closeImage();
    });

    // ESC handler
    this.boundEscHandler = (event) => {
      if (event.key === 'Escape' && this.imageOverlay.classList.contains('media-lightbox--open')) {
        this.closeImage();
      }
    };
    document.addEventListener('keydown', this.boundEscHandler);

    document.body.appendChild(this.imageOverlay);
  }

  closeImage() {
    if (!this.imageOverlay) return;
    this.imageOverlay.classList.remove('media-lightbox--open');
    if (this.imageElement) {
      this.imageElement.src = '';
      this.imageElement.alt = '';
    }
    if (this.contentContainer) this.contentContainer.innerHTML = '';
    // remove any modal-class added from the trigger
    try {
      if (this.currentModalClass) { this.imageOverlay.classList.remove(this.currentModalClass); this.currentModalClass = null; }
    } catch (e) {}
    this.lastTrigger = null;
  }
}

const sharedMediaLightbox = new MediaLightbox();
export default sharedMediaLightbox;
export const initMediaLightbox = (root) => sharedMediaLightbox.init(root);

// Auto-init once the DOM is ready so components don’t have to wire it manually
if (typeof window !== 'undefined') {
  const bootstrapLightbox = () => sharedMediaLightbox.init(document);
  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', bootstrapLightbox, { once: true });
  } else {
    bootstrapLightbox();
  }
}
