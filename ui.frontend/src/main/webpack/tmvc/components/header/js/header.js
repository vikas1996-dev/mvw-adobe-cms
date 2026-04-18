import { registerComponent, Component } from "../../../../site/js/mvw.js";

class Header extends Component {
  init() {
    // Cache DOM elements once
    this.cacheDOM();

    // Internal state
    this.isLoginOpen = false;
    this.lastScrollTop = 0; 
    // Bind events
    this.bindEvents();

    // Sub-nav initialization
    this.setupKeyboardNavigation();

    this.setActiveNavLink();
  }

  /* -------------------------------------------
        CACHE DOM
  -------------------------------------------- */
  cacheDOM() {
    const el = this.element;

    this.header = el;
    this.parentheader = el?.parentNode;
    this.primaryHeader = el.querySelector(".primary-header");
    this.utilityHeader = el.querySelector(".utility-header"); 
    this.menuToggle = el.querySelector(".menu-toggle");
    this.closeIcon = el.querySelector(".close-icon");
    this.navLeft = el.querySelector(".navigation-left");

    // Login elements
    this.userLoginIcon = el.querySelector(".user-login-icon");
    this.loginDropdown = document.querySelector(
      ".account-sec .owner-login-dropdown"
    );

    this.scrollThreshold = 60;
  }

  /* -------------------------------------------
        BIND ALL EVENTS
  -------------------------------------------- */
  bindEvents() {
    window.addEventListener("scroll", () => this.handleScroll());

    this.menuToggle?.addEventListener("click", () => this.toggleMenu());
    this.closeIcon?.addEventListener("click", () => this.closeMenu());

    // Keyboard navigation for close icon
    this.closeIcon?.addEventListener("keydown", (e) => {
      if (e.key === "Enter" || e.key === " ") {
        e.preventDefault();
        this.closeMenu();
      }
    });

    // Login toggle
    if (this.userLoginIcon) {
      this.userLoginIcon.addEventListener("click", (e) => {
        e.stopPropagation();
        this.toggleLoginDropdown();
      });
    }

    // Outside click handler
    document.addEventListener("click", (e) => this.handleOutsideClick(e));
  }

  /* -------------------------------------------
        SCROLL HANDLING
  -------------------------------------------- */
  handleScroll() {
    const currentScroll = window.scrollY;
    const isSticky = currentScroll > this.scrollThreshold;
    const isScrollingDown = currentScroll > this.lastScrollTop;

    if (isSticky) {
      if (this.stickyTimeout) clearTimeout(this.stickyTimeout);
     
      this.parentheader.classList.add("is-sticky");
      void this.parentheader.offsetWidth;
      this.parentheader.classList.add("is-visible");
    } else {
      this.parentheader.classList.remove("is-visible");
      // Remove is-sticky after fade out animation completes
      setTimeout(() => {
        if (!this.parentheader.classList.contains("is-visible")) {
          this.parentheader.classList.remove("is-sticky");
        }
      }, 400);
    }

    if (this.utilityHeader) {
      if (isScrollingDown && currentScroll > this.scrollThreshold) {
        this.utilityHeader.classList.add("hidden");
        document.body.classList.add("utility-hidden"); 
      } else {
        this.utilityHeader.classList.remove("hidden");
        document.body.classList.remove("utility-hidden");
      }
    }

    this.lastScrollTop = currentScroll <= 0 ? 0 : currentScroll;
  }

  /* -------------------------------------------
        MENU TOGGLE
  -------------------------------------------- */
  toggleMenu() {
    this.primaryHeader?.classList.toggle("open");
    this.menuToggle?.classList.toggle("active");
    this.navLeft?.classList.toggle("open");
    console.log("sumit ", this.primaryHeader?.classList.contains("open"));
     document.body.style.overflow = this.primaryHeader?.classList.contains("open") ? "hidden" : "auto";
  }

  closeMenu() {
    this.primaryHeader?.classList.remove("open");
    this.menuToggle?.classList.remove("active");
    this.navLeft?.classList.remove("open");

    this.header
      .querySelectorAll(".nav-item")
      .forEach((i) => i.classList.remove("open"));
      document.body.style.overflow = this.primaryHeader?.classList.contains("open") ? "hidden" : "auto";
    // Return focus to menu toggle button after closing
    this.menuToggle?.focus();
    this.closeLoginDropdown();
  }

  /* -------------------------------------------
        LOGIN DROPDOWN — MOBILE ONLY
  -------------------------------------------- */
  toggleLoginDropdown() {
    if (!this.loginDropdown || window.innerWidth > 1024) return;

    this.isLoginOpen = !this.isLoginOpen;

    this.loginDropdown.classList.toggle("active", this.isLoginOpen);
    this.primaryHeader.classList.toggle("open", this.isLoginOpen);
    document.body.style.overflow = this.primaryHeader?.classList.contains("open") ? "hidden" : "auto";

  }

  closeLoginDropdown() {
    if (!this.loginDropdown) return;

    this.isLoginOpen = false;
    this.loginDropdown.classList.remove("active");
    this.primaryHeader.classList.remove("open");
  }

