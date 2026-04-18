function initMobileMenu() {
    const mobileMenuToggle = document.getElementById('mobileMenuToggle');
    const mobileMenu = document.getElementById('mobileMenu');
    const mobileMenuClose = document.getElementById('mobileMenuClose');

    
    if (mobileMenuToggle && mobileMenu && mobileMenuClose) {
        function openMobileMenu() {
            mobileMenu.classList.add('is-open');
            mobileMenuToggle.classList.add('is-active');
            document.body.classList.add('no-scroll');
            mobileMenuToggle.setAttribute('aria-expanded', 'true');
            mobileMenu.setAttribute('aria-hidden','false');
            mobileMenu.querySelector('a, button')?.focus();
        }
 
        function closeMobileMenu() {
            mobileMenu.classList.remove('is-open');
            mobileMenuToggle.classList.remove('is-active');
            document.body.classList.remove('no-scroll');
            mobileMenuToggle.setAttribute('aria-expanded', 'false');
            mobileMenu.setAttribute('aria-hidden','true');
            mobileMenuToggle.focus();
        }
 
        mobileMenuToggle.addEventListener('click', function() {
            if (!mobileMenu.classList.contains('is-open')) {
                openMobileMenu();
            } else {
                closeMobileMenu();
            }
        });
 
        mobileMenuToggle.addEventListener('keydown', function(event) {
            if (event.key === 'Enter' || event.key === ' ') {
                event.preventDefault();
                if (!mobileMenu.classList.contains('is-open')) {
                    openMobileMenu();
                } else {
                    closeMobileMenu();
                }
            }
        });
 
        mobileMenuClose.addEventListener('click', function() {
            closeMobileMenu();
        });
 
        mobileMenuClose.addEventListener('keydown', function(event) {
            if (event.key === 'Enter' || event.key === ' ') {
                event.preventDefault();
                closeMobileMenu();
            }
        });
 
        document.addEventListener('keydown', function(event) {
            if (event.key === 'Escape' && mobileMenu.classList.contains('is-open')) {
                closeMobileMenu();
            }
        });
 
        window.addEventListener('resize', function() {
            if (window.innerWidth >= 768) {
                closeMobileMenu();
            }
        });
 
        mobileMenuToggle.setAttribute('aria-expanded', 'false');
        mobileMenuToggle.setAttribute('aria-controls', 'mobileMenu');
        mobileMenu.setAttribute('aria-hidden', 'true');
    } else {
        console.warn("Mobile menu functionality may not work as elements are missing.");
    }
}

if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initMobileMenu);
} else {
    initMobileMenu();
}