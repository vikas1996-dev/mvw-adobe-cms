(function () {
    'use strict';

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', init);
    } else {
        init();
    }

    function init() {
        const faqComponents = document.querySelectorAll('.mvw-faq[data-cmp-is="accordion"]');

        faqComponents.forEach(function (faqComponent) {
            enhanceFAQ(faqComponent);
        });

        if (window.Granite && window.Granite.author) {
            document.addEventListener('cmp:loaded', function (event) {
                if (event.detail && event.detail.component) {
                    const component = event.detail.component;
                    if (component.classList && component.classList.contains('mvw-faq')) {
                        enhanceFAQ(component);
                    }
                }
            });
        }
    }

    function enhanceFAQ(faqComponent) {
        const buttons = faqComponent.querySelectorAll('.cmp-accordion__button');
        const singleExpansion = faqComponent.getAttribute('data-cmp-single-expansion') === 'true';

        buttons.forEach(function (button) {
            button.removeEventListener('click', handleButtonClick);

            button.addEventListener('click', function (event) {
                handleButtonClick(event, button, faqComponent, singleExpansion);
            });

            button.addEventListener('keydown', function (event) {
                handleKeyboardNavigation(event, button, buttons);
            });
        });
        addSmoothScrollBehavior(faqComponent);
    }

    function handleButtonClick(event, button, faqComponent, singleExpansion) {
        const item = button.closest('.cmp-accordion__item');
        const isExpanded = button.getAttribute('aria-expanded') === 'true';

        if (singleExpansion && !isExpanded) {
            const allItems = faqComponent.querySelectorAll('.cmp-accordion__item');
            allItems.forEach(function (otherItem) {
                if (otherItem !== item) {
                    collapseItem(otherItem);
                }
            });
        }

        if (isExpanded) {
            collapseItem(item);
        } else {
            expandItem(item);
        }

        faqComponent.dispatchEvent(new CustomEvent('mvw:faq:toggle', {
            detail: {
                item: item,
                expanded: !isExpanded,
                button: button
            },
            bubbles: true
        }));
    }

    function expandItem(item) {
        const button = item.querySelector('.cmp-accordion__button');
        const panel = item.querySelector('.cmp-accordion__panel');

        button.setAttribute('aria-expanded', 'true');
        button.classList.add('cmp-accordion__button--expanded');

        item.classList.add('cmp-accordion__item--expanded');
        item.setAttribute('data-cmp-expanded', 'true');

        panel.classList.remove('cmp-accordion__panel--hidden');
        panel.classList.add('cmp-accordion__panel--expanded');
    }

    function collapseItem(item) {
        const button = item.querySelector('.cmp-accordion__button');
        const panel = item.querySelector('.cmp-accordion__panel');

        button.setAttribute('aria-expanded', 'false');
        button.classList.remove('cmp-accordion__button--expanded');

        item.classList.remove('cmp-accordion__item--expanded');
        item.setAttribute('data-cmp-expanded', 'false');

        panel.classList.add('cmp-accordion__panel--hidden');
        panel.classList.remove('cmp-accordion__panel--expanded');
    }

    function handleKeyboardNavigation(event, currentButton, allButtons) {
        const index = Array.from(allButtons).indexOf(currentButton);
        let targetButton;

        switch (event.key) {
            case 'ArrowDown':
                event.preventDefault();
                targetButton = allButtons[index + 1] || allButtons[0];
                targetButton.focus();
                break;

            case 'ArrowUp':
                event.preventDefault();
                targetButton = allButtons[index - 1] || allButtons[allButtons.length - 1];
                targetButton.focus();
                break;

            case 'Home':
                event.preventDefault();
                allButtons[0].focus();
                break;

            case 'End':
                event.preventDefault();
                allButtons[allButtons.length - 1].focus();
                break;
        }
    }

    function addSmoothScrollBehavior(faqComponent) {
        faqComponent.addEventListener('mvw:faq:toggle', function (event) {
            const item = event.detail.item;

            setTimeout(function () {
                const rect = item.getBoundingClientRect();
                const isVisible = rect.top >= 0 && rect.bottom <= window.innerHeight;

                if (!isVisible) {
                    item.scrollIntoView({
                        behavior: 'smooth',
                        block: 'nearest'
                    });
                }
            }, 120);
        });
    }

})();
