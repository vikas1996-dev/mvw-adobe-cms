class CarouselVideo {
    static playerScriptPromises = new Map();


    static loadBrightcovePlayer(src) {
        if (!CarouselVideo.playerScriptPromises.has(src)) {
            CarouselVideo.playerScriptPromises.set(
                src,
                new Promise((resolve, reject) => {
                    const script = document.createElement('script');
                    script.src = src;
                    script.async = true;
                    script.onload = resolve;
                    script.onerror = () => reject(new Error(`Failed to load Brightcove player: ${src}`));
                    document.head.appendChild(script);
                })
            );
        }
        return CarouselVideo.playerScriptPromises.get(src);
    }

    constructor(element) {
        this.element = element;
        this.playerInstance = null;
        this.init();
    }

    init() {
        this.bgVideoContainer = this.element.querySelector('[data-cmp-hook-carousel="bgVideo"]');
        this.videoElement = this.bgVideoContainer ? this.bgVideoContainer.querySelector('video-js, .video-js') : null;

        this.playerInitialized = false;
        this.lazyObserver = null;

        if (this.videoElement) {
            this.setupObserver();
        }
    }

    setupObserver() {
        if ('IntersectionObserver' in window) {
            this.lazyObserver = new IntersectionObserver((entries) => {
                entries.forEach(entry => {
                    if (entry.isIntersecting) {
                        this.handleEnterViewport();
                    } else {
                        this.handleExitViewport();
                    }
                });
            }, { threshold: 0.25 });
            this.lazyObserver.observe(this.element);
        } else {
            this.initializeVideoPlayer();
        }
    }

    async handleEnterViewport() {
        if (!this.playerInitialized) {
            await this.initializeVideoPlayer();
        }

        if (this.playerInstance) {
            const promise = this.playerInstance.play();
            if (promise !== undefined) {
                promise.catch(error => console.warn("Autoplay blocked:", error));
            }
        }
    }

    handleExitViewport() {
        if (this.playerInstance && !this.playerInstance.paused()) {
            this.playerInstance.pause();
        }
    }

    async initializeVideoPlayer() {
        if (this.playerInitialized || !this.videoElement) return;

        const account = this.videoElement.getAttribute('data-account');
        const player = this.videoElement.getAttribute('data-player');
        const embed = this.videoElement.getAttribute('data-embed') || 'default';
        const scriptSrc = `https://players.brightcove.net/${account}/${player}_${embed}/index.min.js`;

        try {
            await CarouselVideo.loadBrightcovePlayer(scriptSrc);

            if (window.bc && window.videojs) {
                this.playerInstance = window.bc(this.videoElement);

                this.playerInstance.muted(true);
                this.playerInstance.loop(true);
                this.playerInstance.controls(false);
                this.playerInstance.playsinline(true);

                this.playerInstance.on('play', () => {
                    window.videojs.getAllPlayers().forEach(p => {
                        if (p !== this.playerInstance && !p.paused()) {
                            p.pause();
                        }
                    });
                });

                this.bgVideoContainer.classList.add('carousel-video--loaded');
                this.playerInitialized = true;
            }
        } catch (error) {
            console.error('Brightcove carousel video failed to initialize', error);
        }
    }
}


function hideSingleSlideControls() {
    var carousels = document.querySelectorAll('.cmp-carousel');

    carousels.forEach(function (carousel) {
        var items = carousel.querySelectorAll('.cmp-carousel__item');

        if (items.length <= 1) {
            var actions = carousel.querySelector('.cmp-carousel__actions');
            var indicators = carousel.querySelector('.cmp-carousel__indicators');

            if (actions) {
                actions.style.display = 'none';
            }

            if (indicators) {
                indicators.style.display = 'none';
            }
        }
    });
}


function initBrightcoveVideos() {
    const carouselItems = document.querySelectorAll('.cmp-carousel__item');

    carouselItems.forEach(item => {
        if (item.querySelector('[data-cmp-hook-carousel="bgVideo"]')) {
            new CarouselVideo(item);
        }
    });
}

function onDocumentReady() {
    hideSingleSlideControls();
    initBrightcoveVideos();
}

if (document.readyState !== 'loading') {
    onDocumentReady();
} else {
    document.addEventListener('DOMContentLoaded', onDocumentReady);
}
