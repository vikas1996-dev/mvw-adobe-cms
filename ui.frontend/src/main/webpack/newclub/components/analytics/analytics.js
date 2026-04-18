// clientlib: /apps/your-site/clientlibs/clientlib-analytics/js/click-tracking.js

// ---------------------------------------------------------------------
// 1) KEEP ONLY *CUSTOM* ADOBE DATA LAYER PUSHES
//    - Drops ALL component state pushes: { component: {...} }
//    - Drops ALL Core Components events: { event: "cmp:*" }
//    - OPTIONAL (enabled below): drops ANY event not in CUSTOM_EVENT_ALLOWLIST
//
// IMPORTANT:
// - If OOTB pushes happened BEFORE this file loads, they may already be in the array.
//   This script also cleans existing entries immediately when it patches the DL.
// ---------------------------------------------------------------------
(function keepOnlyCustomDataLayerPushes() {
  const DL_NAME = "adobeDataLayer";
  const CMP_PREFIX = "cmp:";

  // ✅ Put ALL your custom event names here (from your click-tracking payloads, page-load events, etc.)
  const CUSTOM_EVENT_ALLOWLIST = new Set([
    "ClickTracking",
    "Logo Click",
    "Footer Link Click",
    "docs page load"
    // add more if you have them
  ]);

  // ✅ If true, ONLY allow payloads whose `event` is in allowlist (plus non-event primitives/functions)
  // If false, we still drop {component:{...}} and cmp:* but allow other non-cmp events through.
  const STRICT_CUSTOM_ONLY = true;

  function isPlainObject(v) {
    return v && typeof v === "object" && !Array.isArray(v);
  }

  function eventNameOf(item) {
    return (isPlainObject(item) && typeof item.event === "string") ? item.event.trim() : "";
  }

  function isComponentStatePayload(item) {
    // Matches your sample:
    // { component: { "carousel-...": { "@type": "...", ... } } }
    return isPlainObject(item) && isPlainObject(item.component);
  }

  function isCmpEventPayload(item) {
    const ev = eventNameOf(item).toLowerCase();
    return ev.startsWith(CMP_PREFIX);
  }

  function isNonCustomEventPayload(item) {
    if (!STRICT_CUSTOM_ONLY) return false;

    // If it has an event string, it must be in allowlist.
    const ev = eventNameOf(item);
    if (!ev) return false; // payloads without `event` are handled by other rules (component/state already dropped)
    return !CUSTOM_EVENT_ALLOWLIST.has(ev);
  }

  function shouldDrop(item) {
    return (
      isComponentStatePayload(item) ||
      isCmpEventPayload(item) ||
      isNonCustomEventPayload(item)
    );
  }

  function cleanExistingArray(dl) {
    // Remove existing unwanted entries (only works when dl is an array-like object)
    try {
      for (let i = dl.length - 1; i >= 0; i--) {
        const entry = dl[i];
        if (shouldDrop(entry)) dl.splice(i, 1);
        // If an entry is an array of items, filter inside too
        else if (Array.isArray(entry)) {
          const kept = entry.filter((x) => !shouldDrop(x));
          if (kept.length) dl[i] = kept;
          else dl.splice(i, 1);
        }
      }
    } catch (_) {
      console.log("ignore: adobeDataLayer existing cleanup failed");
    }
  }

  function patchDataLayer(dl) {
    if (!dl || dl.__customOnlyPatched) return;
    dl.__customOnlyPatched = true;

    // Clean anything already pushed before patch
    cleanExistingArray(dl);

    const originalPush = dl.push && dl.push.bind ? dl.push.bind(dl) : null;
    if (!originalPush) return;

    dl.push = function (...args) {
      const filteredArgs = [];

      for (const arg of args) {
        // Keep functions (adobeDataLayer.push(fn) pattern)
        if (typeof arg === "function") {
          filteredArgs.push(arg);
          continue;
        }

        // Filter arrays (adobeDataLayer.push([obj1, obj2]))
        if (Array.isArray(arg)) {
          const kept = arg.filter((item) => !shouldDrop(item));
          if (kept.length) filteredArgs.push(kept);
          continue;
        }

        // Drop unwanted objects
        if (shouldDrop(arg)) continue;

        // Keep everything else (events that pass allowlist, primitives, etc.)
        filteredArgs.push(arg);
      }

      if (!filteredArgs.length) return;
      return originalPush(...filteredArgs);
    };
  }

  // Ensure it exists and patch immediately
  window[DL_NAME] = window[DL_NAME] || [];
  patchDataLayer(window[DL_NAME]);

  // Re-patch if Adobe client data layer replaces the object later
  try {
    let current = window[DL_NAME];
    Object.defineProperty(window, DL_NAME, {
      configurable: true,
      get() {
        return current;
      },
      set(v) {
        current = v;
        patchDataLayer(current);
      }
    });
  } catch (_) {
    window.addEventListener("load", () => patchDataLayer(window[DL_NAME]));
  }
})();


