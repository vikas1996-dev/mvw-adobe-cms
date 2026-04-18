import { registerComponent, Component } from '../../../site/js/mvw.js';

class ThirdPartyModal extends Component {
    init() {
        // Ensure we only wire global listeners once even if AEM instantiates multiple component instances
        if (window.__thirdPartyModalInitialized) {
            // If modal exists, keep a reference to it for this instance
            this.modal = document.querySelector('[data-cmp-hook-third-party="modal"]');
            return;
        }

        window.__thirdPartyModalInitialized = true;

        this.modal = document.querySelector('[data-cmp-hook-third-party="modal"]');
        this.targetUrl = null;
        this.triggerElement = null;

        if (!this.modal) {
            console.warn('Third-party modal not found in DOM');
            return;
        }

        // Attach listeners to existing links
        this.attachLinkListeners();

        // Listen for dynamically added links (delegation)
        document.addEventListener('click', (e) => {
            const target = e.target.closest && e.target.closest('.third-party-link');
            if (target) {
                e.preventDefault();
                this.targetUrl = target.getAttribute('href');
                this.triggerElement = target;
                this.openModal();
            }
        });

        // Setup modal listeners
        this.setupModalListeners();
    }

    attachLinkListeners() {
        const links = document.querySelectorAll('.third-party-link');
        links.forEach(link => {
            link.addEventListener('click', (e) => {
                e.preventDefault();
                this.targetUrl = link.getAttribute('href');
                this.triggerElement = link;
                this.openModal();
            });
        });
    }

    setupModalListeners() {
        if (!this.modal) return;

        const closeBtn = this.modal.querySelector('[data-cmp-hook-third-party="closeBtn"]');
        const overlay = this.modal.querySelector('[data-cmp-hook-third-party="overlay"]');
        const confirmBtn = this.modal.querySelector('.third-party-modal-tmvc__footer a.cmp-button--main');
        const cancelBtn = this.modal.querySelector('.third-party-modal-tmvc__footer a:not(.cmp-button--main)');

        if (closeBtn) closeBtn.addEventListener('click', (e) => this.closeModal(e));
        if (overlay) overlay.addEventListener('click', (e) => this.closeModal(e));

        if (confirmBtn) {
            confirmBtn.addEventListener('click', (e) => {
                e.preventDefault();
                if (this.targetUrl) {
                    window.open(this.targetUrl, '_blank');
                    this.closeModal(e);
                }
            });
        }

        if (cancelBtn) cancelBtn.addEventListener('click', (e) => this.closeModal(e));

        // Close on Escape key
        document.addEventListener('keydown', (e) => {
            if (e.key === 'Escape' && this.modal && this.modal.classList.contains('active')) {
                this.closeModal();
            }
        });
    }

    openModal() {
        if (!this.modal) return;

        this.modal.classList.add('active');
        document.body.style.overflow = 'hidden';
        const firstFocusable = this.modal.querySelector('button, a, input, textarea, select, [tabindex]:not([tabindex="-1"])');
        if (firstFocusable) {
            firstFocusable.focus();
        }
    }

    closeModal(e) {
        if (e && typeof e.preventDefault === 'function') {
            e.preventDefault();
        }
        if (!this.modal) return;
        this.modal.classList.remove('active');
        document.body.style.overflow = '';
        this.targetUrl = null;

        if (this.triggerElement && document.body.contains(this.triggerElement)) {
            this.triggerElement.focus();
        }
        this.triggerElement = null;
    }
}

registerComponent('third-party-modal-cmp', ThirdPartyModal);

export default ThirdPartyModal;
