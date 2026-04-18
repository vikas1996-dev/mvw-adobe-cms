(function () {
    "use strict";

    class BrightcoveVideo {
        static scriptCache = new Map();

        static loadScript(src) {
            if (!BrightcoveVideo.scriptCache.has(src)) {
                BrightcoveVideo.scriptCache.set(src, new Promise((resolve, reject) => {
                    const s = document.createElement('script');
                    s.src = src;
                    s.async = true;
                    s.onload = resolve;
                    s.onerror = () => reject(new Error(`Failed to load script: ${src}`));
                    document.head.appendChild(s);
                }));
            }
            return BrightcoveVideo.scriptCache.get(src);
        }

        constructor(element) {
            this.videoElement = element;
            this.playerInstance = null;
            this.isAutoplay = this.videoElement.getAttribute('data-autoplay') === "true";
            this.init();
        }

        init() {
            if (this.videoElement.getAttribute('data-init') === 'true') return;

            if ('IntersectionObserver' in window) {
               this.observer = new IntersectionObserver((entries) => {
                    entries.forEach(entry => {
                        if (entry.isIntersecting) {
                           this.handleEnterViewport();
                        } else {
                            this.handleExitViewport();
                        }
                    });
                }, { threshold: 0.5 });

                this.observer.observe(this.videoElement);
            } else {
                this.loadAndPlay();
            }
        }

        async handleEnterViewport() {
            if (!this.playerInstance) {
                await this.loadAndInitialize();
            }

            if (this.playerInstance && this.isAutoplay) {
                const promise = this.playerInstance.play();
                if (promise) {
                    promise.catch(e => console.warn("Autoplay blocked", e));
                }
            }
        }

        handleExitViewport() {
            if (this.playerInstance && !this.playerInstance.paused()) {
                this.playerInstance.pause();
            }
        }

        async loadAndInitialize() {
            const account = this.videoElement.getAttribute('data-account');
            const player = this.videoElement.getAttribute('data-player');
            const embed = this.videoElement.getAttribute('data-embed') || 'default';

            if (!account || !player) return;

            const scriptSrc = `https://players.brightcove.net/${account}/${player}_${embed}/index.min.js`;

            try {
                await BrightcoveVideo.loadScript(scriptSrc);

                if (window.bc && window.videojs) {
                    this.playerInstance = window.bc(this.videoElement);

                    const hasControls = this.videoElement.getAttribute('data-controls') === "true";

                    this.playerInstance.controls(hasControls);
                    this.playerInstance.loop(this.videoElement.hasAttribute('loop'));
                    this.playerInstance.playsinline(true);

                    this.playerInstance.on('play', () => {
                        window.videojs.getAllPlayers().forEach(p => {
                            if (p !== this.playerInstance && !p.paused()) {
                                p.pause();
                            }
                        });
                    });

                    this.videoElement.setAttribute('data-init', 'true');
                    this.videoElement.classList.add('video--loaded');
                }
            } catch (error) {
                console.error("Error initializing Brightcove video:", error);
            }
        }
    }

    function onDocumentReady() {
        const videos = document.querySelectorAll('.cmp-video video-js');
        videos.forEach(video => {
            new BrightcoveVideo(video);
        });
    }

    if (document.readyState !== 'loading') {
        onDocumentReady();
    } else {
        document.addEventListener('DOMContentLoaded', onDocumentReady);
    }
})();
