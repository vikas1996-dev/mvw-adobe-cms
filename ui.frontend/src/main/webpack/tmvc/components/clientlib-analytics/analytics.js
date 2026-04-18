(function() {
    "use strict";

    if (window.__mvwAnalyticsInitialized) return;
    window.__mvwAnalyticsInitialized = true;

    const dataLayerName = "adobeDataLayer";
    let lastEventSignature = "";
    let lastEventTime = 0;


    const SELECTORS = {
        phoneCTAs: ".cta-phone, [data-cta-type='phone']",
        clickable: "a, button",
        logoClick: "[class*='logo'] a",
        cardLink: ".card-link",
        utilityHeaderLink: ".utility-header .link",
        ownerLoginLink: ".owner-login-dropdown .login-list a",
        navMainLink: ".nav-items > .nav-item > a",
        navChildLink: ".sub-nav a.child-link",
        promoImageLink: ".promo .promo-image-container > a",
        phoneLink: ".phone-link",
        form: "form",
        formFields: "input, select, textarea",
        resortRoot: ".resort-list-cmp[data-mod='resort-list']",
        resortFilterGroup: ".filter-group[data-filter-type]",
        resortFilterCheckbox: ".filter-options input[type='checkbox']",
        resortActiveFilters: ".active-filters",
        resortSortContainers: "#sort-container, #sort-container-mobile",
        resortSortItem: "#sort-container .dropdown-item[role='option'], #sort-container-mobile .dropdown-item[role='option']",
        resortCardLink: ".resort-card a.view-resort",
        offerPrimaryCta: ".offer-primary-cta",
        socialLinks: "a[href*='facebook.com'], a[href*='instagram.com'], a[href*='twitter.com'], a[href*='x.com'], a[href*='pinterest.com'], a[href*='linkedin.com'], a[href*='youtube.com']",
    };

    const EVENTS = {
        NAV_CLICK: "navClick",
        PAGE_LOAD: "page-load",
        FORM_START: "formStart",
        FORM_SUBMIT: "formSubmit",
        RESORT_FILTERS: "resort_filters",
        SORT_APPLIED: "sortApplied",
        DESTINATION_CLICK: "destinationClick",
    };

    const qs = (sel, root = document) => root.querySelector(sel);
    const qsa = (sel, root = document) => Array.from(root.querySelectorAll(sel));
    const closest = (el, sel) => (el ? el.closest(sel) : null);
    const trim = (v) => (v || "").toString().trim();

    function initDestinationAnalytics() {

        const root = document.querySelector(".destination-landing");
        if (!root) return;

        root.addEventListener("click", function(event) {

            const regionBtn = event.target.closest(".filter-pills button");
            if (regionBtn) {
                const regionName = regionBtn.textContent.trim();
                const count = root.querySelectorAll(".destination-card").length;

                pushToDataLayer({
                    event: EVENTS.DESTINATION_CLICK,
                    pageInfo: getPageInfo(),
                    destinationInfo: {
                        destinationRegion: regionName,
                        destinationCount: count
                    }
                });

                return;
            }

            const cardLink = event.target.closest(".destination-card a");
            if (cardLink) {
                const card = cardLink.closest(".destination-card");

                pushToDataLayer({
                    event: EVENTS.DESTINATION_CLICK,
                    pageInfo: getPageInfo(),
                    destinationInfo: {
                        destinationRegion: card?.dataset.region || "",
                        destinationName: card?.dataset.name || cardLink.textContent.trim(),
                        destinationCount: root.querySelectorAll(".destination-card").length
                    }
                });

                return;
            }

            const viewAll = event.target.closest(".btn-view-all");
            if (viewAll) {

                const activeRegion = root.querySelector(".filter-pills .active")?.textContent.trim() || "";

                pushToDataLayer({
                    event: EVENTS.DESTINATION_CLICK,
                    pageInfo: getPageInfo(),
                    destinationInfo: {
                        destinationRegion: activeRegion,
                        destinationViewAll: activeRegion + " Resorts"
                    }
                });
            }
        });
    }

    function onReady(fn) {
        if (document.readyState === "loading") {
            document.addEventListener("DOMContentLoaded", fn);
        } else {
            fn();
        }
    }

    function getComponentName(element) {
        return closest(element, "[data-parent-component]")?.dataset.parentComponent || "";
    }

    function getPageInfo() {

        const body = document.body;

        const brand =
            body.dataset.brand ||
            window.brand ||
            "";

        const pathParts = window.location.pathname.split("/").filter(Boolean);

        const siteSection =
            window.siteSection ||
            pathParts[pathParts.length - 2] ||
            "";
            if (window.location.pathname.includes("request-information-thank-you")) {
                siteSection = "Request Information";
            }
        const page = pathParts[pathParts.length - 1]?.replace(".html", "") || "";

let computedPageName =
    window.pageName ||
    [brand, siteSection, page].filter(Boolean).join(":");


if (window.location.pathname.includes("request-information-thank-you")) {
    computedPageName = `${brand}:requestInformationThankYou`;
}
        let pageType =
            body.dataset.cmpTemplate ||
            window.pageType ||
            "";

        if (pageType === "resort-page-template") {
            pageType = "resort-details-page-template";
        }

        return {
            pageName: computedPageName,
            pageUrl: document.location.href,
            pageTitle: document.title,
            pageType,
            siteSection,
            subSection: body.dataset.subSection || window.subSection || "",
            formLoc: body.dataset.locCode || window.locCode || "",
            previousPageName: window.previousPageName || sessionStorage.getItem("previousPageName") || "",
            referrer: document.referrer
        };
    }

    function getStableClassName(el) {
        if (!el) return;
        const clickable = closest(el, SELECTORS.clickable) || el;

        const cls = Array.from(clickable.classList || []).filter((c) => {
            if (!c) return false;
            if (c.startsWith("is-")) return false;
            if (c.startsWith("has-")) return false;
            if (c.startsWith("js-")) return false;
            if (c.includes("active")) return false;
            if (c.includes("open")) return false;
            if (c.includes("selected")) return false;
            if (c.includes("focus")) return false;
            return true;
        });

        const ignore = ["cmp-button", "button", "btn", "link", "cta"];
        const best = cls.find((c) => !ignore.includes(c)) || cls[0];
        return best || undefined;
    }

    function getLinkName(el) {

        const href = el.getAttribute("href") || "";

        if (isSocialLink(el)) {

            if (href.includes("facebook")) return "facebook";
            if (href.includes("instagram")) return "instagram";
            if (href.includes("youtube")) return "youtube";
            if (href.includes("linkedin")) return "linkedin";
            if (href.includes("pinterest")) return "pinterest";
            if (href.includes("twitter") || href.includes("x.com")) return "x";

        }

        const dataName = el.getAttribute("data-link-name");
        if (dataName) return trim(dataName);

        const ariaLabel = el.getAttribute("aria-label");
        if (ariaLabel) return trim(ariaLabel);

        const textContent = trim(el.textContent);
        if (textContent && textContent.length < 100) return textContent;

        const img = el.querySelector("img");
        if (img?.alt) return trim(img.alt);

        return getStableClassName(el);
    }

    function isExternalLink(url) {
        if (!url) return false;
        try {
            const urlObj = new URL(url, window.location.href);
            return urlObj.hostname !== window.location.hostname;
        } catch {
            return false;
        }
    }

    function isSocialLink(el) {
        const href = el.getAttribute("href") || "";
        const socialDomains = ["facebook.com", "instagram.com", "twitter.com", "x.com", "pinterest.com", "linkedin.com", "youtube.com"];
        return socialDomains.some(domain => href.includes(domain));
    }

    function getLinkType(el) {
        const href = el.getAttribute("href") || "";
        if (href.startsWith("tel:") || el.getAttribute("data-cta-type") === "phone") {
            return "Phone Link";
        }
        if (isSocialLink(el)) {
            return "Social Link";
        }
        if (isExternalLink(href)) {
            return "External Link";
        }
        if (el.getAttribute("data-cta-type") === "video") {
            return "Video";
        }
        const componentName = getComponentName(el);
        if ((componentName || "").toLowerCase() === "header") {
            if (closest(el, ".nav-items > .nav-item")) {
                return "Main Level Navigation";
            }
            if (closest(el, ".sub-nav")) {
                return "Sub Level Navigation";
            }
        }
        if (el.classList.contains("submit-btn") || el.type === "submit") {
            return "Form Submit";
        }
        if (closest(el, "[class*='logo']")) {
            return "Logo Click";
        }
        if (el.classList.contains("card-link")) {
            return "Card Link";
        }
        if (closest(el, ".utility-header")) {
            return "Utility Link";
        }
        if (closest(el, ".owner-login-dropdown")) {
            return "Login Link";
        }
        if (closest(el, ".promo")) {
            return "Promo";
        }
        return "Internal Navigation";
    }

    function getRegionFromSection(el) {

        const diningSection = el.closest(".resort-dining-sec");

        if (diningSection) {
            const heading = diningSection.querySelector("h2, .cmp-title__text");
            if (heading) {
                return heading.textContent.trim();
            }
        }

        return getComponentName(el) || "";
    }

    function getDynamicCardName(el) {
        const card = el.closest(".service-card, .dining-card");

        if (!card) return getLinkName(el);

        return (
            card.dataset.title ||
            card.querySelector("h3, h4")?.textContent.trim() ||
            getLinkName(el)
        );
    }


    function getPhoneImpressionInfo() {
        const phoneEl = qs(SELECTORS.phoneCTAs);
        if (!phoneEl) return null;

        const phoneData = trim(phoneEl.getAttribute("data-phone"));
        const href = trim(phoneEl.getAttribute("href"));
        const phoneText = trim(phoneEl.textContent);

        let phoneValue = "";

        if (href.startsWith("tel:")) phoneValue = trim(href.replace("tel:", ""));
        else if (phoneData) phoneValue = phoneData;
        else if (phoneText) phoneValue = phoneText;

        if (!phoneValue) return null;

        return {
            phoneVisible: true,
            phoneNumber: phoneValue,
        };
    }

    function getFormInfo(form) {

        const formType =
            form?.getAttribute("data-form-type") ||
            form?.getAttribute("data-analytics-id") ||
            "";

        let formId = "";
        let formName = "";

        if (formType.includes("owner")) {
            formId = "contact-us-form";
            formName = "Contact Us";
        } else if (formType.includes("purchase")) {
            formId = "contact-us-form";
            formName = "Contact Us";
        } else if (formType.includes("request")) {
            formId = "request-information-form";
            formName = "Request Information";
        } else {
            formId = "special-offers-form";
            formName = "Special offers";
        }

        return {
            formId,
            formName,
            formType,
            componentName: getComponentName(form),
        };
    }

    function getFormFields(form) {

        const fields = {};
        if (!form) return fields;

        const elements = qsa(SELECTORS.formFields, form);

        elements.forEach((el) => {

            const name = el.name || el.id;
            if (!name) return;

            const type = (el.type || "").toLowerCase();
            if (type === "password") return;

            const hasValue = trim(el.value).length > 0;

            if (name === "firstName" || name === "first-name") {
                fields.firstName = hasValue;
            } else if (name === "lastName" || name === "last-name") {
                fields.lastName = hasValue;
            } else if (name === "country") {
                fields.country = hasValue;
            } else if (name === "phone" || name === "phoneNumber") {
                fields.contactProvided = hasValue;
            } else if (name === "email") {
                fields.emailProvided = hasValue;
            }

        });

        return fields;
    }

    function pushToDataLayer(data, callback) {
        if (!window[dataLayerName]) {
            callback?.();
            return;
        }


        if (data.event === EVENTS.PAGE_LOAD) {
            const alreadyFired = window[dataLayerName].some(
                (item) => item && item.event === EVENTS.PAGE_LOAD
            );
            if (alreadyFired) {
                callback?.();
                return;
            }
        }

        const payloadSignature = JSON.stringify(data);
        const now = Date.now();

        if (
            payloadSignature === lastEventSignature &&
            now - lastEventTime < 1500
        ) {
            return;
        }

        lastEventSignature = payloadSignature;
        lastEventTime = now;

        window[dataLayerName].push(data);

        if (typeof callback === "function") {
            requestAnimationFrame(callback);
        }
    }



    function pushNavEvent({
            linkName,
            linkType,
            linkUrl,
            region,
            subSection
        },
        callback
    ) {
        const pageInfo = getPageInfo();

        if (linkUrl) {
            try {
                const url = new URL(linkUrl, window.location.origin);
                const parts = url.pathname.split("/").filter(Boolean);

                const page = parts[parts.length - 1].replace(".html", "");
                const section = parts[parts.length - 2] || "";

                pageInfo.pageName = `tmvcs:${section}:${page}`;
                pageInfo.siteSection = section;

            } catch(e) {}
        }

        if (subSection) {
            pageInfo.subSection = subSection;
        }

        const linkInfo = {};

        if (linkName) linkInfo.linkName = linkName;
        if (linkType) linkInfo.linkType = linkType;

        if (linkName) {
            linkInfo.linkName = linkName;

            if (linkType === "Internal Navigation") {
                linkInfo.linkType = linkType;
                linkInfo.linkclickRegion = "Content/Info";
            } else {
                linkInfo.linkType = linkType;
            }
        }

        if (linkUrl) {

            const fullUrl = new URL(linkUrl, window.location.origin).href;

            linkInfo.linkUrl = fullUrl;

            linkInfo.source = fullUrl;
        }
        if (window.s) {

            s.linkTrackVars = "eVar137,eVar139,prop21";

            if (linkUrl) s.eVar137 = linkUrl;
            if (linkName) s.eVar139 = linkName;
            if (region) s.prop21 = region;

        }


        pushToDataLayer({
            event: EVENTS.NAV_CLICK,
            pageInfo,
            linkInfo,
        }, callback);
    }

    function initPhoneNumbers() {
        const phoneCTAs = qsa(SELECTORS.phoneCTAs);

        phoneCTAs.forEach((link) => {
            const phoneNumber = link.getAttribute("data-phone");
            if (!phoneNumber) return;

            const currentHref = link.getAttribute("href");

            if (currentHref && currentHref.includes("{{phoneNumber}}")) {
                link.setAttribute("href", currentHref.replace("{{phoneNumber}}", phoneNumber));
            } else if (!currentHref || currentHref === "#") {
                link.setAttribute("href", "tel:" + phoneNumber);
            }

            if (link.innerHTML && link.innerHTML.includes("{{phoneNumber}}")) {
                link.innerHTML = link.innerHTML.replace(/\{\{phoneNumber\}\}/g, phoneNumber);
            }
        });
    }

    function createSectionClickConfig(selector, useDynamicCardName = false) {
        return {
            selector,
            getName: (el) =>
                useDynamicCardName ? getDynamicCardName(el) : getLinkName(el),
            getUrl: (el) => el.getAttribute("href"),
            getType: (el) => getLinkType(el),
            getRegion: (el) => getRegionFromSection(el),
        };
    }

    const CLICK_MAP = [
        createSectionClickConfig(
            ".planning-visit-cmp a, .planning-visit-cmp button"
        ),


        createSectionClickConfig(
            ".service-card .viewmore-link, .service-card a.button-with-icon",
            true
        ),

        createSectionClickConfig(
            ".dining-card a, .dining-card button",
            true
        ),

        {
            selector: ".cmp-button",
            getName: (el) => getLinkName(el),
            getUrl: (el) => {
                const link = el.closest("a");
                const href = link ? link.getAttribute("href") : null;
                return href ? new URL(href, window.location.origin).href : null;
            },
            getType: (el) => getLinkType(el),
            getRegion: (el) => getComponentName(el),
        },
        {
            selector: SELECTORS.logoClick,
            getName: (el) => getLinkName(el),
            getUrl: (el) => el.getAttribute("href"),
            getType: () => "Logo Click",
            getRegion: (el) => getComponentName(el),
            isLogo: true,
        },
        {
            selector: SELECTORS.cardLink,
            getName: (el) => getLinkName(el),
            getUrl: (el) => el.getAttribute("href"),
            getType: (el) => getLinkType(el),
            getRegion: (el) => getComponentName(el),
        },
        {
            selector: SELECTORS.utilityHeaderLink,
            getName: (el) => getLinkName(el),
            getUrl: (el) => el.getAttribute("href"),
            getType: (el) => getLinkType(el),
            getRegion: (el) => getComponentName(el),
        },
        {
            selector: SELECTORS.ownerLoginLink,
            getName: (el) => getLinkName(el),
            getUrl: (el) => el.getAttribute("href"),
            getType: (el) => getLinkType(el),
            getRegion: (el) => getComponentName(el),
        },
        {
            selector: SELECTORS.navMainLink,
            getName: (el) => getLinkName(el),
            getUrl: (el) => el.getAttribute("href"),
            getType: (el) => getLinkType(el),
            getRegion: (el) => getComponentName(el),
        },
        {
            selector: SELECTORS.navChildLink,
            getName: (el) => getLinkName(el),
            getUrl: (el) => el.getAttribute("href"),
            getType: (el) => getLinkType(el),
            getRegion: (el) => getComponentName(el),
        },
        {
            selector: SELECTORS.promoImageLink,
            getName: (el) => getLinkName(el),
            getUrl: (el) => el.getAttribute("href"),
            getType: (el) => getLinkType(el),
            getRegion: (el) => getComponentName(el),
        },

        {
            selector: ".detail-popup__btn",
            getName: () => "Details of Participation",
            getUrl: () => null,
            getType: () => "Call To Action",
            getRegion: (el) => getComponentName(el),
        },
        {
            selector: SELECTORS.socialLinks,
            getName: (el) => getLinkName(el),
            getUrl: (el) => el.getAttribute("href"),
            getType: () => "Social Link",
            getRegion: (el) => getComponentName(el),
        },

    ];

    function getContactUsSubSection(element) {

        const step = element.closest(".contact-us-form__step")?.dataset.step;

        if (step === "1") return "Select";

        if (element.closest(".contact-us-form__accordion-item")) {
            return "How can we help you today";
        }
        const form = element.closest("form");
        const formType = form?.getAttribute("data-form-type") || "";

        if (formType.includes("owner")) return "OwnerServices";
        if (formType.includes("purchase")) return "VacationSpecialist";
        if (formType.includes("request")) return "RequestInformation";

        if (element.closest(".contact-us-form__sidebar")) {
            return "LogIn";
        }

        return "";
    }

    function getAccordionLinkName(element) {
        const titleEl = element.querySelector(".contact-us-form__card-title") ||
            element.closest(".contact-us-form__accordion-item")?.querySelector(".contact-us-form__card-title");

        if (!titleEl) return getLinkName(element);

        return titleEl.textContent.trim().split("\n")[0].trim();
    }

    function initClickTracking() {
        document.addEventListener("click", function(event) {

            if (event.__navAnalyticsHandled) return;
            const articleLink = closest(event.target, ".editorial-carousel .card-link");

            if (articleLink) {

                let articleName = getLinkName(articleLink);

                articleName = articleName.replace(/^Read the article about\s*/i, "").trim();

                pushToDataLayer({
                    event: "articleClick",
                    pageInfo: getPageInfo(),
                    articleInfo: {
                        articleName: articleName
                    }
                });

                return;
            }
            const seeOfferBtn = event.target.closest(".offer-primary-cta");

            if (seeOfferBtn && seeOfferBtn.closest(".offers-slider, .offers-carousel, .offers-cards")) {

                event.__navAnalyticsHandled = true;

                pushNavEvent({
                    linkName: "Learn How",
                    linkType: "Internal Navigation",
                    linkUrl: seeOfferBtn.getAttribute("href"),
                    region: "Content/Info"
                });

                return;
            }
            if (seeOfferBtn) {

                event.__navAnalyticsHandled = true;

                pushNavEvent({
                    linkName: "Learn How",
                    linkType: "Internal Navigation",
                    linkUrl: seeOfferBtn.getAttribute("href"),
                    region: "Content/Info"
                });

                return;
            }
            const offerCta = closest(event.target, ".offer-card a[href]");
            const resortCard = closest(event.target, SELECTORS.resortCardLink);
            if (resortCard && !offerCta) {
                pushResortClick(resortCard);
                return;
            }
            if (offerCta) {
                pushOfferCtaClick(offerCta);
                return;
            }

            const phoneLink = closest(event.target, SELECTORS.phoneLink);
            if (phoneLink && (window.siteSection || "") === "Special Offers") {

                event.__navAnalyticsHandled = true;

                pushNavEvent({
                    linkName: getLinkName(phoneLink),
                    linkType: "Phone Link",
                    linkUrl: phoneLink.getAttribute("href"),
                });
                return;
            }
            const loadMoreBtn = closest(event.target, ".load-more-btn");

            if (loadMoreBtn) {

                event.__navAnalyticsHandled = true;

                pushNavEvent({
                    linkName: getLinkName(loadMoreBtn),
                    linkType: getLinkType(loadMoreBtn),
                    linkUrl: window.location.href
                });
                return;
            }
           const accordionItem = event.target.closest(".contact-us-form__accordion-item");
           const isHeaderClick = event.target.closest(".contact-us-form__accordion-header");

           if (
               accordionItem &&
               isHeaderClick &&
               accordionItem.closest(".contact-us-form") &&
               !event.__navAnalyticsHandled
           ) {
               event.__navAnalyticsHandled = true;

               const accordionBtn = accordionItem.querySelector(".contact-us-form__accordion-header");

               pushNavEvent({
                   linkName: getAccordionLinkName(accordionBtn),
                   linkType: getLinkType(accordionBtn),
                   linkUrl: window.location.href,
                   subSection: "How can we help you today"
               });

               return;
           }
            const contactUsBtn = event.target.closest([
                ".contact-us-form__option-btn",
                ".contact-us-form__accordion-header",
                ".contact-us-form__sidebar a",
                ".contact-us-form__speak-btn",
                ".contact-us-form__learn-cta-group a",
                ".contact-us-form .cmp-button"
            ].join(", "));

            if (contactUsBtn && contactUsBtn.closest(".contact-us-form")) {

                event.__navAnalyticsHandled = true;

                pushNavEvent({
                    linkName: getAccordionLinkName(contactUsBtn),
                    linkType: getLinkType(contactUsBtn),
                    linkUrl: contactUsBtn.getAttribute("href") || window.location.href,
                    subSection: getContactUsSubSection(contactUsBtn)
                });

                return;
            }

            const viewResortLink = closest(event.target, ".resort-actions .view-resort");

            if (viewResortLink) {
                pushResortClick(viewResortLink);
                return;
            }
            const offerLearnBtn = closest(event.target, ".offer-book-btn");

            if (offerLearnBtn) {

                event.__navAnalyticsHandled = true;

                pushNavEvent({
                    linkName: getLinkName(offerLearnBtn),
                    linkType: getLinkType(offerLearnBtn),
                    linkUrl: offerLearnBtn.getAttribute("href")
                });

                return;
            }
            for (const item of CLICK_MAP) {
                const element = closest(event.target, item.selector);
                if (!element) continue;

                const rawUrl = item.getUrl(element);

                const url =
                    rawUrl && rawUrl !== "#" && !rawUrl.startsWith("javascript") ?
                    new URL(rawUrl, window.location.origin).href :
                    null;
                const linkRegion = item.getRegion ? item.getRegion(element) : getComponentName(element);

                const navigateCallback = () => {
                    if (
                        url &&
                        url !== "#" &&
                        element.tagName !== "A" &&
                        !element.hasAttribute("target")
                    ) {
                        setTimeout(() => {
                            window.location.href = url;
                        }, 120);
                    }
                };

                if (item.isLogo && (linkRegion || "").toLowerCase() === "header") {
                    firePageLoadAnalytics(navigateCallback);
                    return;
                }

                event.__navAnalyticsHandled = true;

                pushNavEvent({
                    linkName: item.getName(element),
                    linkType: item.getType(element),
                    linkUrl: url,
                    region: linkRegion,
                }, navigateCallback);
                return;
            }


        });
        document.addEventListener("click", function(e) {

                    const accordionBtn = e.target.closest(".contact-us-form__accordion-header");
                    if (!accordionBtn || !accordionBtn.closest(".contact-us-form")) return;

                    pushNavEvent({
                        linkName: getAccordionLinkName(accordionBtn),
                        linkType: getLinkType(accordionBtn),
                        linkUrl: window.location.href,
                        subSection: getContactUsSubSection(accordionBtn)
                    });

                }, true);

    }

    function initFormTracking() {

        document.addEventListener("focusin", function(e) {

            if (!e.target.matches("input, select, textarea")) return;

            if (e.target.matches("input[type='radio']")) return;
            const form = closest(e.target, SELECTORS.form);
            if (!form) return;

            if (form.dataset.analyticsStarted === "true") return;
            form.dataset.analyticsStarted = "true";

            const pageInfo = getPageInfo();
            pageInfo.subSection = getContactUsSubSection(form);

            const {
                formId,
                formName
            } = getFormInfo(form);

            pushToDataLayer({
                event: EVENTS.FORM_START,
                pageInfo,
                formInfo: {
                    formId,
                    formName
                }
            });

        });

       document.addEventListener("change", function(e) {

           if (!e.target.matches("input[type='radio']")) return;

           const form = closest(e.target, SELECTORS.form);
           if (!form) return;

           form.dataset.analyticsStarted = "true";

           const pageInfo = getPageInfo();
           pageInfo.subSection = getContactUsSubSection(form);

           const { formId, formName } = getFormInfo(form);

           const selectedRadioId = e.target.id;

           if (window.s) {
               s.linkTrackVars = "eVar72";
               s.eVar72 = selectedRadioId;
               s.tl(true, "o", "Form Start");
           }

           pushToDataLayer({
               event: EVENTS.FORM_START,
               pageInfo,
               formInfo: {
                   formId,
                   formName,
                   formStep: "form-start",
                   formType: form?.getAttribute("data-form-type") || "",
                   formFields: selectedRadioId
               }
           });

       });
       document.addEventListener("submit", function(e) {

           const form = e.target;
           if (!form.matches("form")) return;
           const pageInfo = getPageInfo();
           pageInfo.subSection = getContactUsSubSection(form);
           const selectedRadio = form.querySelector("input[type='radio']:checked");
           const selectedRadioId = selectedRadio ? selectedRadio.id : "";

           pushToDataLayer({
               event: EVENTS.FORM_SUBMIT,
               pageInfo,
               formInfo: {
                   ...getFormInfo(form),
                   formStep: "form-complete",
                   formFields: {
                       contactReason: selectedRadioId,
                       ...getFormFields(form)
                   }
               }
           });
           }, true);

}

    function getResortRoot() {
        return qs(SELECTORS.resortRoot);
    }

    function getFilterValuesForType(resortRoot, filterType) {
        const group = qs(`.filter-group[data-filter-type="${filterType}"]`, resortRoot);
        if (!group) return "";

        const checked = qsa(`.filter-options input[type="checkbox"]:checked`, group);
        return checked
            .map((cb) => {
                const label = closest(cb, "label");
                const labelText = trim(label?.textContent);
                return labelText || trim(cb.value);
            })
            .filter(Boolean)
            .join("|");
    }

    function getResortFiltersInfo(resortRoot) {
        return {
            filterRegion: getFilterValuesForType(resortRoot, "region"),
            filtervacationType: getFilterValuesForType(resortRoot, "vacationType"),
            filterresortBrand: getFilterValuesForType(resortRoot, "brand"),
            filteractivities: getFilterValuesForType(resortRoot, "activities"),
        };
    }

    function pushResortFilters(resortRoot) {
        pushToDataLayer({
            event: EVENTS.RESORT_FILTERS,
            filtersInfo: getResortFiltersInfo(resortRoot),
            pageInfo: getPageInfo(),
        });
    }

    function pushSortApplied(sortText) {
        pushToDataLayer({
            event: EVENTS.SORT_APPLIED,
            sortInfo: {
                sortOption: sortText || ""
            },
            pageInfo: getPageInfo(),
        });
    }

    function pushResortClick(el) {

        const link = el.closest("a");
        if (!link) return;

        const card = link.closest(".resort-card");

        const resortName =
            card?.querySelector(".resort-title")?.textContent.trim() ||
            link.getAttribute("data-resort-name") ||
            "";

        const resortId =
            link.getAttribute("data-resort-id") ||
            link.pathname.split("/").filter(Boolean).pop();

        pushToDataLayer({
            event: "resortClick",
            pageInfo: getPageInfo(),
            resortInfo: {
                resortName: resortName,
                resortId: resortId
            }
        });
    }

    function pushOfferCtaClick(el) {
        const linkName = getLinkName(el);
        const linkUrl = el.getAttribute("href") || "";

        pushNavEvent({
            linkName,
            linkType: getLinkType(el),
            linkUrl
        });
    }

    function initResortsAnalytics() {
        const resortRoot = getResortRoot();
        if (!resortRoot) return;

        resortRoot.addEventListener(
            "change",
            (e) => {
                const cb = closest(e.target, SELECTORS.resortFilterCheckbox);
                if (!cb) return;
                setTimeout(() => pushResortFilters(resortRoot), 0);
            }
        );

        resortRoot.addEventListener(
            "click",
            (e) => {
                const btn = closest(e.target, ".clear-filters");
                if (!btn) return;
                setTimeout(() => pushResortFilters(resortRoot), 0);
            }
        );

        resortRoot.addEventListener(
            "click",
            (e) => {
                const opt = closest(e.target, SELECTORS.resortSortItem);
                if (!opt) return;
                const sortText = trim(opt.textContent);
                if (!sortText) return;
                setTimeout(() => pushSortApplied(sortText), 0);
            }
        );
    }

    function firePageLoadAnalytics(callback) {
        callback?.();
    }

    onReady(() => {

        if (!window.__pageLoadFired && !window.__mvwPageLoadFired) {
            window.__pageLoadFired = true;
            window.__mvwPageLoadFired = true;
            pushToDataLayer({
                event: EVENTS.PAGE_LOAD,
                pageInfo: getPageInfo()
            });
        }

        initPhoneNumbers();
        initClickTracking();
        initFormTracking();
        initResortsAnalytics();
        initDestinationAnalytics();
    });
})();