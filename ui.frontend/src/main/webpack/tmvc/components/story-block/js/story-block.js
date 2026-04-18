import { registerComponent, Component } from "../../../../site/js/mvw.js";

class StoryBlock extends Component {
  static playerScriptPromises = new Map();


  static loadBrightcovePlayer(src) {
    if (!StoryBlock.playerScriptPromises.has(src)) {
      StoryBlock.playerScriptPromises.set(
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
    return StoryBlock.playerScriptPromises.get(src);
  }

  init() {
    this.cacheDOM();
    this.fixHeadingTags();
    this.setupVideoPlayer();
    if (!this.timerSection || !this.isTimerEnabled) return;
    this.remainingTime = this.duration;
    this.interval = null;
    this.startTimer();
  }

  cacheDOM() {
    const el = this.element;
    this.wcmMode = el.dataset.wcmMode;
    this.timerSection = el;
    this.timerText = el.querySelector(".timer-count");
    this.duration = parseInt(el.dataset.duration, 10) || 30;
    this.isTimerEnabled = el.dataset.timercheckbox === "true";
    this.bgVideoPlayer = this.element.querySelector(
      '[data-cmp-hook-hero="storyBlockVideoPlayer"]'
    );
    this.playerInitialized = false;
    this.lazyObserver = null;
    this.redirectUrl = el.querySelector(".timer-sec a")?.href;
    this.redirectTarget = el.querySelector(".timer-sec a")?.target;
  }

  setupVideoPlayer() {
    console.log("Setting up video player", this.bgVideoPlayer);
    if (!this.bgVideoPlayer) return;

      this.initializeVideoPlayer();
  }

  async initializeVideoPlayer() {

    if (this.playerInitialized || !this.bgVideoPlayer) return;

    const account = this.bgVideoPlayer.getAttribute('data-account');
    const player = this.bgVideoPlayer.getAttribute('data-player');
    const embed = this.bgVideoPlayer.getAttribute('data-embed') || 'default';
    const scriptSrc = `https://players.brightcove.net/${account}/${player}_${embed}/index.min.js`;

    try {
      await StoryBlock.loadBrightcovePlayer(scriptSrc);

      if (window.bc && window.videojs) {
        const playerInstance = window.bc(this.bgVideoPlayer);

        playerInstance.controls(true);

        this.playerInitialized = true;
      }
    } catch (error) {
      console.error('Brightcove video player failed to initialize:', error);
    }
  }

  startTimer() {
    if (!this.timerText) return;
    this.timerText.textContent = this.remainingTime;
    this.interval = setInterval(() => {
      this.remainingTime--;
      this.timerText.textContent = this.remainingTime;

      if (this.remainingTime <= 0) {
        this.handleRedirect();
      }
    }, 1000);
  }

  handleRedirect() {
    clearInterval(this.interval);
    console.log("Redirecting to", this.redirectUrl , window?.location , this.wcmMode);
    if (this.wcmMode === 'EDIT') {
      console.log("In Edit mode - redirect prevented");
      return;
    }
    if (!this.redirectUrl) {
      console.warn("No redirect URL specified");
      return;
    }
    console.log("Performing redirect to", this.redirectUrl, this.redirectTarget);
    if (this.redirectTarget === '_blank') {
      const newWindow = window.open(this.redirectUrl, '_blank');
      if (!newWindow) {
        console.warn("Failed to open new window. Popup may be blocked.");
      }
    } else {
      window.location.href = this.redirectUrl;
    }
  }
    fixHeadingTags() {

      this.element.querySelectorAll("h3").forEach((el) => {

        let newTag = null;

        if (el.classList.contains("heading2")) newTag = "h2";
        if (el.classList.contains("heading3")) newTag = "h3";
        if (el.classList.contains("heading4")) newTag = "h4";

        if (newTag && newTag !== "h3") {

          const newEl = document.createElement(newTag);

          newEl.className = el.className;
          newEl.innerHTML = el.innerHTML;

          el.replaceWith(newEl);

        }

      });

    }
  destroy() {
    if (this.interval) {
      clearInterval(this.interval);
    }
    if (this.lazyObserver) {
      this.lazyObserver.disconnect();
    }

    if (this.videoPlayer && window.videojs) {
      try {
        const playerInstance = window.videojs(this.videoPlayer);
        if (playerInstance) {
          playerInstance.dispose();
        }
      } catch (error) {
        console.error('Error disposing video player:', error);
      }
    }
  }
}

registerComponent("story-block", StoryBlock);
export default StoryBlock;
