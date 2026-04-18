import {
    registerComponent,
    Component
} from '../../../../site/js/mvw.js';
import {
    locationService
} from '../../form-page-hero/js/location-service.js';

class ContactUsForm extends Component {
    static SELECTORS = {
        step: '.contact-us-form__step',
        stepActive: '.contact-us-form__step--active',
        optionBtn: '.contact-us-form__option-btn',
        card: '.contact-us-form__card',
        backBtn: '.contact-us-form__back-btn',
        accordionItem: '.contact-us-form__accordion-item',
        accordionHeader: '.contact-us-form__accordion-header',
        accordionPanel: '.contact-us-form__accordion-panel',
        desktopForm: '.contact-us-form__desktop-form',
        desktopFormContainer: '.contact-us-form__desktop-form-container'
    };

    static CSS_CLASSES = {
        active: 'contact-us-form__step--active',
        accordionExpanded: 'contact-us-form__accordion-item--expanded',
        desktopFormActive: 'contact-us-form__desktop-form--active',
        hasValue: 'has-value',
        error: 'error',
        errorMessage: '.error-message, .form__error-message'
    };

    static VALIDATION = {
        phoneRegex: /^\+?[0-9]+(?:-[0-9]+)*$/,
        emailRegex: /^[A-Za-z0-9._-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/,
        postalCodeRegex: /^\d{5}(?:[-\s]\d{4})?$/,
        postalCodeRegexGeneral: /^[A-Za-z0-9]+(?:[-\s][A-Za-z0-9]+)*$/,
        nameRegex: /^[A-Za-z]+(?:['\-\s][A-Za-z]+)*$/,
        firstNameMaxLength: 40,
        lastNameMaxLength: 80,
        commentMaxLength: 255,
        phoneMaxLength: 40,
        emailMaxLength: 80,
        postalCodeMaxLength: 20,

        countries: {
            usa: 'United States of America'
        }
    };

    constructor(element) {
        super(element);
        this.steps = element.querySelectorAll(ContactUsForm.SELECTORS.step);
        this.currentStep = 1;
        this.history = [1];
        this.isMobile = window.innerWidth <= 768;

        this.init();
    }

    init() {
        this.bindEvents();
        this.initForms();
        this.handleResize();
    }

    bindEvents() {
        if (this.eventsBound) return;
        this.eventsBound = true;

        const optionBtns = this.element.querySelectorAll(ContactUsForm.SELECTORS.optionBtn);
        optionBtns.forEach(btn => {
            btn.addEventListener('click', (e) => this.handleOwnerQuestion(e));
        });

        const accordionHeaders = this.element.querySelectorAll(ContactUsForm.SELECTORS.accordionHeader);
        accordionHeaders.forEach(header => {
            header.addEventListener('click', (e) => {
                this.handleAccordionToggle(e);
            });
        });

        const backBtns = this.element.querySelectorAll(ContactUsForm.SELECTORS.backBtn);
        backBtns.forEach(btn => {
            btn.addEventListener('click', (e) => this.handleBack(e));
        });

        window.addEventListener('resize', () => this.handleResize());
    }

    handleResize() {
        const wasMobile = this.isMobile;
        this.isMobile = window.innerWidth <= 768;

        if (wasMobile !== this.isMobile) {
            this.resetAccordionStates();
        }
    }

    resetAccordionStates() {
        const accordionItems = this.element.querySelectorAll(ContactUsForm.SELECTORS.accordionItem);
        accordionItems.forEach(item => {
            item.classList.remove(ContactUsForm.CSS_CLASSES.accordionExpanded);
            const header = item.querySelector(ContactUsForm.SELECTORS.accordionHeader);
            const panel = item.querySelector(ContactUsForm.SELECTORS.accordionPanel);
            if (header) header.setAttribute('aria-expanded', 'false');
            if (panel) panel.setAttribute('aria-hidden', 'true');
        });

        const desktopForms = this.element.querySelectorAll(ContactUsForm.SELECTORS.desktopForm);
        desktopForms.forEach(form => {
            form.classList.remove(ContactUsForm.CSS_CLASSES.desktopFormActive);
        });
    }

    handleAccordionToggle(e) {
        e.preventDefault();
        e.stopPropagation();

        const header = e.currentTarget;
        const accordionItem = header.closest(ContactUsForm.SELECTORS.accordionItem);
        if (!accordionItem) return;

        const accordionType = accordionItem.dataset.accordion;
        const isExpanded = accordionItem.classList.contains(ContactUsForm.CSS_CLASSES.accordionExpanded);

        const allAccordionItems = this.element.querySelectorAll(ContactUsForm.SELECTORS.accordionItem);
        allAccordionItems.forEach(item => {
            if (item !== accordionItem) {
                item.classList.remove(ContactUsForm.CSS_CLASSES.accordionExpanded);
                const itemHeader = item.querySelector(ContactUsForm.SELECTORS.accordionHeader);
                const itemPanel = item.querySelector(ContactUsForm.SELECTORS.accordionPanel);
                if (itemHeader) itemHeader.setAttribute('aria-expanded', 'false');
                if (itemPanel) {
                    itemPanel.setAttribute('aria-hidden', 'true');
                    itemPanel.style.display = 'none';
                }
            }
        });

        if (this.isMobile) {
            const panel = accordionItem.querySelector(ContactUsForm.SELECTORS.accordionPanel);

            if (isExpanded) {
                accordionItem.classList.remove(ContactUsForm.CSS_CLASSES.accordionExpanded);
                header.setAttribute('aria-expanded', 'false');
                if (panel) {
                    panel.setAttribute('aria-hidden', 'true');
                    panel.style.display = 'none';
                }
            } else {
                accordionItem.classList.add(ContactUsForm.CSS_CLASSES.accordionExpanded);
                header.setAttribute('aria-expanded', 'true');
                if (panel) {
                    panel.setAttribute('aria-hidden', 'false');
                    panel.style.display = 'block';
                    panel.style.padding = '24px';
                    panel.style.borderTop = '1px solid #e0e0e0';
                    this.initFormsInPanel(panel);
                }
            }
        } else {
            const desktopForms = this.element.querySelectorAll(ContactUsForm.SELECTORS.desktopForm);

            const desktopFormContainer = this.element.querySelector(ContactUsForm.SELECTORS.desktopFormContainer);

            if (isExpanded) {
                accordionItem.classList.remove(ContactUsForm.CSS_CLASSES.accordionExpanded);
                header.setAttribute('aria-expanded', 'false');
                if (desktopFormContainer) {
                    desktopFormContainer.setAttribute('style', 'display: block;');
                }
            } else {
                accordionItem.classList.add(ContactUsForm.CSS_CLASSES.accordionExpanded);
                header.setAttribute('aria-expanded', 'true');

                if (desktopFormContainer) {
                    desktopFormContainer.setAttribute('style', 'display: block !important; margin-top: 24px;');
                }

                desktopForms.forEach((form, index) => {
                    if (form.dataset.desktopForm === accordionType) {
                        form.classList.add(ContactUsForm.CSS_CLASSES.desktopFormActive);
                        form.setAttribute('style', 'display: block !important;');
                        this.initFormsInPanel(form);
                    } else {
                        form.classList.remove(ContactUsForm.CSS_CLASSES.desktopFormActive);
                        form.setAttribute('style', 'display: none;');
                    }
                });
            }
        }
    }

    initFormsInPanel(panel) {
        const forms = panel.querySelectorAll('form');
        forms.forEach(form => {
            if (!form.dataset.initialized) {
                this.populateCountrySelect(form);
                this.initFormValidation(form);
                form.dataset.initialized = 'true';
            }
        });
    }

    handleOwnerQuestion(e) {
        const btn = e.currentTarget;
        const isOwner = btn.dataset.owner === 'yes';

        if (isOwner) {
            this.goToStep(2);
        } else {
            this.goToStep(3);
        }
    }

    handleBack(e) {
        const btn = e.currentTarget;
        const backTo = parseInt(btn.dataset.backTo, 10);

        if (backTo) {
            this.goToStep(backTo, true);
        } else {
            this.goBack();
        }
    }

    goToStep(stepNumber, isBack = false) {
        const allSteps = this.element.querySelectorAll(
            ContactUsForm.SELECTORS.step
        );

        allSteps.forEach(step => {
            step.classList.remove(ContactUsForm.CSS_CLASSES.active);
        });

        const nextStepEl = this.element.querySelector(
            `[data-step="${stepNumber}"]`
        );

        if (!nextStepEl) return;

        nextStepEl.classList.add(ContactUsForm.CSS_CLASSES.active);

        if (!isBack) {
            this.history.push(stepNumber);
        }

        this.currentStep = stepNumber;

        setTimeout(() => {
            const hero = document.querySelector('.pageHero');
            const offset = 80;
            if (hero) {
                const heroBottom = hero.offsetTop + hero.offsetHeight;
                window.scrollTo({
                    top: heroBottom - offset,
                    behavior: 'smooth'
                });
            }
        }, 50);
    }


    goBack() {
        if (this.history.length > 1) {
            this.history.pop();
            const previousStep = this.history[this.history.length - 1];
            this.goToStep(previousStep, true);
        }
    }

    initForms() {
        const forms = this.element.querySelectorAll('form');
        forms.forEach(form => {
            if (!form.dataset.initialized) {
                this.populateCountrySelect(form);
                this.initFormValidation(form);
                this.initializeVisibility(form);
                form.dataset.initialized = 'true';
            }
        });
    }

    async populateCountrySelect(form) {
        const countrySelect = form.querySelector('select[name="country"]') || form.querySelector('#country');
        if (!countrySelect) return;
        
        if (countrySelect.options.length > 1) {
            try {
                const countries = await locationService.fetchCountries();
                const countryMap = new Map(countries.map(c => [c.countryName, c.countryCode]));
                
                Array.from(countrySelect.options).forEach(option => {
                    if (option.value && !option.dataset.countryCode) {
                        option.dataset.countryCode = countryMap.get(option.value) || '';
                    }
                });
            } catch (error) {
                console.error('Error getting country options:', error);
            }
            return;
        }
        
        // Populate countries from scratch if not already populated
        if (countrySelect.options.length <= 1) {
            try {
                const countries = await locationService.fetchCountries();
                
                // Find USA and move it to the top
                const usaIndex = countries.findIndex(country => country.countryCode === 'USA');
                if (usaIndex > -1) {
                    const usaCountry = countries.splice(usaIndex, 1)[0];
                    countries.unshift(usaCountry);
                }
                
                countries.forEach(country => {
                    const option = document.createElement('option');
                    option.value = country.countryName;
                    option.textContent = country.countryName;
                    option.dataset.countryCode = country.countryCode;
                    countrySelect.appendChild(option);
                });
            } catch (error) {
                console.error('Error populating countries:', error);
            }
        }
    }

    initializeVisibility(form) {
        const stateGroup = form.querySelector('#stateGroup');
        const postalCodeGroup = form.querySelector('#postalCodeGroup');
        const commentGroup = form.querySelector('#commentGroup');

        if (stateGroup) stateGroup.style.display = 'none';
        if (postalCodeGroup) postalCodeGroup.style.display = 'none';

        const submitBtn = form.querySelector('button[type="submit"]');
        if (submitBtn) {
            submitBtn.disabled = true;
        }
    }

    async updateStateAndZip(countryValue, form) {
        const stateSelect = form.querySelector('#state');
        const stateGroup = form.querySelector('#stateGroup');
        const postalCode = form.querySelector('#postalCode');
        const postalCodeGroup = form.querySelector('#postalCodeGroup');
        const gdprPolicyDiv = form.querySelector('#gdprPolicy');

        try {
            const selectedCountry = await locationService.getCountryByName(countryValue);

            if (gdprPolicyDiv) {
                gdprPolicyDiv.style.display = (selectedCountry && selectedCountry.hasGDPR) ? 'block' : 'none';
            }

            if (!selectedCountry || !stateSelect || !stateGroup) {
                if (stateSelect) stateSelect.innerHTML = '';
                if (stateGroup) stateGroup.style.display = 'none';
                if (postalCodeGroup) postalCodeGroup.style.display = 'none';
                return;
            }

            // Fetch states dynamically for selected country
            if (selectedCountry && selectedCountry.countryCode) {
                const states = await locationService.fetchStates(selectedCountry.countryCode);
                
                if (states && states.length > 0) {
                    stateSelect.innerHTML = '<option value="">Select State</option>';
                    states.forEach(st => {
                        const opt = document.createElement('option');
                        opt.value = st.value;
                        opt.textContent = st.label;
                        opt.dataset.stateCode = st.stateCode;
                        stateSelect.appendChild(opt);
                    });
                    stateGroup.style.display = 'block';
                } else {
                    stateSelect.innerHTML = '';
                    stateGroup.style.display = 'none';
                }
            } else {
                stateSelect.innerHTML = '';
                stateGroup.style.display = 'none';
            }

            if (postalCodeGroup) {
                if (selectedCountry.hasPostalCode) {
                    postalCodeGroup.style.display = 'block';
                } else {
                    postalCodeGroup.style.display = 'none';
                    if (postalCode) postalCode.value = '';
                }
            }
        } catch (error) {
            console.error('Error updating state and zip:', error);
        }
    }

    initFormValidation(form) {
        const inputs = form.querySelectorAll('input, select, textarea');
        const submitBtn = form.querySelector('.contact-us-form__submit-btn');
        const {
            hasValue,
            error
        } = ContactUsForm.CSS_CLASSES;
        const touchedFields = {};

        if (submitBtn) {
            submitBtn.disabled = true;
            submitBtn.style.backgroundColor = '';
            submitBtn.style.color = '';
        }

        const validateFieldOnBlur = (input) => {
            touchedFields[input.id] = true;
            this.validateFieldByType(input, form);
            this.checkSubmitButtonState(form, submitBtn);
        };

        const validateFieldOnChange = (input) => {
            touchedFields[input.id] = true;
            if (input.id === 'email') {
                this.validateEmail(form);
            } else if (input.id === 'comments') {
                const commentGroup = form.querySelector('#commentGroup');
                if (commentGroup && commentGroup.style.display !== 'none') {
                    this.validateComment(form);
                }
            }
            this.checkSubmitButtonState(form, submitBtn);
        };

        inputs.forEach(input => {
            if (input.value) {
                input.classList.add(hasValue);
            }

            input.addEventListener('input', () => {
                if (input.value) {
                    input.classList.add(hasValue);
                } else {
                    input.classList.remove(hasValue);
                }
                if (input.id === 'email' || input.id === 'comments') {
                    validateFieldOnChange(input);
                }
            });

            input.addEventListener('blur', () => {
                validateFieldOnBlur(input);
            });

            if (input.tagName === 'SELECT') {
                input.addEventListener('change', async () => {
                    if (input.value) {
                        input.classList.add(hasValue);
                    } else {
                        input.classList.remove(hasValue);
                    }
                    if (input.id === 'country') {
                        await this.updateStateAndZip(input.value, form);
                    }
                    validateFieldOnBlur(input);
                });
            }
        });
        console.log('Form validation initialized for form:', form);
        form.addEventListener('submit', (e) => this.handleSubmit(e, form));
    }

    validateFieldByType(input, form, silent = false) {
        const inputId = input.id;
        switch (inputId) {

            case 'firstName':
                return this.validateFirstName(form, silent);
            case 'lastName':
                return this.validateLastName(form, silent);
            case 'country':
                return this.validateCountry(form, silent);
            case 'state':
                return this.validateState(form, silent);
            case 'postalCode':
                return this.validatePostalCode(form, silent);
            case 'phone':
                return this.validatePhone(form, silent);
            case 'email':
                return this.validateEmail(form, silent);
            case 'comments':
                return this.validateComment(form, silent);
            default:
                return true;
        }
    }

    checkSubmitButtonState(form, submitBtn) {
        if (!submitBtn) return;

        const validate = {
            firstName: this.validateFirstName(form, true),
            lastName: this.validateLastName(form, true),
            country: this.validateCountry(form, true),
            phone: this.validatePhone(form, true),
            email: this.validateEmail(form, true)
        };

        const stateGroup = form.querySelector('#stateGroup');
        const postalCodeGroup = form.querySelector('#postalCodeGroup');
        const commentGroup = form.querySelector('#commentGroup');

        if (stateGroup && stateGroup.style.display !== 'none') {
            validate.state = this.validateState(form, true);
        }

        if (postalCodeGroup && postalCodeGroup.style.display !== 'none') {
            validate.postalCode = this.validatePostalCode(form, true);
        }

        if (commentGroup && commentGroup.style.display !== 'none') {
            validate.comment = this.validateComment(form, true);
        }

        const allValid = Object.values(validate).every(value => value === true);
        submitBtn.disabled = !allValid;
    }

    validateFirstName(form, silent = false) {
        const firstName = form.querySelector('#firstName');
        if (!firstName) return true;

        const {
            firstNameMaxLength,
            nameRegex
        } = ContactUsForm.VALIDATION;
        const value = firstName.value.trim();

        if (!value) {
            if (!silent) this.showError(firstName, 'Please enter your first name');
            return false;
        } else if (value.length > firstNameMaxLength) {
            if (!silent) this.showError(firstName, `First name must be ${firstNameMaxLength} characters or less`);
            return false;
        } else if (!nameRegex.test(value)) {
            if (!silent) this.showError(firstName, 'First name can only contain letters, hyphens, apostrophes, and spaces');
            return false;
        }

        if (!silent) this.removeError(firstName);
        return true;
    }

    validateLastName(form, silent = false) {
        const lastName = form.querySelector('#lastName');
        if (!lastName) return true;

        const {
            lastNameMaxLength,
            nameRegex
        } = ContactUsForm.VALIDATION;
        const value = lastName.value.trim();

        if (!value) {
            if (!silent) this.showError(lastName, 'Please enter your last name');
            return false;
        } else if (value.length > lastNameMaxLength) {
            if (!silent) this.showError(lastName, `Last name must be ${lastNameMaxLength} characters or less`);
            return false;
        } else if (!nameRegex.test(value)) {
            if (!silent) this.showError(lastName, 'Last name can only contain letters, hyphens, apostrophes, and spaces');
            return false;
        }

        if (!silent) this.removeError(lastName);
        return true;
    }

    validateCountry(form, silent = false) {
        const country = form.querySelector('#country');
        if (!country) return true;

        if (!country.value) {
            if (!silent) this.showError(country, 'Please select your country');
            return false;
        }

        if (!silent) this.removeError(country);
        return true;
    }

    validateState(form, silent = false) {
        const state = form.querySelector('#state');
        const stateGroup = form.querySelector('#stateGroup');

        if (!state || !stateGroup) return true;

        if (stateGroup.style.display === 'none') {
            if (!silent) this.removeError(state);
            return true;
        }

        if (stateGroup.style.display !== 'none') {
            if (!state.value) {
                if (!silent) this.showError(state, 'Please select your state');
                return false;
            }
        }

        if (!silent) this.removeError(state);
        return true;
    }

    validatePostalCode(form, silent = false) {
        const postalCode = form.querySelector('#postalCode');
        const postalCodeGroup = form.querySelector('#postalCodeGroup');

        if (!postalCode || !postalCodeGroup) return true;

        if (postalCodeGroup.style.display === 'none') {
            if (!silent) this.removeError(postalCode);
            return true;
        }

        const {
            postalCodeMaxLength,
            postalCodeRegex,
            postalCodeRegexGeneral
        } = ContactUsForm.VALIDATION;
        const {
            usa
        } = ContactUsForm.VALIDATION.countries;
        const isUSA = country?.value === usa;
        const value = postalCode.value.trim();

        if (postalCodeGroup.style.display !== 'none') {
            if (!value) {
                if (!silent) this.showError(postalCode, 'Please enter your zip/postal code');
                return false;
            } else if (value.length > postalCodeMaxLength) {
                if (!silent) this.showError(postalCode, 'Postal code must be 20 characters or less');
                return false;
            } else if (isUSA && !postalCodeRegex.test(value)) {
                if (!silent) this.showError(postalCode, 'Please enter a valid zipcode (e.g., 12345 or 12345-6789)');
                return false;
            } else if (!isUSA && !postalCodeRegexGeneral.test(value)) {
                if (!silent) this.showError(postalCode, 'Please enter a valid postal code (letters, numbers, and hyphens allowed)');
                return false;
            }
        }

        if (!silent) this.removeError(postalCode);
        return true;
    }

    validatePhone(form, silent = false) {
        const phone = form.querySelector('#phone');
        if (!phone) return true;

        const {
            phoneRegex
        } = ContactUsForm.VALIDATION;
        const value = phone.value.trim();

        if (!value) {
            if (!silent) this.showError(phone, 'Please enter a valid phone number');
            return false;
        } else if (!phoneRegex.test(value)) {
            if (!silent) this.showError(phone, 'Please enter a valid phone number (numbers, + at start, and hyphens allowed)');
            return false;
        }

        if (!silent) this.removeError(phone);
        return true;
    }

    validateEmail(form, silent = false) {
        const email = form.querySelector('#email');
        if (!email) return true;

        const {
            emailRegex,
            emailMaxLength
        } = ContactUsForm.VALIDATION;
        const value = email.value.trim();

        if (!value) {
            if (!silent) this.showError(email, 'Please enter a valid email address');
            return false;
        } else if (value.length > emailMaxLength) {
            if (!silent) this.showError(email, `Email must be ${emailMaxLength} characters or less`);
            return false;
        } else if (!emailRegex.test(value)) {
            if (!silent) this.showError(email, 'Please enter a valid email address');
            return false;
        }

        if (!silent) this.removeError(email);
        return true;
    }

    validateComment(form, silent = false) {
        const commentGroup = form.querySelector('#commentGroup');
        const commentInput = form.querySelector('#comments');

        if (!commentInput) return true;

        if (commentGroup && commentGroup.style.display === 'none') {
            if (!silent) this.removeError(commentInput);
            return true;
        }

        const {
            commentMaxLength
        } = ContactUsForm.VALIDATION;
        const value = commentInput.value.trim();

        if (!value) {
            if (!silent) this.showError(commentInput, 'Please enter a comment');
            return false;
        } else if (value.length > commentMaxLength) {
            if (!silent) this.showError(commentInput, `Comments must be ${commentMaxLength} characters or less`);
            return false;
        }

        if (!silent) this.removeError(commentInput);
        return true;
    }

    showError(input, message) {
        if (!input) return;

        const {
            error,
            errorMessage
        } = ContactUsForm.CSS_CLASSES;
        const formGroup = input.closest('.form-group');
        if (!formGroup) return;

        formGroup.classList.add(error);

        const errorMsg = formGroup.querySelector(errorMessage);
        if (errorMsg) {
            const span = errorMsg.querySelector('span');
            if (span) {
                span.textContent = message;
            } else {
                errorMsg.textContent = message;
            }
            errorMsg.style.display = 'flex';

            input.setAttribute('aria-invalid', 'true');
            if (!errorMsg.id) {
                errorMsg.id = input.id + '-error';
            }
            input.setAttribute('aria-describedby', errorMsg.id);
        }
    }


    removeError(input) {
        if (!input) return;

        const {
            error,
            errorMessage
        } = ContactUsForm.CSS_CLASSES;
        const formGroup = input.closest('.form-group');
        if (formGroup) {
            formGroup.classList.remove(error);
            const errorMsg = formGroup.querySelector(errorMessage);
            if (errorMsg) {
                errorMsg.style.display = 'none';
            }

            input.removeAttribute('aria-invalid');
            input.removeAttribute('aria-describedby');
        }
    }


    handleSubmit(e, form) {
        e.preventDefault();
        e.stopPropagation();

        let isValid = true;

        isValid = this.validateFirstName(form) && isValid;
        isValid = this.validateLastName(form) && isValid;
        isValid = this.validateCountry(form) && isValid;
        isValid = this.validateState(form) && isValid;
        isValid = this.validatePostalCode(form) && isValid;
        isValid = this.validatePhone(form) && isValid;
        isValid = this.validateEmail(form) && isValid;
        isValid = this.validateComment(form) && isValid;

        if (isValid) {
            this.submitToServlet(form);
        } else {
            this.scrollToFirstError(form);
        }
    }

    async submitToServlet(form) {

        if (form.dataset.submitting === 'true') return;
        form.dataset.submitting = 'true';

        const submitButton = form.querySelector('.contact-us-form__submit-btn');
        const originalText = submitButton?.textContent;

        if (submitButton) {
            submitButton.disabled = true;
            submitButton.textContent = 'Submitting...';
        }

        try {
            const formData = new FormData(form);
            const formType = form.dataset.formType;

            const countryName = formData.get('country');
            const selectedCountry = await locationService.getCountryByName(countryName);
            const countryCode = selectedCountry?.countryCode || '';

            let response;
            
            const isOwnerService = formType.includes('owner-service');
            const isCase = isOwnerService;
            const isLead = !isOwnerService;
            const formId = isOwnerService ? 'Owner Services' : '';

            const stateElement = form.querySelector('#state');
            const selectedStateOption = stateElement?.options[stateElement.selectedIndex];
            const stateCode = selectedStateOption?.dataset?.stateCode || '';

            let payload;
            let apiUrl;

            if (isCase) {
                apiUrl = '/bin/mvw/submitLead?type=case';
                payload = {
                    name: `${formData.get('firstName')} ${formData.get('lastName')}`.trim(),
                    phone: parseInt(formData.get('phone').replace(/\D/g, ''), 10) || 0,
                    formId: formId,
                    country: countryName,
                    countryCode: countryCode,
                    email: formData.get('email'),
                    subject: 'Request Information',
                    type: 'Product Support',
                    priority: 'Medium',
                    description: formData.get('comments') || '',
                    reason: 'Website',
                    reasonDetail: 'General Information',
                    origin: 'MVW Web Contact Us',
                    urlSource: window.location.href.substring(0, 255)
                };
            } else if (isLead) {
                apiUrl = '/bin/mvw/submitLead?type=lead';

                const getOptanonConsent = () => {
                    const name = 'OptanonConsent=';
                    const decodedCookie = decodeURIComponent(document.cookie);
                    const cookies = decodedCookie.split(';');

                    for (let i = 0; i < cookies.length; i++) {
                        const c = cookies[i].trim();
                        if (c.indexOf(name) === 0) {
                            const cookieValue = c.substring(name.length);
                            const params = new URLSearchParams(cookieValue);
                            const intTypeVal = params.get('intType');

                            if (intTypeVal === '2') return false;
                            if (intTypeVal === '1' || intTypeVal === '3') return true;
                        }
                    }
                    return false;
                };

                const consentCheckbox = form.querySelector('#consent');

                const postalCodeValue = formData.get('postalCode') || '';
                const postalCodeNumber = parseInt(postalCodeValue.replace(/\D/g, ''), 10);

                const getloc = () => {
                    try {
                        return JSON.parse(localStorage.getItem("tmvc_loc_data"))?.loc || null;
                    } catch (e) {
                        return null;
                    }
                };

                payload = {
                    firstName: formData.get('firstName'),
                    lastName: formData.get('lastName'),
                    formId: formId,
                    countryCode: countryCode,
                    country: countryName,
                    stateCode: stateCode,
                    postalCode: isNaN(postalCodeNumber) ? 0 : postalCodeNumber,
                    phone: parseInt(formData.get('phone').replace(/\D/g, ''), 10) || 0,
                    email: formData.get('email'),
                    webOptin: consentCheckbox?.checked || false,
                    mktSourceSystem: "SFO",
                    mktLeadOriginCode: getloc(),
                    mktInbndOutbndInd: "Outbound",
                    leadSource: 'Website',
                    optanonConsent: getOptanonConsent(),
                    vendorId: '',
                    webReferringWebsite: document.referrer || '',
                    website: window.location.href.substring(0, 255)
                };
            }

            response = await fetch(apiUrl, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(payload)
            });
            // else {
            //     const isOwnerService = formType === 'owner-service';
            //     const action = isOwnerService ? 'web_to_case' : 'web_to_lead';
            //     const formId = isOwnerService ? 'Owner Services' : '';

            //     const message = {
            //         action,
            //         formId,
            //         FirstName: formData.get('firstName'),
            //         LastName: formData.get('lastName'),
            //         Country: formData.get('country'),
            //         State: formData.get('state') || '',
            //         PostalCode: formData.get('postalCode') || '',
            //         Phone: formData.get('phone'),
            //         Email: formData.get('email'),
            //         Description: formData.get('comments') || '',
            //         URL_Source__c: window.location.href
            //     };

            //     const params = new URLSearchParams();
            //     params.append('message', JSON.stringify(message));

            //     response = await fetch('/bin/mvw/request-info', {
            //         method: 'POST',
            //         headers: {
            //             'Content-Type': 'application/x-www-form-urlencoded',
            //             ...(window.Granite?.csrf?.token && {
            //                 'CSRF-Token': window.Granite.csrf.token
            //             })
            //         },
            //         body: params.toString()
            //     });
            // }

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const data = await response.json();

            if (data.redirectUrl && typeof data.redirectUrl === 'string') {
                window.location.assign(data.redirectUrl);
            } else {
                form.reset();
            }

        } catch (error) {
            console.error('Contact Us submission failed:', error);
            alert('Something went wrong. Please try again later.');
        } finally {
            form.dataset.submitting = 'false';

            if (submitButton) {
                submitButton.disabled = false;
                submitButton.textContent = originalText;
            }
        }
    }



    scrollToFirstError(form) {
        const {
            error
        } = ContactUsForm.CSS_CLASSES;
        const firstError = form.querySelector(`.form-group.${error}`);
        if (firstError) {
            firstError.scrollIntoView({
                behavior: 'smooth',
                block: 'center'
            });

            const firstInput = firstError.querySelector('input, select, textarea');
            firstInput?.focus();
        }
    }

}

registerComponent('contact-us-form', ContactUsForm);

export default ContactUsForm;