// ---------------------------------------------------------------------
// 2) RULE-BASED CLICK TRACKING (your custom events)
// ---------------------------------------------------------------------
(function initRuleBasedClickTracking() {
  const TRACKED_FLAG = "__analyticsTracked";
  const SKIP_ATTR = "data-analytics-skip";

  const RULES = [
    {
      selector: ".header_logo",
      getEvent: () => "Logo Click",
      getName: () => "Logo Icon",
      getRegion: () => "Main Level Navigation",
      getDestinationPage: (el) => el.getAttribute("href") || el.dataset.url || null
    },
    {
      selector: ".header-navigation",
      getEvent: () => "ClickTracking",
      getName: el => el.innerText.trim(),
      getRegion: el => "Main Level Navigation",
      getDestinationPage: el => el.getAttribute("href") || el.dataset.url
    },
    {
      selector: "#social-icon-link, .social-icon-link",
      getEvent: () => "ClickTracking",
      getName: (el) => (el.getAttribute("aria-label") || "").trim() || "",
      getRegion: () => "Social Link",
      getDestinationPage: (el) => el.getAttribute("href") || el.href || ""
    },
    {
      selector: "#footer-logo, .footer-logo",
      getEvent: () => "Logo Click",
      getName: (el) => (el.getAttribute("aria-label") || "").trim() || "",
      getRegion: () => "Footer",
      getDestinationPage: (el) => el.getAttribute("href") || el.href || ""
    },
    {
      selector: "#footer-legal-link, .footer-legal-link",
      getEvent: () => "Footer Link Click",
      getName: (el) => (el.getAttribute("aria-label") || "").trim() || "",
      getRegion: () => "Footer",
      getDestinationPage: (el) => el.getAttribute("href") || el.href || ""
    },
    {
      selector: ".video-js",
      getEvent: () => "ClickTracking",
      getName: () => "Play",
      getRegion: () => "Video",
      getDestinationPage: (el) => {
        const videoEl = el.querySelector("video");
        if (!videoEl) return null;
        return videoEl.src || videoEl.getAttribute("src");
      }
    },
    {
      selector: ".resort-carousel .cmp-teaser__link",
      getEvent: () => "ClickTracking",
      getName: el => el.querySelector(".cmp-teaser__title").textContent.trim(),
      getRegion: el => "Resort Details",
      getDestinationPage: el => el.getAttribute("href") || el.dataset.url
    },
    {
      selector: ".resort-promo__link",
      getEvent: () => "ClickTracking",
      getName: el => el.innerText.trim(),
      getRegion: el => "Resort Promotion",
      getDestinationPage: el => el.getAttribute("href") || el.dataset.url
    },
    {
      selector: ".nav-link",
      getEvent: () => "ClickTracking",
      getName: el => el.getAttribute("aria-label") || el.innerText.trim(),
      getRegion: el => "Resort Promotion",
      getDestinationPage: el => el.getAttribute("href") || el.dataset.url
    },
    // NOTE: you had duplicate ".nav-link" rule; left as-is, but you may want to remove one.
    {
      selector: ".nav-link",
      getEvent: () => "ClickTracking",
      getName: el => el.getAttribute("aria-label") || el.innerText.trim(),
      getRegion: el => "Resort Promotion",
      getDestinationPage: el => el.getAttribute("href") || el.dataset.url
    },
    {
      selector: ".footer-logo",
      getEvent: () => "ClickTracking",
      getName: el => el.getAttribute("aria-label") || el.innerText.trim(),
      getRegion: el => "Footer Logo",
      getDestinationPage: el => el.getAttribute("href") || el.dataset.url
    }, {
      selector: "#tpm-acknowledge-btn",
      getEvent: () => "ClickTracking",
      getName: el => el.getAttribute("aria-label") || el.innerText.trim(),
      getRegion: el => "Call to Action",
      getDestinationPage: el => {
        const url = window.targetUrl || el.getAttribute("href") || el.dataset.url || null;
        window.targetUrl = null; // or "" if you prefer empty string
        return url;
      }
    },
    {
      selector: ".cmp-button",
      getEvent: () => "ClickTracking",
      getName: el =>
        el.querySelector(".cmp-button__text")?.innerText.trim() ||
        el.innerText.trim(),
      getRegion: el => "Call to Action",
      getDestinationPage: el => el.getAttribute("href") || el.dataset.url
    },
    {
      selector: "a",
      getEvent: () => "ClickTracking",
      getName: (el) => (el.innerText || el.textContent || "").trim() || "",
      getRegion: (el) => {
        const href = (el.getAttribute("href") || "").trim().toLowerCase();
        if (href.startsWith("mailto:")) return "Email Link";
        if (href.startsWith("tel:")) return "Phone Link";
        return "Call to Action";
      },
      getDestinationPage: (el) => el.getAttribute("href") || el.dataset.url
    }
  ];

  function pushToAnalyticsLayer(payload) {
    if (window.adobeDataLayer && typeof window.adobeDataLayer.push === "function") {
      window.adobeDataLayer.push(payload);
      return;
    }
    window.dataLayer = window.dataLayer || [];
    window.dataLayer.push(payload);
  }

  function safe(fn, fallback = null) {
    try { return fn(); } catch (_) { return fallback; }
  }

  function buildPayload(rule, el) {
    const linkPageRaw = rule.getLinkPage ? safe(() => rule.getLinkPage(el), null) : null;
    const linkPageValue = (linkPageRaw ?? "").toString().trim();

    return {
      event: safe(() => rule.getEvent?.(el), "ClickTracking"),
      linkName: safe(() => rule.getName?.(el), null),
      linkRegion: safe(() => rule.getRegion?.(el), null),
      destinationPage: rule.getDestinationPage ? safe(() => rule.getDestinationPage(el), null) : null,
      ...(linkPageValue ? { linkPage: linkPageValue } : {})
    };
  }

  function findRuleMatch(target) {
    for (const rule of RULES) {
      const matchedEl = target.closest(rule.selector);
      if (matchedEl) return { rule, el: matchedEl };
    }
    return null;
  }

  function shouldTrack(el) {
    if (!el) return false;
    if (el.closest(`[${SKIP_ATTR}]`)) return false;
    if (el[TRACKED_FLAG]) return false;
    return true;
  }

  function onClickCapture(e) {
    if (e.button !== 0) return;
    if (e.defaultPrevented) return;

    const target = e.target instanceof Element ? e.target : null;
    if (!target) return;

    const match = findRuleMatch(target);
    if (!match) return;

    const { rule, el } = match;
    if (!shouldTrack(el)) return;

    el[TRACKED_FLAG] = true;

    const payload = buildPayload(rule, el);

    try {
      pushToAnalyticsLayer(payload);
    } catch (err) {
      console.warn("Analytics push failed:", err);
    }

    queueMicrotask(() => {
      try { delete el[TRACKED_FLAG]; } catch (_) { el[TRACKED_FLAG] = false; }
    });
  }

  document.addEventListener("click", onClickCapture, true);
})();
