import { registerComponent, Component } from "../../../../site/js/mvw.js";
import { locationService } from "./location-service.js";

class RequestInformation extends Component {
  static SELECTORS = {
    form: "#bonvoyForm",
    inputs: 'input:not([type="checkbox"]), select, textarea',
    visitRadios: 'input[name="visitReason"]',
    commentGroup: "#commentGroup",
    commentInput: "#comment",
    country: "#country",
    stateGroup: "#stateGroup",
    state: "#state",
    postalCodeGroup: "#postalCodeGroup",
    postalCode: "#postalCode",
    firstName: "#firstName",
    lastName: "#lastName",
    phone: "#phone",
    email: "#email",
    consent: "#consent",
    consentError: "#consentError",
    consentGroup: "#consentGroup",
    visitReasonGroup: "#visitReasonGroup",
    modalTrigger: ".detail-popup__btn",
    modal: "#participationModal",
    modalClose: ".participation-modal__close",
    modalOverlay: ".participation-modal__overlay",
    hiddenFieldsContainer: "#hiddenFields",
  };

  static CSS_CLASSES = {
    hasValue: "has-value",
    error: "error",
    errorMessage: ".error-message, .form__error-message",
  };

  static VALIDATION = {
    phoneRegex: /^\+?[0-9]+(?:-[0-9]+)*$/,
    emailRegex: /^[A-Za-z0-9._-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/,
    postalCodeRegex: /^\d{5}(?:[-\s]\d{4})?$/,
    postalCodeRegexGeneral: /^[A-Za-z0-9]+(?:[-\s][A-Za-z0-9]+)*$/,
    nameRegex: /^[A-Za-z]+(?:['\-\s][A-Za-z]+)*$/,
    firstNameMaxLength: 40,
    commentMaxLength: 255,
    lastNameMaxLength: 80,
    emailMaxLength: 80,
    postalCodeMaxLength: 20,
    visitReasons: {
      prospect: "Prospect",
      ownerServices: "Owner Services",
      owner: "Owner",
    },
    countries: {
      usa: "United States of America",
    },
  };

  init() {
    this.cacheElements();
    if (this.form) {

        if (!this.form.dataset.formId) {
          this.form.dataset.formId =
            (this.form.dataset.formType || "request-information") + "-form";
        }

        if (!this.form.dataset.formName) {
          this.form.dataset.formName =
            this.form.dataset.formType === "special-offers"
              ? "Special Offers"
              : "Request Information";
        }

      }
    this.bindEvents();
    this.initializeVisibility();
    this.populateCountrySelect();
    this.updateVisitReasonVisibility();
    this.initializeSubmitButton();
  }

  initializeSubmitButton() {
    const submitBtn = this.form?.querySelector('button[type="submit"]');
    if (submitBtn) {
      submitBtn.disabled = true;
    }
  }

  removeError(input) {
    if (!input) return;
    const { error, errorMessage } = RequestInformation.CSS_CLASSES;
    const formGroup = input.closest(".form-group");
    if (formGroup) {
      formGroup.classList.remove(error);
      const errorMsg = formGroup.querySelector(errorMessage);
      if (errorMsg) {
        errorMsg.style.display = "none";
      }
    }
  }

  async populateCountrySelect() {
    const countrySelect = this.element.querySelector(
      RequestInformation.SELECTORS.country
    );
    if (!countrySelect) return;

    if (countrySelect.dataset.populated === 'true' || countrySelect.dataset.populating === 'true') {
      return;
    }
    
    if (countrySelect.options.length > 1) {
      const allHaveCodes = Array.from(countrySelect.options).every(
        option => !option.value || option.dataset.countryCode
      );
      
      if (allHaveCodes) {
        countrySelect.dataset.populated = 'true';
        return;
      }
      countrySelect.dataset.populating = 'true';
      try {
        const countries = await locationService.fetchCountries();
        const countryMap = new Map(countries.map(c => [c.countryName, c.countryCode]));
        
        Array.from(countrySelect.options).forEach(option => {
          if (option.value && !option.dataset.countryCode) {
            option.dataset.countryCode = countryMap.get(option.value) || '';
          }
        });
        countrySelect.dataset.populated = 'true';
      } catch (error) {
        console.error('Error enriching country options:', error);
      } finally {
        delete countrySelect.dataset.populating;
      }
      return;
    }
    
    if (countrySelect.options.length <= 1) {
      countrySelect.dataset.populating = 'true';
      try {
        const countries = await locationService.fetchCountries();
        
        const usaIndex = countries.findIndex(country => country.countryCode === 'USA');
        if (usaIndex > -1) {
          const usaCountry = countries.splice(usaIndex, 1)[0];
          countries.unshift(usaCountry);
        }
        
        countries.forEach((country) => {
          const option = document.createElement("option");
          option.value = country.countryName;
          option.textContent = country.countryName;
          option.dataset.countryCode = country.countryCode;
          countrySelect.appendChild(option);
        });
        countrySelect.dataset.populated = 'true';
      } catch (error) {
        console.error('Error populating countries:', error);
      } finally {
        delete countrySelect.dataset.populating;
      }
    }
  }

  async updateStateAndZip(countrySelect) {
    const stateSelect = this.elements.state;
    const { postalCodeGroup, postalCode } = this.elements;

    try {
      const selectedCountry = await locationService.getCountryByName(countrySelect);
      
      const gdprPolicyDiv = document.getElementById("gdprPolicy");
      if (gdprPolicyDiv) {
        gdprPolicyDiv.style.display =
        selectedCountry && selectedCountry.hasGDPR ? "block" : "none";
      }

      if (!countrySelect || !stateSelect || !postalCodeGroup) return;

      if (!selectedCountry) {
        stateSelect.innerHTML = "";
        stateSelect.parentElement.style.display = "none";
        postalCodeGroup.style.display = "none";
        return;
      }

      if (selectedCountry && selectedCountry.countryCode) {
        const states = await locationService.fetchStates(selectedCountry.countryCode);
        
        if (states && states.length > 0) {
          stateSelect.innerHTML = '<option value="">Select State</option>';
          states.forEach((st) => {
            const opt = document.createElement("option");
            opt.value = st.value;
            opt.textContent = st.label;
            opt.dataset.stateCode = st.stateCode;
            stateSelect.appendChild(opt);
          });
          stateSelect.parentElement.style.display = "block";
          stateSelect.focus();
        } else {
          stateSelect.innerHTML = "";
          stateSelect.parentElement.style.display = "none";
        }
      } else {
        stateSelect.innerHTML = "";
        stateSelect.parentElement.style.display = "none";
      }
      
    
      this.updateStateVisibility();
      this.updatePostalCodeVisibility();
      this.checkSubmitButtonState();
    } catch (error) {
      console.error('Error updating state and zip:', error);
    }
  }

  cacheElements() {
    const { SELECTORS } = RequestInformation;

    this.form = this.element.querySelector(SELECTORS.form);

    if (!this.form) return;

    this.elements = {
      inputs: this.form.querySelectorAll(SELECTORS.inputs),
      visitRadios: this.form.querySelectorAll(SELECTORS.visitRadios),
      commentGroup: this.element.querySelector(SELECTORS.commentGroup),
      commentInput: this.element.querySelector(SELECTORS.commentInput),
      country: this.element.querySelector(SELECTORS.country),
      stateGroup: this.element.querySelector(SELECTORS.stateGroup),
      state: this.element.querySelector(SELECTORS.state),
      postalCodeGroup: this.element.querySelector(SELECTORS.postalCodeGroup),
      postalCode: this.element.querySelector(SELECTORS.postalCode),
      firstName: this.element.querySelector(SELECTORS.firstName),
      lastName: this.element.querySelector(SELECTORS.lastName),
      phone: this.element.querySelector(SELECTORS.phone),
      email: this.element.querySelector(SELECTORS.email),
      consent: this.element.querySelector(SELECTORS.consent),
      consentError: this.element.querySelector(SELECTORS.consentError),
      consentGroup: this.element.querySelector(SELECTORS.consentGroup),
      visitReasonGroup: this.element.querySelector(SELECTORS.visitReasonGroup),
      modalTrigger: this.element.querySelector(SELECTORS.modalTrigger),
      modal: document.querySelector(SELECTORS.modal),
      modalClose: document.querySelector(SELECTORS.modalClose),
      modalOverlay: document.querySelector(SELECTORS.modalOverlay),
      hiddenFieldsContainer: this.element.querySelector(
        SELECTORS.hiddenFieldsContainer
      ),
    };
  }

  bindEvents() {
    if (!this.form) return;

    this.bindInputEvents();
    this.bindConditionalFieldEvents();
    this.bindModalEvents();
    this.form.addEventListener("submit", (e) => this.handleSubmit(e));

    if (this.elements.country) {
      this.elements.country.addEventListener("change", async (e) => {
        await this.updateStateAndZip(e.target.value);
      });
    }
  }


    checkSubmitButtonState(){
        // const { hasValue, error, errorMessage } = RequestInformation.CSS_CLASSES;
     const submitBtn = this.form.querySelector('button[type="submit"]');

      const validate = {
        firstName: this.validateFirstName(true),
        lastName: this.validateLastName(true),
        country: this.validateCountry(true),
        phone: this.validatePhone(true),
        email: this.validateEmail(true),
      };

      const { visitReasonGroup, stateGroup, postalCodeGroup, commentGroup } =
        this.elements;

      if (visitReasonGroup && visitReasonGroup.style.display !== "none") {
        validate.visitReason = this.validateVisitReason(true);
      }

      if (stateGroup && stateGroup.style.display !== "none") {
        validate.state = this.validateState(true);
      }

      if (postalCodeGroup && postalCodeGroup.style.display !== "none") {
        validate.postalCode = this.validatePostalCode(true);
      }

      if (commentGroup && commentGroup.style.display !== "none") {
        validate.comment = this.validateComment(true);
      }

      const allValid = Object.values(validate).every((value) => value === true);
      validate.allValid = allValid;

      if (submitBtn) {
        submitBtn.disabled = !allValid;
      }
    }

  bindInputEvents() {
    const { hasValue, } = RequestInformation.CSS_CLASSES;
    // const submitBtn = this.form.querySelector('button[type="submit"]');

    const touchedFields = {};


    const validateFieldOnBlur = (fieldId) => {
      touchedFields[fieldId] = true;

      switch (fieldId) {
        case "visitReason":
          this.validateVisitReason();
          break;
        case "firstName":
          this.validateFirstName();
          break;
        case "lastName":
          this.validateLastName();
          break;
        case "country":
          this.validateCountry();
          break;
        case "state":
          this.validateState();
          break;
        case "postalCode":
          this.validatePostalCode();
          break;
        case "phone":
          this.validatePhone();
          break;
        case "email":
          this.validateEmail();
          break;
        case "comment":
          this.validateComment();
          break;
      }

      this.checkSubmitButtonState();
    };

    const validateFieldOnChange = (fieldId) => {
      touchedFields[fieldId] = true;
      const { commentGroup, visitReasonGroup } = this.elements;
      if (commentGroup && commentGroup.style.display !== "none") {
        this.validateComment();
      } else {
        this.validateEmail();
        this.validateVisitReason();
      }
      if (visitReasonGroup && visitReasonGroup.style.display !== "none") {
        this.validateVisitReason();
      }
      this.checkSubmitButtonState();
    };

    this.elements.inputs.forEach((input) => {
      input.addEventListener("input", () => {
        if (input.value) {
          input.classList.add(hasValue);
        } else {
          input.classList.remove(hasValue);
        }
        if (input.id === "email" || input.id === "comment") {
          validateFieldOnChange(input.id);
        }
      });

      input.addEventListener("blur", () => {
        validateFieldOnBlur(input.id);
      });
    });
  }

  bindConditionalFieldEvents() {
    this.elements.visitRadios.forEach((radio) => {
      radio.addEventListener("change", () => {
        this.updateCommentVisibility();
        this.updateStateVisibility();
        this.updatePostalCodeVisibility(); 
        this.updateConsentVisibility();
        this.clearVisitReasonError();
        this.checkSubmitButtonState();
      });
    });

    if (this.elements.state) {
      this.elements.state.addEventListener("change", () => {
        this.checkSubmitButtonState();
      });
    }
  }

  bindModalEvents() {
    const { modalTrigger, modal, modalClose, modalOverlay } = this.elements;

    if (modalTrigger && modal) {
      modalTrigger.addEventListener("click", (e) => {
        e.preventDefault();
        this.openModal();
      });
    }

    if (modalClose) {
      modalClose.addEventListener("click", () => this.closeModal());
    }

    if (modalOverlay) {
      modalOverlay.addEventListener("click", () => this.closeModal());
    }

    document.addEventListener("keydown", (e) => {
      if (e.key === "Escape" && modal && modal.classList.contains("is-open")) {
        this.closeModal();
      }
    });
  }

  openModal() {
    const { modal } = this.elements;
    if (modal) {
      modal.classList.add("is-open");
      document.body.classList.add("modal-open");
      modal.querySelector(".participation-modal__close")?.focus();
    }
  }

  closeModal() {
    const { modal, modalTrigger } = this.elements;
    if (modal) {
      modal.classList.remove("is-open");
      document.body.classList.remove("modal-open");
      if (modalTrigger) {
        modalTrigger.focus();
      }
    }
  }

  updateCommentVisibility() {
    const { commentGroup, commentInput } = this.elements;
    const { ownerServices } = RequestInformation.VALIDATION.visitReasons;
    const selected = this.form.querySelector(
      'input[name="visitReason"]:checked'
    );

    if (selected && selected.value === ownerServices) {
      commentGroup.style.display = "block";
    } else {
      commentGroup.style.display = "none";
      if (commentInput) commentInput.value = "";
    }
  }

  clearVisitReasonError() {
    const { visitReasonGroup } = this.elements;
    const { error } = RequestInformation.CSS_CLASSES;

    if (visitReasonGroup) {
      visitReasonGroup.classList.remove(error);
      const errorMsg = visitReasonGroup.querySelector(".error-message");
      if (errorMsg) {
        errorMsg.style.display = "none";
      }
    }
  }

  updateStateVisibility() {
    const { country, stateGroup, state } = this.elements;
  
    const { ownerServices } = RequestInformation.VALIDATION.visitReasons;
    const selectedReason = this.form.querySelector('input[name="visitReason"]:checked')?.value;
  
     if (selectedReason === ownerServices) {
      stateGroup.style.display = "none";
      if (state) state.value = ""; 
    } 
  
    else if (state && state.options.length > 1) {
      stateGroup.style.display = "block";
    } 
    
    else {
      stateGroup.style.display = "none";
      if (state) state.value = "";
    }
  }

  updateConsentVisibility() {
    const { consentGroup, consent } = this.elements;
    const { ownerServices } = RequestInformation.VALIDATION.visitReasons;
    const selectedReason = this.form.querySelector('input[name="visitReason"]:checked')?.value;

    if (consentGroup) {
      if (selectedReason === ownerServices) {
        consentGroup.style.display = "none";
        if (consent) {
          consent.checked = false;
          consent.required = false; 
        }
      } else {
        consentGroup.style.display = "block";
        if (consent) {
          consent.required = true;
        }
      }
    }
  }

  async updatePostalCodeVisibility() {
    const { state, stateGroup, postalCodeGroup, postalCode, country } = this.elements;

    const { ownerServices } = RequestInformation.VALIDATION.visitReasons;
    
    const selectedReason = this.form.querySelector('input[name="visitReason"]:checked')?.value;

    if (selectedReason === ownerServices) {
      postalCodeGroup.style.display = "none";
      if (postalCode) postalCode.value = "";
      this.checkSubmitButtonState();
      return;
    }

    if (country && country.value) {
      const selectedCountry = await locationService.getCountryByName(country.value);
      
      if (selectedCountry && selectedCountry.hasPostalCode) {
        postalCodeGroup.style.display = "block";
      } else {
        // Hide and clear postal code if country doesn't require it
        postalCodeGroup.style.display = "none";
        if (postalCode) postalCode.value = "";
      }
    } else {
      // No country selected, hide postal code
      postalCodeGroup.style.display = "none";
      if (postalCode) postalCode.value = "";
    }
    
    // Re-validate submit button state
    this.checkSubmitButtonState();
  }

  initializeVisibility() {
    this.updateCommentVisibility();
    this.updateStateVisibility();
    this.updatePostalCodeVisibility();
    this.updateConsentVisibility();
  }

  setupSuppliedNameUpdate() {
    const { firstName, lastName, hiddenFieldsContainer } = this.elements;

    const updateSuppliedName = () => {
      const suppliedNameField = hiddenFieldsContainer.querySelector(
        'input[name="SuppliedName"]'
      );
      if (suppliedNameField) {
        const combinedName =
          `${firstName.value.trim()} ${lastName.value.trim()}`.trim();
        suppliedNameField.value = combinedName;
      }
    };

    firstName.removeEventListener("input", this.updateSuppliedNameBound);
    lastName.removeEventListener("input", this.updateSuppliedNameBound);

    this.updateSuppliedNameBound = updateSuppliedName;

    firstName.addEventListener("input", updateSuppliedName);
    lastName.addEventListener("input", updateSuppliedName);
  }

  handleSubmit(e) {
    e.preventDefault();

    let isValid = true;

    isValid = this.validateVisitReason() && isValid;
    isValid = this.validateFirstName() && isValid;
    isValid = this.validateLastName() && isValid;
    isValid = this.validateCountry() && isValid;
    isValid = this.validateState() && isValid;
    isValid = this.validatePostalCode() && isValid;
    isValid = this.validatePhone() && isValid;
    isValid = this.validateEmail() && isValid;
    isValid = this.validateComment() && isValid;

    if (isValid) {
      this.submitForm();
    } else {
      this.scrollToFirstError();
    }
  }

  updateVisitReasonVisibility() {
    const { visitReasonGroup } = this.elements;
    // Check data-form-type attribute on the form
    const formType = this.form?.getAttribute("data-form-type");
    if (formType !== "request-information") {
      // Hide the group and do not validate
      if (visitReasonGroup) visitReasonGroup.style.display = "none";
      return true;
    }
    // Show/hide details-of-participation based on formType
    const detailsElement = document.getElementById("details-of-participation");
    if (detailsElement) {
      if (formType === "special-offers") {
        detailsElement.style.display = "";
      } else {
        detailsElement.style.display = "none";
      }
    }
  }

  validateVisitReason(silent = false) {
    const { visitReasonGroup } = this.elements;
    const { error } = RequestInformation.CSS_CLASSES;
    // Check data-form-type attribute on the form
    const formType = this.form?.getAttribute("data-form-type");
    if (formType !== "request-information") {
      // Hide the group and do not validate
      if (!silent && visitReasonGroup) visitReasonGroup.style.display = "none";
      return true;
    }
    // Show/hide details-of-participation based on formType
    const detailsElement = document.getElementById("details-of-participation");
    if (!silent && detailsElement) {
      if (formType === "special-offers") {
        detailsElement.style.display = "";
      } else {
        detailsElement.style.display = "none";
      }
    }
    // Show the group and validate
    if (!silent && visitReasonGroup) visitReasonGroup.style.display = "";
    const selected = this.form.querySelector(
      'input[name="visitReason"]:checked'
    );
    if (!selected) {
      if (!silent && visitReasonGroup) {
        visitReasonGroup.classList.add(error);
        const errorMsg = visitReasonGroup.querySelector(".error-message");
        if (errorMsg) {
          errorMsg.style.display = "flex";
        }
      }
      return false;
    }
    if (!silent && visitReasonGroup) {
      visitReasonGroup.classList.remove(error);
      const errorMsg = visitReasonGroup.querySelector(".error-message");
      if (errorMsg) {
        errorMsg.style.display = "none";
      }
    }
    return true;
  }

  validateFirstName(silent = false) {
    const { firstName } = this.elements;
    const { firstNameMaxLength, nameRegex } = RequestInformation.VALIDATION;
    if (!firstName.value.trim()) {
      if (!silent) this.showError(firstName, "Please enter your first name");
      return false;
    } else if (firstName.value.trim().length > firstNameMaxLength) {
      if (!silent)
        this.showError(
          firstName,
          `First name must be ${firstNameMaxLength} characters or less`
        );
      return false;
    } else if (!nameRegex.test(firstName.value.trim())) {
      if (!silent)
        this.showError(
          firstName,
          "First name can only contain letters, hyphens, apostrophes, and spaces"
        );
      return false;
    }
    if (!silent) this.removeError(firstName);
    return true;
  }

  validateLastName(silent = false) {
    const { lastName } = this.elements;
    const { lastNameMaxLength, nameRegex } = RequestInformation.VALIDATION;
    if (!lastName.value.trim()) {
      if (!silent) this.showError(lastName, "Please enter your last name");
      return false;
    } else if (lastName.value.trim().length > lastNameMaxLength) {
      if (!silent)
        this.showError(
          lastName,
          `Last name must be ${lastNameMaxLength} characters or less`
        );
      return false;
    } else if (!nameRegex.test(lastName.value.trim())) {
      if (!silent)
        this.showError(
          lastName,
          "Last name can only contain letters, hyphens, apostrophes, and spaces"
        );
      return false;
    }
    if (!silent) this.removeError(lastName);
    return true;
  }

  validateCountry(silent = false) {
    const { country } = this.elements;
    if (!country.value) {
      if (!silent) this.showError(country, "Please select your country");
      return false;
    }
    if (!silent) this.removeError(country);
    return true;
  }

  validateState(silent = false) {
    const { state, stateGroup } = this.elements;

    if (stateGroup && stateGroup.style.display === "none") {
      if (!silent) this.removeError(state);
      return true;
    }
    
    if (stateGroup && stateGroup.style.display !== "none") {
      if (!state.value) {
        if (!silent) this.showError(state, "Please select your state");
        return false;
      }
    }
    
    if (!silent) this.removeError(state);
    return true;
  }

  validatePostalCode(silent = false) {
    const { postalCode, postalCodeGroup, country } = this.elements;
    
    if (postalCodeGroup && postalCodeGroup.style.display === "none") {
      if (!silent) this.removeError(postalCode);
      return true;
    }
    
    const { postalCodeMaxLength, postalCodeRegex, postalCodeRegexGeneral } = RequestInformation.VALIDATION;
    const { usa } = RequestInformation.VALIDATION.countries;
    const isUSA = country?.value === usa;
    
    if (postalCodeGroup && postalCodeGroup.style.display !== "none") {
      if (!postalCode.value.trim()) {
        if (!silent)
          this.showError(postalCode, "Please enter your zip/postal code");
        return false;
      } else if (postalCode.value.trim().length > postalCodeMaxLength) {
        if (!silent)
          this.showError(
            postalCode,
            "Postal code must be 20 characters or less"
          );
        return false;
      } else if (isUSA && !postalCodeRegex.test(postalCode.value.trim())) {
        if (!silent)
          this.showError(postalCode, "Please enter a valid zipcode (e.g., 12345 or 12345-6789)");
        return false;
      } else if (!isUSA && !postalCodeRegexGeneral.test(postalCode.value.trim())) {
        if (!silent)
          this.showError(postalCode, "Please enter a valid postal code (letters, numbers, and hyphens allowed)");
        return false;
      }
    }
    if (!silent) this.removeError(postalCode);
    return true;
  }

  validatePhone(silent = false) {
    const { phone } = this.elements;
    const { phoneRegex } = RequestInformation.VALIDATION;
    if (!phone.value.trim()) {
      if (!silent) this.showError(phone, "Please enter a valid phone number");
      return false;
    } else if (!phoneRegex.test(phone.value.trim())) {
      if (!silent)
        this.showError(
          phone,
          "Please enter a valid phone number (numbers, + at start, and hyphens allowed)"
        );
      return false;
    }
    if (!silent) this.removeError(phone);
    return true;
  }

  validateEmail(silent = false) {
    const { email } = this.elements;
    const { emailRegex, emailMaxLength } = RequestInformation.VALIDATION;
    if (!email.value.trim()) {
      if (!silent) this.showError(email, "Please enter a valid email address");
      return false;
    } else if (email.value.trim().length > emailMaxLength) {
      if (!silent)
        this.showError(
          email,
          `Email must be ${emailMaxLength} characters or less`
        );
      return false;
    } else if (!emailRegex.test(email.value.trim())) {
      if (!silent) this.showError(email, "Please enter a valid email address");
      return false;
    }
    if (!silent) this.removeError(email);
    return true;
  }

  validateComment(silent = false) {
    const { commentGroup, commentInput } = this.elements;
    const { commentMaxLength } = RequestInformation.VALIDATION;
    if (commentGroup.style.display !== "none") {
      if (!commentInput.value.trim()) {
        if (!silent) this.showError(commentInput, "Please enter a comment");
        return false;
      } else if (commentInput.value.trim().length > commentMaxLength) {
        if (!silent)
          this.showError(
            commentInput,
            `Comments must be ${commentMaxLength} characters or less`
          );
        return false;
      }
    }
    if (!silent) this.removeError(commentInput);
    return true;
  }

  showError(input, message) {
    if (!input) return;

    const { error, errorMessage } = RequestInformation.CSS_CLASSES;
    const formGroup = input.closest(".form-group");
    if (!formGroup) return;

    formGroup.classList.add(error);
    const errorMsg = formGroup.querySelector(errorMessage);
    if (errorMsg) {
      const span = errorMsg.querySelector("span");
      if (span) {
        span.textContent = message;
      } else {
        errorMsg.textContent = message;
      }
      errorMsg.style.display = "flex";
    }
  }

  // submitForm() {
  //     const formData = this.getFormData();
  //     console.log('Form Data with Hidden Fields:', formData);

  //     let formDataDisplay = `Form submitted successfully!\n\n` +
  //         `First Name: ${formData.firstName}\n` +
  //         `Last Name: ${formData.lastName}\n` +
  //         `Country: ${formData.country}\n` +
  //         `State: ${formData.state}\n` +
  //         `Zip/Postal Code: ${formData.postalCode}\n` +
  //         `Phone: ${formData.phone}\n` +
  //         `Email: ${formData.email}\n` +
  //         `Comments: ${formData.comments}\n` +
  //         `Visit Reason: ${formData.visitReason}\n`;

  //     if (formData.hiddenFields && Object.keys(formData.hiddenFields).length > 0) {
  //         formDataDisplay += `\nHidden Fields:\n`;
  //         Object.entries(formData.hiddenFields).forEach(([key, value]) => {
  //             formDataDisplay += `${key}: ${value}\n`;
  //         });
  //     }

  //     alert(formDataDisplay);
  // }

  async submitForm() {
    const submitButton = this.form.querySelector(
      'button[type="submit"], input[type="submit"]'
    );
    const originalButtonText = submitButton ? submitButton.textContent : "";

    if (submitButton) {
      submitButton.disabled = true;
      submitButton.textContent = "Submitting...";
    }

    try {
      const formData = new FormData(this.form);
      const visitReason = formData.get("visitReason");
      const formType = this.form?.getAttribute("data-form-type");

      const selectedCountry = await locationService.getCountryByName(formData.get("country"));
      const countryCode = selectedCountry?.countryCode || "";
      const countryName = formData.get("country");

      const isCase = visitReason === "Owner Services";
      const isLead = visitReason === "Prospect" || visitReason === "Owner" || formType === "special-offers";

      const selectedStateElement = this.elements.state;
      const selectedStateOption = selectedStateElement?.options[selectedStateElement.selectedIndex];
      const stateCode = selectedStateOption?.dataset?.stateCode || "";
      const requestType =
        formType === "special-offers"
          ? "special-offers"
          : formData.get("visitReason");

      let payload;
      let apiUrl;

      if (isCase) {
        apiUrl = "/bin/mvw/submitLead?type=case";
        payload = {
          name: `${formData.get("firstName")} ${formData.get("lastName")}`.trim(),
          phone: parseInt(formData.get("phone").replace(/\D/g, ''), 10) || 0,
          email: formData.get("email"),
          formId: requestType,
          countryCode: countryCode,
          country: countryName,
          subject: "Request Information",
          type: "Product Support",
          priority: "Medium",
          description: formData.get("comment") || "",
          reason: "Website",
          reasonDetail: "General Information",
          origin: "MVW Web Contact Us",
          urlSource: window.location.href.substring(0, 255)
        };
      } else if (isLead) {
        apiUrl = "/bin/mvw/submitLead?type=lead";

        const getOptanonConsent = () => {
          const name = "OptanonConsent=";
          const decodedCookie = decodeURIComponent(document.cookie);
          const cookies = decodedCookie.split(";");

          for (let i = 0; i < cookies.length; i++) {
            const c = cookies[i].trim();
            if (c.indexOf(name) === 0) {
              const cookieValue = c.substring(name.length);
              const params = new URLSearchParams(cookieValue);
              const intTypeVal = params.get("intType");

              if (intTypeVal === "2") return false;
              if (intTypeVal === "1" || intTypeVal === "3") return true;
            }
          }
          return false;
        };

        const postalCodeValue = formData.get("postalCode") || "";
        const getloc = () => {
          try {
            return JSON.parse(localStorage.getItem("tmvc_loc_data"))?.loc || null;
          } catch (e) {
            return null;
          }
        };
        const postalCodeNumber = parseInt(postalCodeValue.replace(/\D/g, ''), 10);

        payload = {
          firstName: formData.get("firstName"),
          lastName: formData.get("lastName"),
          countryCode: countryCode,
          stateCode: stateCode,
          country: countryName,
          formId: requestType,
          postalCode: isNaN(postalCodeNumber) ? 0 : postalCodeNumber,
          phone: parseInt(formData.get("phone").replace(/\D/g, ''), 10) || 0,
          email: formData.get("email"),
          webOptin: this.elements.consent?.checked || false,
          mktSourceSystem: "SFO",
          mktLeadOriginCode: getloc(),
          mktInbndOutbndInd: "Outbound",
          leadSource: "Website",
          optanonConsent: getOptanonConsent(),
          vendorId: "",
          webReferringWebsite: document.referrer || "",
          website: window.location.href.substring(0, 255)
        };
      }

      let response;

      response = await fetch(apiUrl, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "CSRF-Token": window.Granite?.csrf?.token,
        },
        body: JSON.stringify(payload),
      });
      // else {
      //   const requestType = formData.get("visitReason");
      //   const isCaseForm = requestType === "Owner Services";

      //   const message = Object.assign(
      //     {
      //       action: isCaseForm ? "web_to_case" : "web_to_lead",
      //       formId: requestType,
      //       FirstName: formData.get("firstName"),
      //       LastName: formData.get("lastName"),
      //       Country: formData.get("country"),
      //       State: formData.get("state") || "",
      //       PostalCode: formData.get("postalCode"),
      //       Description: formData.get("comment") || "",
      //     },
      //     isCaseForm
      //       ? {
      //           SuppliedPhone: formData.get("phone"),
      //           SuppliedEmail: formData.get("email"),
      //           Origin: "MVW Web Contact Us",
      //           SuppliedName: `${formData.get("firstName")} ${formData.get(
      //             "lastName"
      //           )}`.trim(),
      //           Type: "Product Support",
      //           ReasonDetail__c: "General Information",
      //           ReasonDetailReplicate__c: "General Information",
      //           Reason__c: "Website",
      //           Subject: "Request Information",
      //           URL_Source__c: window.location.href,
      //         }
      //       : {
      //           Phone: formData.get("phone"),
      //           Email: formData.get("email"),
      //           Web_Optin__c: this.elements.consent?.checked ? "true" : "false",
      //           MKT_Source_System__c: "SFD",
      //           MKT_Lead_Origin_Code_c: "",
      //           MKT_Inbnd_Outbnd_Ind_c: "Outbound",
      //           LeadSource: "Website",
      //           OptanonConsent__c: "0",
      //           VendorId_c: "",
      //           WEB_Referring_Website__c: document.referrer || "",
      //           Website: window.location.href,
      //         }
      //   );

      //   const params = new URLSearchParams();
      //   params.append("message", JSON.stringify(message));

      //   response = await fetch("/bin/mvw/request-info", {
      //     method: "POST",
      //     headers: {
      //       "Content-Type": "application/x-www-form-urlencoded",
      //       "CSRF-Token": window.Granite?.csrf?.token,
      //     },
      //     body: params.toString(),
      //   });
      // }

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const data = await response.json();
      const formId = this.form.dataset.formId || "special-offers";
      const formName = this.form.dataset.formName || "Special Offers";

      if (data.redirectUrl && data.redirectUrl !== "") {
        try {
          const parsed = new URL(data.redirectUrl, window.location.origin);
          if (parsed.origin === window.location.origin) {
            window.location.href = parsed.pathname + parsed.search + parsed.hash;
          }
        } catch (e) {
        }
      } else {
        this.form.reset();
      }
    } catch (error) {
      console.error("Form submission error:", error);
    } finally {
      if (submitButton) {
        submitButton.disabled = false;
        submitButton.textContent = originalButtonText;
      }
    }
  }

  getFormData() {
    const {
      firstName,
      lastName,
      country,
      state,
      stateGroup,
      postalCode,
      postalCodeGroup,
      phone,
      email,
      commentGroup,
      commentInput,
      hiddenFieldsContainer,
    } = this.elements;

    const hiddenFields = {};
    if (hiddenFieldsContainer) {
      const hiddenInputs = hiddenFieldsContainer.querySelectorAll(
        'input[type="hidden"]'
      );
      hiddenInputs.forEach((input) => {
        hiddenFields[input.name] = input.value;
      });
    }

    return {
      firstName: firstName.value,
      lastName: lastName.value,
      country: country.options[country.selectedIndex].text,
      state: state && stateGroup.style.display !== "none" ? state.value : "N/A",
      postalCode:
        postalCode && postalCodeGroup.style.display !== "none"
          ? postalCode.value
          : "N/A",
      phone: phone.value,
      email: email.value,
      comments:
        commentGroup.style.display !== "none" ? commentInput.value : "N/A",
      visitReason:
        this.form.querySelector('input[name="visitReason"]:checked')?.value ||
        "Not selected",
      hiddenFields: hiddenFields,
    };
  }

  scrollToFirstError() {
    const { error } = RequestInformation.CSS_CLASSES;
    const firstError = this.form.querySelector(`.form-group.${error}`);
    if (firstError) {
      firstError.scrollIntoView({ behavior: "smooth", block: "center" });
    }
  }
}

registerComponent("form-page-hero", RequestInformation);

export default RequestInformation;