  handleOutsideClick(e) {
    if (!this.isLoginOpen) return;

    const clickOutside =
      this.loginDropdown &&
      !this.loginDropdown.contains(e.target) &&
      !this.userLoginIcon.contains(e.target);

    if (clickOutside) {
      this.closeLoginDropdown();
    }
  }


  setActiveNavLink() {
    const currentPath = window.location.pathname;
    const navItems = this.header.querySelectorAll('.navigation-left .nav-item');

    navItems.forEach((navItem) => {
      const mainLink = navItem.querySelector('.link');
      const subNavLinks = navItem.querySelectorAll('.sub-nav .child-link, .sub-nav a');

      if (!mainLink) return;

      const mainHref = mainLink.getAttribute('href');
      let isActive = false;

      if (mainHref && this.isPathMatch(currentPath, mainHref)) {
        isActive = true;
      }

      subNavLinks.forEach((subLink) => {
        const subHref = subLink.getAttribute('href');
        if (subHref && this.isPathMatch(currentPath, subHref)) {
          isActive = true;
          subLink.classList.add('is-active');
          subLink.setAttribute('aria-current', 'page');
        }
      });

      if (isActive) {
        navItem.classList.add('is-active');
        mainLink.classList.add('is-active');
        mainLink.setAttribute('aria-current', 'page');
      }
    });

    window.addEventListener('resize', () => this.handleActiveStateResize());
  }


  isPathMatch(currentPath, linkHref) {
    if (!linkHref || linkHref === '#' || linkHref === '') return false;
    const normalizePath = (path) => {
      return path
        .replace(/\.html$/, '') 
        .replace(/\/$/, '') 
        .toLowerCase();
    };
    const normalizedCurrent = normalizePath(currentPath);
    const normalizedLink = normalizePath(linkHref);
    if (normalizedCurrent === normalizedLink) {
      return true;
    }
    if (normalizedCurrent.startsWith(normalizedLink + '/')) {
      return true;
    }
    return false;
  }


  handleActiveStateResize() {
    console.log("resize event triggered");
    const navItems = this.header.querySelectorAll('.navigation-left .nav-item');
    const isDesktop = window.innerWidth > 1024;

    navItems.forEach((navItem) => {
      const mainLink = navItem.querySelector('.link');
      
      if (isDesktop) {
        if (navItem.dataset.wasActive === 'true') {
          navItem.classList.add('is-active');
          mainLink?.classList.add('is-active');
        }
       document.body.style.overflow = "auto";
      } else {
        if (navItem.classList.contains('is-active')) {
          navItem.dataset.wasActive = 'true';
          navItem.classList.remove('is-active');
          mainLink?.classList.remove('is-active');
        }
      }
    });

  }


  setupKeyboardNavigation() {
    const navItems = this.header.querySelectorAll(".nav-item");
    navItems.forEach((item) => {
      const trigger = item.querySelector(".link.has-sub-nav");
      const submenu = item.querySelector(".promo, .owner-login-dropdown");

      if (!trigger || !submenu) return;

      const isDesktop = () => window.innerWidth > 1024;

      const openSubmenu = () => {
        item.classList.add("open");
        trigger.setAttribute("aria-expanded", "true");
        trigger.setAttribute("role", "button");
      };

      const closeSubmenu = () => {
        item.classList.remove("open");
        trigger.setAttribute("aria-expanded", "true");
        trigger.setAttribute("role", "button");
      };

      const toggleSubmenu = () => {
        if (item.classList.contains("open")) closeSubmenu();
        else openSubmenu();
      };

      trigger.addEventListener("click", (e) => {
        if (!isDesktop()) {
          e.preventDefault();
          e.stopPropagation();

          // Close all other nav items
          navItems.forEach((i) => {
            if (i !== item) i.classList.remove("open");
          });

          toggleSubmenu();
        }
      });

      document.addEventListener("click", (e) => {
        if (!isDesktop() && !item.contains(e.target)) {
          closeSubmenu();
        }
      });


      trigger.addEventListener("mouseenter", () => {
        if (isDesktop()) {
          openSubmenu();
        }
      });

      item.addEventListener("mouseleave", () => {
        if (isDesktop()) {
          closeSubmenu();
        }
      });

      item.addEventListener("focusin", (e) => {
        if (isDesktop()) {
          openSubmenu();
        }
      });

      item.addEventListener("focusout", (e) => {
        if (isDesktop() && !item.contains(e.relatedTarget)) {
          closeSubmenu();
        }
      });


      trigger.addEventListener("keydown", (e) => {
        switch (e.key) {
          case "Enter":
          case " ":
          case "ArrowDown":
            e.preventDefault();
            openSubmenu();
            submenu.querySelector("a, button")?.focus();
            break;

          case "Escape":
            closeSubmenu();
            trigger.focus();
            break;
        }
      });

      submenu.addEventListener("keydown", (e) => {
        if (e.key === "Escape") {
          closeSubmenu();
          trigger.focus();
        }
      });
    });
  }
}

registerComponent("header", Header);
export default Header;
