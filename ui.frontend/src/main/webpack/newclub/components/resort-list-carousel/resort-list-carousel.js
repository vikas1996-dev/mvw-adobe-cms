import Splide from '@splidejs/splide';

(function () {
    "use strict";

    function initResortListCarousel() {
        const carouselContainers = document.querySelectorAll('.resort-list-carousel');
        carouselContainers.forEach(container => {
            const track = container.querySelector('.resort-list-carousel__cards') || container;
            const isEditMode = container.classList.contains('cq-wcm-edit');

           if (isEditMode) {
                container.classList.add('desktop-static-layout');
                container.classList.remove('center-aligned');
                return;
            }

            const cards = track.querySelectorAll('.featuredcard');
            const cardCount = cards.length;
            const isDiningVariation = track.classList.contains('right-wrapped');

            if (cardCount === 0) return;

            let splide = null;
            const desktopMQ = window.matchMedia('(min-width: 768px)');

            function equalizeHeights() {
                cards.forEach(card => card.style.height = 'auto');

                let maxHeight = 0;
                cards.forEach(card => {
                    if (card.offsetHeight > maxHeight) {
                        maxHeight = card.offsetHeight;
                    }
                });

                if (maxHeight > 0) {
                    cards.forEach(card => card.style.height = maxHeight + 'px');
                }

               if (splide) {
                    splide.refresh();
                }
            }

            function handleLayout() {
                const isDesktop = desktopMQ.matches;
                const shouldBeStatic = isDesktop && (cardCount <= 3);

                if (shouldBeStatic) {
                    if (splide) {
                        splide.destroy();
                        splide = null;
                    }
                    container.classList.add('desktop-static-layout');
                    if (cardCount === 2) {
                        container.classList.add('center-aligned');
                    } else {
                        container.classList.remove('center-aligned');
                    }
                } else {
                    container.classList.remove('desktop-static-layout', 'center-aligned');

                    if (!splide) {
                        const shouldWrap = isDiningVariation ? isDesktop : true;

                        if (!track.classList.contains('splide')) {
                            track.classList.add('splide');
                            const list = track.querySelector('.splide__list') || (() => {
                                const ul = document.createElement('div');
                                ul.className = 'splide__list';
                                while (track.firstChild) ul.appendChild(track.firstChild);
                                const wrapper = document.createElement('div');
                                wrapper.className = 'splide__track';
                                wrapper.appendChild(ul);
                                track.appendChild(wrapper);
                                return ul;
                            })();
                            cards.forEach(card => card.classList.add('splide__slide'));
                        }

                        splide = new Splide(track, {
                            type: shouldWrap ? 'loop' : 'slide',
                            focus: isDiningVariation ? 0 : 'center',
                            perPage: 1,
                            autoWidth: true,
                            pagination: true,
                            arrows: true,
                            gap: 0
                        });
                        splide.mount();
                    }
                }

                equalizeHeights();
            }

            handleLayout();

            desktopMQ.addEventListener('change', handleLayout);
            window.addEventListener('resize', equalizeHeights);

            imagesLoaded(track, function () {
                equalizeHeights();
            });
        });
    }

    function imagesLoaded(element, callback) {
        let imgs = element.querySelectorAll('img');
        let loaded = 0;
        let total = imgs.length;
        if (total === 0) {
            callback();
            return;
        }
        imgs.forEach(img => {
            if (img.complete) {
                loaded++;
                if (loaded === total) callback();
            } else {
                img.addEventListener('load', () => {
                    loaded++;
                    if (loaded === total) callback();
                });
                img.addEventListener('error', () => {
                    loaded++;
                    if (loaded === total) callback();
                });
            }
        });
    }

    if (document.readyState !== "loading") {
        initResortListCarousel();
    } else {
        document.addEventListener("DOMContentLoaded", initResortListCarousel);
    }

})();
