let modalInstance = null;

class VideoModal {
    constructor() {
        this.modal = null;
        this.isInitialized = false;
        this.listenersAttached = false;
        this.escapeHandler = null;
    }

    createModal() {
        if (document.querySelector('.video-modal')) {
            this.modal = document.querySelector('.video-modal');
            this.isInitialized = true;
            this.setupListeners();
            return;
        }
        const modal = document.createElement('div');
        modal.className = 'video-modal';
        modal.innerHTML = `
            <div class="video-modal__overlay"></div>
            <div class="video-modal__content">
                <button class="video-modal__close" aria-label="Close video" type="button">
                    <span aria-hidden="true">&times;</span>
                </button>
                <div class="video-modal__video-container">
                    <iframe 
                        id="modal-video-player" 
                        class="video-modal__iframe"
                        title="Video player"
                        allow="autoplay; fullscreen" 
                        allowfullscreen 
                        webkitallowfullscreen 
                        mozallowfullscreen 
                        frameborder="0">
                    </iframe>
                </div>
            </div>
        `;
        document.body.appendChild(modal);
        this.modal = modal;
        this.isInitialized = true;
        this.setupListeners();
    }

    setupListeners() {
        if (!this.modal || this.listenersAttached) return;
        const closeBtn = this.modal.querySelector('.video-modal__close');
        const overlay = this.modal.querySelector('.video-modal__overlay');
        closeBtn.addEventListener('click', () => this.close());
        overlay.addEventListener('click', () => this.close());
        this.escapeHandler = (e) => {
            if (e.key === 'Escape' && this.modal.classList.contains('active')) {
                this.close();
            }
        };
        document.addEventListener('keydown', this.escapeHandler);
        this.listenersAttached = true;
    }

    open(videoId) {
        if (!this.isInitialized) this.createModal();
        if (!this.modal) return;
        const iframe = this.modal.querySelector('iframe');
        // Play with audio (muted=0) and controls visible
        const src = `https://players.brightcove.net/1441355349001/jFCpKQt1X_default/index.html?videoId=${videoId}&autoplay=1&controls=1`;
        iframe.src = src;
        this.modal.classList.add('active');
        document.body.style.overflow = 'hidden';
        setTimeout(() => {
            const content = this.modal.querySelector('.video-modal__content');
            if (content) content.focus();
        }, 100);
    }

    close() {
        if (!this.modal) return;
        this.modal.classList.remove('active');
        document.body.style.overflow = '';
        const iframe = this.modal.querySelector('iframe');
        if (iframe) iframe.src = '';
    }
}

export function getVideoModal() {
    if (!modalInstance) modalInstance = new VideoModal();
    return modalInstance;
}
