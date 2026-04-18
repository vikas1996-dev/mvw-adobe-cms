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
                  this.targetUrl = target.href || target.getAttribute('href') || "";
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
                this.openModal(e);
            });
        });
    }

    setupModalListeners() {
        if (!this.modal) return;

        const closeBtn = this.modal.querySelector('[data-cmp-hook-third-party="closeBtn"]');
        const overlay = this.modal.querySelector('[data-cmp-hook-third-party="overlay"]');
        const confirmBtn = this.modal.querySelector('.third-party-modal-tmvc__footer a.cmp-button--main');
        const cancelBtn = this.modal.querySelector('.third-party-modal-tmvc__footer a:not(.cmp-button--main)')

        if (closeBtn) closeBtn.addEventListener('click', (e) => this.closeModal(e));
        if (overlay) overlay.addEventListener('click', (e) => this.closeModal(e));

        if (confirmBtn) {
            confirmBtn.addEventListener('click', (e) => {
                e.preventDefault();
                if (this.targetUrl) {
                    // Open in a new tab/window
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

    openModal(e) {
        if (!this.modal) return;

        // Store the currently focused element (the trigger) so we can restore focus on close
        const url = (this.targetUrl || "").trim();
        this.modal.setAttribute("data-target-url", url);     // easiest to verify in DOM
        this.modal.dataset.targetUrl = url;
        this.modal.classList.add('active');
        document.body.style.overflow = 'hidden';
        const firstFocusable = this.modal.querySelector('button, a, input, textarea, select, [tabindex]:not([tabindex="-1"])');
        firstFocusable.focus();
    }

    closeModal(e) {
        e.preventDefault();
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

// Register component using the project's AEM component registry so AEM/webpack bootstrapping
// can initialize it where appropriate.
registerComponent('third-party-modal-cmp', ThirdPartyModal);

export default ThirdPartyModal;