function initResortDetails() {
  // MODAL ELEMENTS
  const modal = document.getElementById("mediaModal");
  const closeBtn = document.querySelector(".media-modal__close");
  const modalImage = document.getElementById("mainModalImage");
  const modalVideoWrapper = document.getElementById("modalVideoWrapper");
  const modalVideo = document.getElementById("modalVideo");
  const playBtn = document.querySelector(".modal-video-play-btn");

  const thumbnails = document.querySelectorAll(".media-modal__thumbnails .thumb");
  const prevBtn = document.querySelector(".thumb-arrow--left");
  const nextBtn = document.querySelector(".thumb-arrow--right");

  let currentModalIndex = 0;
  let mobileIndex = 0;
  let lastFocusedElement = null;

  // HELPER FUNCTIONS
  function showImage(src) {
    if (!modalVideo || !modalImage || !modalVideoWrapper) return;
    modalVideo.pause();
    modalVideo.currentTime = 0;
    modalVideo.removeAttribute("controls");
    if (playBtn) playBtn.style.display = "block";
    modalVideoWrapper.style.display = "none";
    modalImage.style.display = "block";
    modalImage.src = src;
  }

  function showVideo(poster, videoSrc) {
    if (!modalVideo || !modalImage || !modalVideoWrapper) return;
    modalVideo.pause();
    modalVideo.currentTime = 0;
    modalVideo.removeAttribute("controls");
    if (playBtn) playBtn.style.display = "block";

    modalImage.style.display = "none";
    modalVideoWrapper.style.display = "block";

    modalVideo.poster = poster;
    const source = modalVideo.querySelector("source");
    if (source) source.src = videoSrc;
    modalVideo.load();
  }

  function setActiveThumb(activeThumb) {
    if (!thumbnails) return;
    thumbnails.forEach(t => t.classList.remove("active-thumb"));
    activeThumb.classList.add("active-thumb");
  }

  function renderModalByIndex(index) {
    if (!thumbnails || thumbnails.length === 0 || !thumbnails[index]) return;
    const thumb = thumbnails[index];

    setActiveThumb(thumb);
    const allGalleryTriggers = document.querySelectorAll(".media-main, .media-gallery__item");
    lastFocusedElement = allGalleryTriggers[index];
    
    if (thumb.dataset.type === "video") {
      showVideo(thumb.dataset.poster, thumb.dataset.video);
    } else {
      showImage(thumb.dataset.src);
    }
  }

  function closeModal() {
    if (!modal) return;
    modal.style.display = "none";
    document.body.style.overflow = "auto";

    if (lastFocusedElement) {
      lastFocusedElement.focus({ preventScroll: true });
      lastFocusedElement.classList.add('is-focused');
      lastFocusedElement.addEventListener('blur', function handleBlur() {
        lastFocusedElement.classList.remove('is-focused');
        lastFocusedElement.removeEventListener('blur', handleBlur);
      }, { once: true });
    }

    if (modalVideo) {
      modalVideo.pause();
      modalVideo.currentTime = 0;
      modalVideo.removeAttribute("controls");
    }

    document.querySelector(".resort-details-component")?.classList.remove("dim-resort-details-component");
  }

  // CLOSE MODAL
  closeBtn?.addEventListener("click", closeModal);

  modal?.addEventListener("click", (e) => {
    if (!e.target.closest(".media-modal__container")) {
      closeModal();
    }
  });

  thumbnails.forEach((thumb, index) => {
    thumb.addEventListener("click", () => {
      currentModalIndex = index;
      renderModalByIndex(currentModalIndex);
    });
  });

  prevBtn?.addEventListener("click", () => {
    if (thumbnails.length === 0) return;
    currentModalIndex = (currentModalIndex - 1 + thumbnails.length) % thumbnails.length;
    renderModalByIndex(currentModalIndex);
  });

  nextBtn?.addEventListener("click", () => {
    if (thumbnails.length === 0) return;
    currentModalIndex = (currentModalIndex + 1) % thumbnails.length;
    renderModalByIndex(currentModalIndex);
  });

  playBtn?.addEventListener("click", () => {
    if (!modalVideo) return;
    modalVideo.setAttribute("controls", "controls");
    modalVideo.play();
    playBtn.style.display = "none";
  });

  // MOBILE CAROUSEL
  const track = document.querySelector(".carousel-track");
  const items = document.querySelectorAll(".carousel-item");
  const nextArrow = document.querySelector(".carousel-arrow.right");
  const prevArrow = document.querySelector(".carousel-arrow.left");
  const dotsContainer = document.querySelector(".carousel-dots");

  if (track && items.length > 0 && dotsContainer) {
    function updateCarousel() {
      track.style.transform = `translateX(-${mobileIndex * 100}%)`;
      updateDots();
    }

    // Create dots
    items.forEach((_, i) => {
      const dot = document.createElement("div");
      dot.classList.add("dot");
      if (i === 0) dot.classList.add("active");
      dot.dataset.index = i;
      dot.addEventListener("click", () => {
        mobileIndex = i;
        updateCarousel();
      });
      dotsContainer.appendChild(dot);
    });

    function updateDots() {
      const dots = dotsContainer.querySelectorAll(".dot");
      dots.forEach(d => d.classList.remove("active"));
      if (dots[mobileIndex]) dots[mobileIndex].classList.add("active");
    }

    nextArrow?.addEventListener("click", () => {
      mobileIndex = (mobileIndex + 1) % items.length;
      updateCarousel();
    });

    prevArrow?.addEventListener("click", () => {
      mobileIndex = (mobileIndex - 1 + items.length) % items.length;
      updateCarousel();
    });
  }

  // AUTO CLOSE MODAL ON MOBILE RESIZE
  window.addEventListener("resize", () => {
    if (window.innerWidth <= 768) {
      closeModal();
    }
  });

  const mediaWrapper = document.querySelector(".resort-details-wrapper__top");
  if (mediaWrapper && modal) {
    mediaWrapper.addEventListener("click", (e) => {
      const isVideo = e.target.closest(".media-main");
      const isGalleryItem = e.target.closest(".media-gallery__item");
      const isViewAllBtn = e.target.closest(".gallery__cta");

      if (!isVideo && !isGalleryItem) return;

      if (isVideo) {
        currentModalIndex = 0;
      }

      if (isGalleryItem) {
        const galleryItems = [...document.querySelectorAll(".media-gallery__item")];
        const clickedIndex = galleryItems.indexOf(isGalleryItem);
        currentModalIndex = isViewAllBtn ? 0 : clickedIndex + 1;
      }

      lastFocusedElement = isVideo || isGalleryItem;
      modal.style.display = "flex";
      document.body.style.overflow = "hidden";
      trapFocus(modal);
      renderModalByIndex(currentModalIndex);

      document.querySelector(".resort-details-component")?.classList.add("dim-resort-details-component");
    });
  }

  document.addEventListener("keydown", (e) => {
    if (modal && e.key === "Escape" && modal.style.display === "flex") {
      closeModal();
    }
  });

  document.querySelectorAll(".media-main, .media-gallery__item").forEach(el => {
    el.setAttribute("tabindex", "0");
  });

  function trapFocus(modalElement) {
    if (!modalElement) return;
    const focusableSelectors = `a[href], button:not([disabled]), textarea, input, select, video[controls], [tabindex]:not([tabindex="-1"])`;
    const focusableElements = modalElement.querySelectorAll(focusableSelectors);
    
    if (focusableElements.length === 0) return;

    const firstElement = focusableElements[0];
    const lastElement = focusableElements[focusableElements.length - 1];

    modalElement.addEventListener("keydown", function(e) {
      if (e.key !== "Tab") return;
      if (e.shiftKey) {
        if (document.activeElement === firstElement) {
          e.preventDefault();
          lastElement.focus();
        }
      } else {
        if (document.activeElement === lastElement) {
          e.preventDefault();
          firstElement.focus();
        }
      }
    });

    firstElement?.focus();
  }
}

if (document.readyState === 'loading') {
  document.addEventListener('DOMContentLoaded', initResortDetails);
} else {
  initResortDetails();
}