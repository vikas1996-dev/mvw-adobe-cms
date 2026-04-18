(function () {
    'use strict';

    const initMultiBubble = () => {
        const bubbleSections = document.querySelectorAll('.multi-bubble');

        if (!bubbleSections.length) return;

        const observerOptions = {
            root: null, 
            rootMargin: '0px',
            threshold: 0.2 
        };

        const observer = new IntersectionObserver((entries, observer) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    entry.target.classList.add('is-visible');
                    observer.unobserve(entry.target);
                }
            });
        }, observerOptions);

        bubbleSections.forEach(section => {
            observer.observe(section);
        });
    };

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', initMultiBubble);
    } else {
        initMultiBubble();
    }
})();