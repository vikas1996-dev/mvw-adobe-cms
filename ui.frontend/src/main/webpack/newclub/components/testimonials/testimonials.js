import Splide from '@splidejs/splide';

const splideInstances = new WeakMap();

function manageTestimonialCarousel() {
  const isMobile = window.innerWidth < 768;
  const containers = document.querySelectorAll('.testimonials-container .cards-container');

  containers.forEach(container => {
    const splideInstance = splideInstances.get(container);
    const cards = container.querySelectorAll('.testimonial-card');
    const cardCount = cards.length;

    if (isMobile && cardCount > 1) {
      if (!splideInstance) {
        if (!container.classList.contains('splide')) {
          container.classList.add('splide');
          const ul = document.createElement('div');
          ul.className = 'splide__list';
          while (container.firstChild) ul.appendChild(container.firstChild);
          const wrapper = document.createElement('div');
          wrapper.className = 'splide__track';
          wrapper.appendChild(ul);
          container.appendChild(wrapper);
          cards.forEach(card => card.classList.add('splide__slide'));
        }

        const instance = new Splide(container, {
          type: 'loop',
          focus: 'center',
          perPage: 1,
          autoWidth: true,
          pagination: true,
          arrows: false
        });
        instance.mount();
        splideInstances.set(container, instance);
      }
      container.classList.remove('single-card-view');
    } else {
      if (splideInstance) {
        splideInstance.destroy();
        splideInstances.delete(container);
      }
      
      if(isMobile && cardCount === 1) {
        container.classList.add('single-card-view');
      } else {
        container.classList.remove('single-card-view');
      }
    }
  });
}

// Run on load
if (document.readyState !== "loading") {
  manageTestimonialCarousel();
} else {
  document.addEventListener("DOMContentLoaded", manageTestimonialCarousel);
}

// Run on resize
window.addEventListener('resize', manageTestimonialCarousel);