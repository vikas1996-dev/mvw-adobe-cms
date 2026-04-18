import { registerComponent, Component } from '../.././../../site/js/mvw.js';
 
class Hero extends Component {
    static playerScriptPromises = new Map();
 
    static loadBrightcovePlayer(src) {
        if (!Hero.playerScriptPromises.has(src)) {
            Hero.playerScriptPromises.set(
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
        return Hero.playerScriptPromises.get(src);
    }
 
    init() {
        this.heroElement = this.element;
        this.bgType = this.heroElement.getAttribute('data-bg-type') || 'image';
        this.bgImage = this.element.querySelector('[data-cmp-hook-hero="bgImage"]');
        this.bgVideo = this.element.querySelector('[data-cmp-hook-hero="bgVideo"]');
        this.bgVideoPlayer = this.element.querySelector('[data-cmp-hook-hero="bgVideoPlayer"]');
        this.playerInitialized = false;
        this.lazyObserver = null;
        this.setupBackground();
    }
 
    setupBackground() {
        if (this.bgType === 'video' && this.bgVideoPlayer) {
            if (this.bgImage) this.bgImage.style.display = 'none';
            if (this.bgVideo) this.bgVideo.style.display = 'block';
            if (!this.bgVideoPlayer) return;
 
            const observeTarget = this.bgVideoPlayer;
            if ('IntersectionObserver' in window) {
                this.lazyObserver = new IntersectionObserver((entries, observer) => {
                    entries.forEach(entry => {
                        if (entry.isIntersecting) {
                            observer.unobserve(entry.target);
                            this.initializeVideoPlayer();
                        }
                    });
                }, { threshold: 0.1 });
                this.lazyObserver.observe(observeTarget);
            } else {
                this.initializeVideoPlayer();
            }
        }
    }
 
    async initializeVideoPlayer() {
        if (this.playerInitialized || !this.bgVideoPlayer) return;
 
        const account = this.bgVideoPlayer.getAttribute('data-account');
        const player = this.bgVideoPlayer.getAttribute('data-player');
        const embed = this.bgVideoPlayer.getAttribute('data-embed') || 'default';
        const scriptSrc = `https://players.brightcove.net/${account}/${player}_${embed}/index.min.js`;
 
        try {
            await Hero.loadBrightcovePlayer(scriptSrc);
 
            if (window.bc && window.videojs) {
                const playerInstance = window.bc(this.bgVideoPlayer);
                playerInstance.muted(true);
                playerInstance.loop(true);
                playerInstance.controls(false);
                playerInstance.ready(() => {
                    playerInstance.play().catch(() => {
                        playerInstance.bigPlayButton.show();
                    });
                });
                if (this.bgVideo) this.bgVideo.classList.add('hero__video--loaded');
                this.playerInitialized = true;
            }
        } catch (error) {
            console.error('Brightcove background video failed to initialise', error);
        }
    }
}
 
registerComponent('hero-cmp', Hero);
 
export default Hero;