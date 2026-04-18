const Components = [];

class Component {
    constructor(element, shadowroot) {
        if (element) {
            this.element = element;
            this.elData = element.dataset;
            this.shadowRoot = shadowroot || null;
        }
        this.init();
        return this;
    }

    init() {
        return this.getComponent();
    }

    getComponent() {
        return this;
    }

    // Helper method to query select within component
    $(selector) {
        const root = this.shadowRoot || this.element;
        return root.querySelector(selector);
    }

    // Helper method to query select all within component
    $$(selector) {
        const root = this.shadowRoot || this.element;
        return Array.from(root.querySelectorAll(selector));
    }

    // Helper method to add event listener
    on(event, selector, handler) {
        if (typeof selector === 'function') {
            handler = selector;
            this.element.addEventListener(event, handler);
        } else {
            this.element.addEventListener(event, (e) => {
                if (e.target.matches(selector) || e.target.closest(selector)) {
                    handler.call(this, e);
                }
            });
        }
    }

    off(event, handler) {
        this.element.removeEventListener(event, handler);
    }
}

function registerComponent(selector, componentClass, shadowroot = false) {
    const root = shadowroot || document;
    const elements = Array.from(root.querySelectorAll(`[data-mod="${selector}"]`));
    
    elements.forEach(element => {
        // Check if component already initialized on this element
        if (!element._componentInstance) {
            const component = new componentClass(element, shadowroot);
            if (component) {
                Components.push(component);
                element._componentInstance = component;
            }
        }
    });
    
    return elements.length;
}

function getComponentsRegistered() {
    return Promise.resolve(Components);
}

function getComponent(selector) {
    return getComponentsRegistered().then(data => {
        return data.filter(comp => comp.elData.mod === selector);
    }).catch(err => {
        console.error('Error getting components:', err);
        return [];
    });
}

// Utility functions
const ComponentUtils = {
    ready: (fn) => {
        if (document.readyState !== 'loading') {
            fn();
        } else {
            document.addEventListener('DOMContentLoaded', fn);
        }
    },

    ajax: (url, options = {}) => {
        return fetch(url, {
            method: options.method || 'GET',
            headers: options.headers || {},
            body: options.body || null
        }).then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return options.dataType === 'json' ? response.json() : response.text();
        });
    },

    hide: (element) => {
        element.style.display = 'none';
    },

    show: (element) => {
        element.style.display = '';
    },

    toggle: (element) => {
        element.style.display = element.style.display === 'none' ? '' : 'none';
    },

    addClass: (element, className) => {
        element.classList.add(className);
    },

    removeClass: (element, className) => {
        element.classList.remove(className);
    },

    toggleClass: (element, className) => {
        element.classList.toggle(className);
    },

    hasClass: (element, className) => {
        return element.classList.contains(className);
    }
};

export {
    Component,
    registerComponent,
    getComponentsRegistered,
    getComponent,
    ComponentUtils
};